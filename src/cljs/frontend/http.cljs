(ns frontend.http
  (:require [ajax.core :as ajax]
            [re-frame.core :refer [dispatch]]))

(defn- response-handler [key [success response]]
  (dispatch [(keyword :remote (if success "success" "failure")) key response]))

(defn request [{:keys [path method]} handler]
  (ajax/ajax-request
    {:uri path
     :method (or method :get)
     :handler handler
     :format (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})}))

(defn request-fx [requests]
  (doall
    (for [[key payload] requests]
      (do
        (dispatch [:remote/request key payload])
        (request payload (partial response-handler key))))))
