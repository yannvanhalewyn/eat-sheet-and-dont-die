(ns sheet-bucket.db
  (:require [datomic.client :as d]
            [clojure.core.async :refer [<!!]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def conn (<!! (d/connect {:db-name "hello"
                           :account-id d/PRO_ACCOUNT
                           :endpoint "localhost:8998"
                           :secret "secret"
                           :access-key "key"
                           :service "peer-server"
                           :region "none"})))

(defn- read-schema! []
  (->> (io/file "resources/schema.edn") slurp edn/read-string))

(defn transact! [conn tx-data]
  (<!! (d/transact conn {:tx-data tx-data})))

(defn load-schema! [conn]
  (transact! conn (read-schema!)))

(comment
  (<!! (d/q conn
            {:query '[:find ?u
                      :where [?u :user/email]]
             :args [(d/db conn)]}))

  (<!! (d/transact conn {:tx-data
                         [{:db/id "s"
                           :sheet/title "Skaterboy"
                           :sheet/artist "Avril Lavigne"
                           :sheet/data "[[]]"}
                          [:db/add [:user/email "yann_vanhalewyn@hotmail.com"]
                           :playlist/sheets "s"]]}))

  (<!! (d/pull (d/db conn)
               {:eid [:user/email "yann_vanhalewyn@hotmail.com"]
                :selector '[:user/first-name {:playlist/sheets [*]}]}))

  (<!! (d/pull (d/db conn)
               {:eid 17592186045446
                :selector '[:sheet/title {:playlist/_sheets [*]}]}))

  (transact! conn
             [{:sheet/sections
               [{:section/name "Intro"
                 :section/bars [{:bar/chords [{:chord/value "A-"}
                                              {:chord/value "C"}]}
                                {:bar/chords [{:chord/value "D-"}]}]}
                {:section/name "Verse"
                 :section/bars [{:bar/chords [{:chord/value "C7"}
                                              {:chord/value "B-"}]}]}]}]))
