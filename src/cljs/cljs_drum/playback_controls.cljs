(ns cljs-drum.playback-controls)

(defn render-playback-button [{:keys [playback] :as model} dispatch]
  (let [toggle-playback (if (= playback :stopped)
                          :start-playback
                          :stop-playback)
        button-class (if (= playback :playing)
                       "_playing"
                       "_stopped")]
    [:button.playback-button
     {:class    button-class
      :on-click #(dispatch toggle-playback)}]))

(defn render-bpm [model dispatch]
  [:input.bpm-input
   {:value     (:bpm model)
    :maxlength 3
    :type      "number"
    :min       60
    :max       300
    :on-change #(dispatch [:update-bpm (.-value (.-target %))])}])

(defn render-playback-controls [model dispatch]
  [:div.playback-controls
   [render-playback-button model dispatch]
   [render-bpm model dispatch]])