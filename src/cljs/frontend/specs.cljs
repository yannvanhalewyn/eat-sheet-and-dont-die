(ns frontend.specs
  (:require [shared.specs :as specs]
            [frontend.router :as router]
            [cljs.spec.alpha :as s]))

;; Routes
;; ======

(s/def :route/handler (set (map :handler router/ROUTES)))
(s/def :route/params (s/nilable map?))
(s/def :route/query-params (s/nilable map?))
(s/def ::route (s/keys :req [:route/handler]
                 :opt [:route/params :route/query-params]))

;; Selection
;; =========

(s/def :selection/type #{:selection/chord :selection/attachment})
(s/def :selection/id :db/id)
(s/def ::selection (s/keys :req [:selection/type :selection/id]))

;; Modal
;; =====
(s/def :modal/key #{:modal/time-signature})
(s/def :modal/props (s/nilable map?))
(s/def ::modal (s/keys :req [:modal/key :modal/props]))

;; App db
;; ======

(s/def :db/current-user (s/nilable ::specs/user))
(s/def :db/active-route ::route)
(s/def :db/selection (s/nilable ::selection))
(s/def :db/sheets (s/nilable (s/coll-of ::specs/sheet)))
(s/def :db/modal (s/nilable ::modal))

(s/def ::app-db (s/keys :req [:db/active-route
                              :db/current-user
                              :db/modal
                              :db/selection
                              :db/sheets]))
