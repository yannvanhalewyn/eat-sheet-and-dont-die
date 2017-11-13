(ns frontend.models.chord-test
  (:require [frontend.models.chord :as sut :refer [parse]]
            [goog.string :refer [format]]
            [test.util :refer-macros [deftest-gen]]
            [cljs.test :refer-macros [deftest is testing]]))

(deftest-gen chord-parser `sut/parse)
