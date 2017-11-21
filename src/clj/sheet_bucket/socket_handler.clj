(ns sheet-bucket.socket-handler
  (:require [sheet-bucket.controllers.session :as session]
            [sheet-bucket.models.user :as user]
            [sheet-bucket.models.sheet :as sheet]
            [sheet-bucket.controllers.sheets :as sheets]
            [clj-stacktrace.repl :refer [pst-str]]))

(defn- event-handler* [{:as msg :keys [connected-uids uid send-fn
                                       ring-req client-id id ?data ring-req]}]
  (let [db-conn (:db-conn ring-req)
        reply (:?reply-fn msg)]
    (case id
      :users/me (reply (session/show db-conn))
      :sheets/index (reply (user/sheets db-conn (:user-id ?data)))
      :sheets/show (reply (sheet/find db-conn ?data))
      :sheets/create (reply (sheet/create! db-conn ?data))
      :sheets/update (reply (sheets/update db-conn ?data))
      :sheets/destroy (reply (sheets/destroy db-conn ?data))
      (println id ?data uid client-id))))

(defn- wrap-stacktrace
  "Wrap a handler such that exceptions are caught and a helpful debugging
   response is returned."
  [handler]
  (fn [msg]
    (try
      (handler msg)
      (catch Exception e
        (let [res {:error (pst-str e)}
              reply (or (:?reply-fn msg) println)]
          (reply res))))))

(def event-handler (wrap-stacktrace event-handler*))
