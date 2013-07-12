(ns billiards.state.global)

(def is-free-ball (atom false))
(def player-one-turn (atom true))
(def game-ended (atom 0))

(defn reset-global []
  (reset! is-free-ball false)
  (reset! player-one-turn true)
  (reset! game-ended 0))
