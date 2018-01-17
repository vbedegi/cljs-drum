(ns cljs-drum.boot
  (:require
    [re-alm.boot :as boot]
    [re-alm.core :as ra]
    [cljs-drum.core :as core]))

(enable-console-print!)

(defn ^:export init []
  (boot/boot
    (.getElementById js/document "app")
    core/drum-component
    (core/init-drum)
    (-> ra/default-handler
        ra/wrap-log)))


