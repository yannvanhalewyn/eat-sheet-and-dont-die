(ns shared.datsync
  #?(:clj (:require [datomic.api :as d])))

;; Transaction normalisation
;; =========================

(defmulti normalize-tx-form #(if (map? %) :tx-form/map :tx-form/list))

(defmethod normalize-tx-form :tx-form/list
  [[op e a v]]
  "Takes a transaction list form and normalizes it's values, such that
  any maps or collections are expanded into list forms and concat'd
  onto the argument list form."
  (cond (map? v) (conj (normalize-tx-form v) [op e a (:db/id v)])
        :else [[op e a v]]))

(defmethod normalize-tx-form :tx-form/map
  [m]
  "Takes a transaction map form and translates it into a collection of list forms"
  (if-let [id (:db/id m)]
    (mapcat
      (fn [[k v]] (normalize-tx-form [:db/add id k v]))
      (dissoc m :db/id))
    (let [message "Tx map form doesn't have a :db/id"]
      (throw
        #?(:clj  (IllegalArgumentException. message)
           :cljs (js/Error. message))))))

(defn normalize-tx
  "Takes a list of transactions and returns a list of normalized
  transaction forms like [op e a v], eg: [:db/add fred :likes
  \"bananas\"]"
  [txes]
  (into (empty txes)
    (mapcat normalize-tx-form txes)))

(defn datoms->tx
  [datoms]
  (map
    (fn [[e a v _ added?]] [(if added? :db/add :db/retract) e a v])
    datoms))

#?(:clj
   (defn datom->vec [db d]
     [(.e d) (:db/ident (d/entity db (.a d))) (.v d) (.tx d) (.added d)]))
