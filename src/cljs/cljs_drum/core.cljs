(ns cljs-drum.core
  (:require-macros [cljs.core.match :refer [match]])
  (:require [cljs.core.match :as m]
            [re-alm.core :as ra]
            [re-alm.io.time :as rat]
            [cljs-drum.cursor :as c]
            [cljs-drum.tracks :as tr]
            [cljs-drum.playback-controls :as pc]
            [cljs-drum.credits :as cr]
            [cljs-drum.utils :as utils]))

(defn init-sequence []
  (vec (repeat 16 :off)))

(defn init-hat []
  {:sequence (init-sequence)
   :name     "Hat"
   :clip     "hat"
   :active?  true})

(defn init-snare []
  {:sequence (init-sequence)
   :name     "Snare"
   :clip     "snare"
   :active?  false})

(defn init-kick []
  {:sequence (init-sequence)
   :name     "Kick"
   :clip     "kick"
   :active?  false})

(defn init-drum []
  {:playback          :stopped
   :playback-position 16
   :playback-sequence (vec (repeat 16 #{}))
   :bpm               108
   :tracks            [(init-hat)
                       (init-snare)
                       (init-kick)]})

(defn- render-drum [model dispatch]
  [:div.step-sequencer
   [c/render-cursor model]
   [tr/render-track-selector model dispatch]
   [tr/render-tracks model dispatch]
   [:div.control-panel
    [pc/render-playback-controls model dispatch]
    [cr/render-credits]]])

(defrecord SendClipsFx [clips]
  ra/IEffect
  (execute [this dispatch]
    (.log js/console (str clips))
    ))

(defn send-clips-fx [clips]
  (->SendClipsFx clips))

(defn set-nested-array [index f xs]
  (update xs index f))

(defn update-track-step [track-index step-index tracks]
  (let [toggle-step (fn [step]
                      (if (= step :off)
                        :on
                        :off))
        new-sequence (fn [sequence]
                       (set-nested-array step-index toggle-step sequence))
        new-track (fn [track]
                    (update track :sequence new-sequence))]
    (set-nested-array track-index new-track tracks)))

(defn update-playback-sequence [step-index track-clip playback-sequence]
  (let [update-sequence (fn [track-clip sequence]
                          (if (contains? sequence track-clip)
                            (disj sequence track-clip)
                            (conj sequence track-clip)))]
    (set-nested-array step-index #(update-sequence track-clip %) playback-sequence)))

(defn- update-drum [model msg]
  (match msg
         :start-playback
         (assoc model :playback :playing)

         :stop-playback
         (assoc model :playback :stopped
                      :playback-position 16)

         [:update-playback-position _]
         (let [playback-position (:playback-position model)
               new-position (if (>= playback-position 15)
                              0
                              (inc playback-position))
               playback-sequence (:playback-sequence model)
               step-clips (get playback-sequence new-position #{})]
           (ra/with-fx
             (assoc model :playback-position new-position)
             (send-clips-fx step-clips)))

         [:update-bpm bpm]
         (let [new-bpm (utils/with-default (:bpm model) #(utils/parse-int bpm))]
           (assoc model :bpm new-bpm))

         [:toggle-step track-index track-clip step-index]
         (assoc model :tracks (update-track-step track-index step-index (:tracks model))
                      :playback-sequence (update-playback-sequence step-index track-clip (:playback-sequence model)))

         [:activate-track track-index]
         (let [activate-track (fn [track] (assoc track :active? true))
               deactivate-track (fn [track] (assoc track :active? false))
               new-tracks (->> model
                               :tracks
                               (map deactivate-track)
                               (set-nested-array track-index activate-track))]
           (assoc model :tracks new-tracks))

         _
         model))

(defn bpm-to-milliseconds [bpm]
  (let [seconds-per-minute 60
        milliseconds-per-second 1000
        beats 4]
    (/
      (*
        (/ seconds-per-minute (float bpm))
        milliseconds-per-second)
      beats)))

(defn subscriptions [{:keys [playback bpm] :as model}]
  [(when (= playback :playing)
     (rat/every (bpm-to-milliseconds bpm) :update-playback-position))])

(def drum-component
  {:render        #'render-drum
   :update        #'update-drum
   :subscriptions #'subscriptions})
