(ns test.util
  (:require [clojure.test]
            [clojure.spec.alpha]
            [clojure.spec.test.alpha]
            [clojure.string :as str]))

(defmacro deftest-gen
  ([name sym-or-syms] `(deftest-gen ~name ~sym-or-syms nil))
  ([name sym-or-syms opts]
   `(def ~(vary-meta name assoc
            :test `(fn []
                     (.log js/console "Running generation tests for" ~sym-or-syms)
                     (.time js/console "Time")
                     (let [check-results# (cljs.spec.test.alpha/check ~sym-or-syms ~opts)
                           checks-passed?# (every? nil? (map :failure check-results#))]
                       (.timeEnd js/console "Time")
                       (if checks-passed?#
                         (cljs.test/do-report {:type    :pass
                                               :message (str "Generative tests pass for "
                                                          (str/join ", " (map :sym check-results#)))})
                         (doseq [failed-check# (filter :failure check-results#)
                                 :let [r# (cljs.spec.test.alpha/abbrev-result failed-check#)
                                       failure# (:failure r#)]]
                           (cljs.test/do-report
                             {:type     :fail
                              :message  (with-out-str (cljs.spec.alpha/explain-out failure#))
                              :expected [(->> r# :spec rest (apply hash-map) :ret)]
                              :actual   (:cljs.spec.test.alpha/val failure#)})))
                       checks-passed?#)))
      (fn [] (cljs.test/test-var (var ~name))))))
