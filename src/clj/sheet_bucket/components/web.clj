(ns sheet-bucket.components.web
  (:use [org.httpkit.server :only [run-server]])
  (:require [sheet-bucket.routes :as routes]
            [sheet-bucket.utils :refer [parse-int]]
            [com.stuartsierra.component :as c]
            [muuntaja.core :as m]
            [muuntaja.middleware :refer [wrap-format wrap-params]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn- wrap-db [handler db]
  (fn [req] (handler (assoc req :db-conn (:conn db)))))

(def app-defaults
  (assoc api-defaults
    :params {:urlencoded true
             :multipart  true
             :nested     true
             :keywordize true}
    :static {:resources "public"}))

(def muuntaja-options
  (m/create (assoc m/default-options :default-format "application/transit+json")))

(defn make-handler [db]
  (-> #'routes/app-routes
    (wrap-db db)
    (wrap-params)
    (wrap-format muuntaja-options)
    (wrap-defaults app-defaults)
    wrap-reload))

(defrecord Web [port db]
  c/Lifecycle
  (start [this]
    (if (:server this)
      this
      (let [server (run-server (make-handler db) {:port (parse-int port)})
            options {:port (parse-int port)}]
        (println (format "Starting web server on port %s..." port))
        (assoc this :stop-server-fn server))))
  (stop [this]
    (if (:stop-server-fn this)
      (do
        (println "Stopping web server...")
        ((:stop-server-fn this))
        (dissoc this :stop-server-fn))
      this)))

(defn component [port]
  (map->Web {:port port}))
