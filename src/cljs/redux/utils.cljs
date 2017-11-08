(ns redux.utils)

(defn create-selector
  [selectors f]
  (let [cache (atom [])]
    (fn [state]
      (let [args (map #(% state) selectors)
            [cached-args cached-result] @cache]
        (if (= args cached-args)
          cached-result
          (let [result (apply f args)]
            (reset! cache [args result])
            result))))))
