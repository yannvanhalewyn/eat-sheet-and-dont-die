(ns sheet-bucket.components.web
  (:require [sheet-bucket.routes :as routes]
            [sheet-bucket.utils :refer [parse-int]]
            [com.stuartsierra.component :as c]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]))

(defn- wrap-db [handler db]
  (fn [req] (handler (assoc req :db db))))

(def app-defaults
  (assoc api-defaults
    :params {:urlencoded true
             :multipart  true
             :nested     true
             :keywordize true}
    :static {:resources "public"}))

(defn make-handler [db]
  (-> #'routes/app-routes
      (wrap-db db)
      (wrap-params)
      (wrap-defaults app-defaults)))

(defrecord Web [port db]
  c/Lifecycle
  (start [this]
    (if (:server this)
      this
      (let [handler (make-handler db)
            server-options {:port (parse-int port) :join? false}]
        (println (format "Starting web server on port %s..." port))
        (assoc this :server (jetty/run-jetty handler server-options)))))
  (stop [this]
    (if (:server this)
      (do
        (println "Stopping web server...")
        (.stop (:server this))
        (dissoc this :server))
      this)))

(defn component [port]
  (map->Web {:port port}))
