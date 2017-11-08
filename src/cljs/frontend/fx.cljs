(ns frontend.fx
  (:require [re-frame.core :as rf]
            [clojure.data :refer [diff]]))

(def debug-logger
  (rf/->interceptor
    :id :debug-logger
    :after
    (fn [{:keys [effects coeffects] :as context}]
      (let [new-db (:db effects)
            old-db (:db coeffects)
            event (:event coeffects)
            group-name (str "Dispatch: " (first event)
                            (if (= :request-status (first event))
                              (str " (" (:type (second event)) ")")))]
        (.groupCollapsed js/console group-name)
        (.info js/console "%c Event" "color: #03A9F4; font-weight: bold" event)
        (if new-db
          (do
            (.info js/console "%c New DB" "color: #9E9E9E; font-weight: bold" (sort new-db))
            (let [diff (diff old-db new-db)]
              (.info js/console "%c removed" "color: #FF6259; font-weight: bold" (first diff))
              (.info js/console "%c added" "color: #29D042; font-weight: bold" (second diff))))
          (.info js/console "No db changes"))
        (.groupEnd js/console group-name "color: grey"))
      context)))

(defn reg-event-db [id handler]
  (rf/reg-event-db
    id
    (when ^boolean goog.DEBUG debug-logger)
    handler))
