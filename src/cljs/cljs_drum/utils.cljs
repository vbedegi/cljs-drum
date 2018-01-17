(ns cljs-drum.utils)

(defn parse-int [s]
  (js/parseInt s))

(defn with-default [default f]
  (try
    (f)
    (catch :default e
      default)))