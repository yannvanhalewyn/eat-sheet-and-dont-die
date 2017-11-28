(ns umuses.env-middleware
  (:require [ring.middleware.gzip :refer [wrap-gzip]]))

(defn wrap-env-middleware [handler]
  (wrap-gzip handler))
