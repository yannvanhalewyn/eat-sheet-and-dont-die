(ns umuses.env-middleware
  (:require [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.defaults :refer [wrap-defaults]]))

(defn wrap-ssl-redirect [handler]
  (wrap-defaults handler {:proxy true
                          :security {:ssl-redirect true
                                     :hsts true}}))

(defn wrap-env-middleware [handler]
  (-> handler
    wrap-gzip
    wrap-ssl-redirect))
