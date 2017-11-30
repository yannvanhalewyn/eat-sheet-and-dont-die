(ns umuses.env-middleware
  (:require [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.defaults :refer [wrap-defaults]]))

(defn wrap-ssl-redirect [handler]
  (wrap-defaults handler {:proxy true
                          :security {:ssl-redirect true
                                     :hsts true}}))

(defn wrap-health-check
  "Will respond with a status 200 on the path /health. It's important
  to wrap this around SSL redirect, since EB's health check sends a
  HTTP GET and expects a 200 response, not a 301 redirect."
  [handler]
  (fn [req]
    (if (= (:uri req) "/health")
      {:status 200}
      (handler req))))

(defn wrap-env-middleware [handler]
  (-> handler
    wrap-gzip
    wrap-ssl-redirect
    wrap-health-check))
