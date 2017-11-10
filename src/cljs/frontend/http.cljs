(ns frontend.http
  (:require [ajax.core :as ajax]
            [re-frame.core :refer [dispatch]]))

(defn- response-handler [key [success response]]
  (dispatch [(keyword :remote (if success "success" "failure")) key response]))

(defn request [{:keys [path method params]} handler]
  (ajax/ajax-request
    {:uri path
     :method (or method :get)
     :handler handler
     :format (ajax/transit-request-format)
     :response-format (ajax/transit-response-format)
     :params params}))

(defn request-fx [requests]
  (doseq [[key payload] requests]
    (dispatch [:remote/request key payload])
    (request payload (partial response-handler key))))
