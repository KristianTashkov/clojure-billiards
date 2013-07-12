(ns billiards.state.rules)

(def player-one-color (atom :none))
(def player-two-color (atom :none))
(def players-colors-decided (atom false))
(def commited-foul (atom false))
(def pocketed-ball (atom false))
(def hit-border (atom false))
(def first-collision (atom nil))

(defn reset-rules [full-reset]
  (when full-reset
    (reset! player-one-color :none)
    (reset! player-two-color :none)
    (reset! players-colors-decided false))
  (reset! commited-foul false)
  (reset! pocketed-ball false)
  (reset! hit-border false)
  (reset! first-collision nil))
