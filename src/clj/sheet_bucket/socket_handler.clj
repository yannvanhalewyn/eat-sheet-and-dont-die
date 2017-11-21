(ns sheet-bucket.socket-handler
  (:require [clj-stacktrace.repl :refer [pst-str]]
            [taoensso.timbre :as timbre]))

(defmulti socket-handler "Multimethod for handling socket messages" :id)

(defmethod socket-handler :default [{:keys [event ?reply-fn]}]
  (timbre/debug "Unhandled event" event)
  (when-let [reply ?reply-fn]
    (reply {:unmatched-event-echo event})))

(defn- wrap-stacktrace
  "Wrap a handler such that exceptions are caught and a helpful debugging
   response is returned."
  [handler]
  (fn [msg]
    (try
      (handler msg)
      (catch Exception e
        (let [res {:error (pst-str e)}
              reply (or (:?reply-fn msg) #'timbre/error)]
          (reply res))))))

(defn- wrap-reply
  "Wraps a handler and calls the reply fn if any"
  [handler]
  (fn [msg]
    (if-let [reply (:?reply-fn msg)]
      (reply (handler msg))
      (handler msg))))

(def handler (-> socket-handler wrap-reply wrap-stacktrace))
