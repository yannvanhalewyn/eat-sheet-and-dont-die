(ns sheet-bucket.socket-handler
  (:require [clojure.stacktrace :as st]
            [taoensso.timbre :as timbre]
            [datomic.api :as d]))

(defmulti socket-handler "Multimethod for handling socket messages" :id)

(defmethod socket-handler :default [{:keys [event ?reply-fn]}]
  (timbre/debug "Unhandled event" event)
  (when-let [reply ?reply-fn]
    (reply {:unmatched-event-echo event})))

(defn- ->tx [[e a v t added]]
  [({true :db/add false :db/retract} added) e a v])

(defmethod socket-handler :tx/sync
  [{:keys [?data ring-req ?reply-fn]}]
  (let [tx (map ->tx ?data)
        result (d/transact (:db-conn ring-req) tx)]
    (?reply-fn (:tempids @result))))

(defn- wrap-stacktrace
  "Wrap a handler such that exceptions are caught and a helpful debugging
   response is returned."
  [handler]
  (fn [msg]
    (try
      (handler msg)
      (catch Exception e
        (let [res {:error (with-out-str (st/print-stack-trace e))}
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
