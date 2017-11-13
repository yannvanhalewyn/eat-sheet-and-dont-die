(ns frontend.fx
  (:require [frontend.http :as http]
            [frontend.selectors :as sel]
            [frontend.router :as router]
            [shared.diffp :refer [diffp]]
            [re-frame.core :as rf]
            [clojure.data :refer [diff]]
            [goog.string :refer [format]]))

(rf/reg-fx :remote http/request-fx)

(def debug-logger
  (rf/->interceptor
    :id :debug-logger
    :after
    (fn [{:keys [effects coeffects] :as context}]
      (let [new-db (:db effects)
            old-db (:db coeffects)
            event (:event coeffects)
            group-name (str "Dispatch: " (first event)
                         (if (= "remote" (namespace (first event)))
                           (str " (" (second event) ")")))]
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

(def sync-browser-url
  (rf/->interceptor
    :id :browser-url
    :after
    (fn [context]
      (let [prev-route (get-in context [:coeffects :db :db/active-route])
            next-route (get-in context [:effects :db :db/active-route])]
        (if (and (not= prev-route next-route)
              prev-route
              next-route)
          (router/redirect-to (router/path-for next-route))))
      context)))

(def sheet-tx-sync
  (rf/->interceptor
    :id :sheet-tx-sync
    :after
    (fn [{:keys [effects coeffects] :as context}]
      (let [event (get-in coeffects [:event 0])]
        (if-not (= "sheet" (namespace event))
          context
          (let [new-sheet (sel/sheet (:db effects))
                old-sheet (sel/sheet (:db coeffects))]
            (if (= new-sheet old-sheet)
              context
              (assoc-in context [:effects :remote :sync-sheet]
                {:path (format "/api/sheets/%s" (:db/id new-sheet))
                 :method :patch
                 :params {:tx (diffp old-sheet new-sheet :db/id)}}))))))))

(def EVENT_MIDDLEWARE
  [sync-browser-url sheet-tx-sync (when ^boolen goog.DEBUG debug-logger)])

(defn reg-event-fx [id & args]
  (apply rf/reg-event-fx id EVENT_MIDDLEWARE args))

(defn reg-event-db [id handler]
  (rf/reg-event-db id EVENT_MIDDLEWARE handler))
