(ns cljs-drum.credits)

(defn credit-link [title link]
  [:li
   [:a.credit-link {:href   link
                    :target "_blank"}
    title]])

(defn render-credits []
  [:div.credits
   [:h1.credit-title
    "ClojureScript"
    [:strong "Drum"]]
   [:ul.credit-links
    [:li "Made in ClojureScript + re-alm"]
    [credit-link "Github" "https://github.com/vbedegi/cljs-drum"]
    [credit-link "Twitter" "https://twitter.com/vbedegi"]
    [credit-link "Original" "https://bholtbholt.github.io/step-sequencer/"]]])
