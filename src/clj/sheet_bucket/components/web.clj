(ns sheet-bucket.components.web
  (:require [com.stuartsierra.component :as c]
            [muuntaja.core :as m]
            [muuntaja.middleware :refer [wrap-format wrap-params]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.defaults :refer [api-defaults
                                              wrap-defaults]]
            [sheet-bucket.env-middleware :refer [wrap-env-middleware]]
            [sheet-bucket.routes :as routes]
            [sheet-bucket.utils :refer [parse-int]]
            [taoensso.timbre :as timbre]))

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

(defn make-handler [db channel-sockets]
  (-> #'routes/app-routes
    (routes/wrap-chsk-routes channel-sockets)
    (wrap-db db)
    wrap-anti-forgery
    wrap-session
    wrap-params
    (wrap-format muuntaja-options)
    (wrap-defaults app-defaults)
    wrap-env-middleware))

(defrecord Web [port db channel-sockets]
  c/Lifecycle
  (start [this]
    (if (:server this)
      this
      (let [port (parse-int port)]
        (timbre/infof "Starting web server on port %s..." port)
        (assoc this :stop-server-fn
               (run-server (make-handler db channel-sockets) {:port port})))))
  (stop [this]
    (timbre/info "Stopping web server...")
    (if-let [stop (:stop-server-fn this)]
      (stop)
      (timbre/error "No stop-fn found for Web component. Doing nothing."))
    (dissoc this :stop-server-fn)))

(defn component [port]
  (map->Web {:port port}))
