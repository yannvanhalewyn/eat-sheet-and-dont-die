(ns umuses.env-middleware
  (:require [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.defaults :refer [wrap-defaults]]))

(defn- wrap-health-check
  "Will respond with a status 200 for user agents starting with
  \"ELB-Health\" (like \"ELB-HealthChecker/2.0\"). It's important
  to wrap this around SSL redirect, since EB's health check sends a
  HTTP GET and expects a 200 response, not a 301 redirect."
  [handler]
  (fn [req]
    (if (.startsWith (get-in req [:headers "user-agent"]) "ELB-Health")
      {:status 200}
      (handler req))))

(defn- wrap-ssl-redirect [handler]
  (-> handler
    (wrap-defaults {:proxy true
                    :security {:ssl-redirect true :hsts true}})
    wrap-health-check))

(defn wrap-env-middleware [handler]
  (-> handler
    wrap-gzip
    wrap-ssl-redirect))
