(ns sheet-bucket.env-middleware
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn wrap-env-middleware [handler]
  (-> handler wrap-reload wrap-stacktrace))
