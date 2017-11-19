(ns sheet-bucket.env-middleware
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [response status content-type]]
            [clj-stacktrace.repl :refer [pst-str]]))

(defn- wrap-stacktrace
  "Wrap a handler such that exceptions are caught and a helpful debugging
   response is returned."
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (-> (response (pst-str e)) (status 500)
          (content-type "text/plain"))))))

(defn wrap-env-middleware [handler]
  (-> handler wrap-reload wrap-stacktrace))
