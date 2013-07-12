(ns billiards.state.gui)

(def is-playing (atom true))
(def painting-future (atom nil))
(def player-one-pocketed (atom []))
(def player-two-pocketed (atom []))

(defn reset-gui []
  (reset! is-playing true)
  (reset! player-one-pocketed [])
  (reset! player-two-pocketed []))
