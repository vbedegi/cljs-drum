(ns cljs-drum.cursor)

(defn render-cursor-point [{:keys [playback-position playback] :as model} index]
  (let [active-class (if (and
                           (= playback-position index)
                           (= playback :playing))
                       "_active"
                       "")]
    [:li {:key   index
          :class active-class}]))

(defn render-cursor [{:keys [playback-sequence] :as model}]
  [:ul.cursor
   (map #(render-cursor-point model %) (range (count playback-sequence)))])
