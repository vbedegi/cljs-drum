(ns cljs-drum.tracks)

(defn render-step [track-index track-clip step-index step dispatch]
  [:button.step
   {:key      step-index
    :class    (when (= step :on) "_active")
    :on-click #(dispatch [:toggle-step track-index track-clip step-index])}])

(defn render-sequence [track-index {:keys [clip sequence] :as track} dispatch]
  (map #(render-step track-index clip %1 %2 dispatch) (range) sequence))

(defn render-track [track-index track dispatch]
  [:div.track
   {:key   track-index
    :class (if (:active? track) "_active" "_hidden")}
   [:p.track-title (:name track)]
   [:div.track-sequence
    (render-sequence track-index track dispatch)]])

(defn render-tracks [{:keys [tracks] :as model} dispatch]
  [:div.tracks
   (map #(render-track %1 %2 dispatch) (range) tracks)])

(defn render-track-button [track-index track dispatch]
  [:button.selector-button
   {:key track-index
    :class    (when (:active? track) "_active")
    :on-click #(dispatch [:activate-track track-index])}
   (:name track)])

(defn render-track-selector [{:keys [tracks] :as model} dispatch]
  [:div.track-selector
   (map #(render-track-button %1 %2 dispatch) (range) tracks)])

