(ns frontend.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub :sheet (fn [db] (:sheet db)))
(reg-sub :selected (fn [db] (:selected db)))
