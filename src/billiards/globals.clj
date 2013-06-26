(ns billiards.globals)

(def main-frame (atom {}))
(def borders (atom {}))
(def pockets (atom {}))
(def balls (atom {}))
(def cue-angle (atom 0))
(def cue-power (atom 0))
(def is-playing (atom true))

(def window-width 800)
(def window-height 600)
(def board-width 650)
(def board-height 325)
(def board-padding 10)
(def border-size 20)
(def ball-size 9.0)
(def pocket-size 12.0)
(def ball-max-speed 10.0)
(def ball-max-power 6.5)
(def cushion-effect 0.85)
(def friction-counter-start 2)
(def friction 0.015)
(def friction-step (/ friction friction-counter-start))

(def board-start-x
  (/ (- window-width board-width) 2))

(def board-start-y
  (/ (- window-height board-height) 2))

(defn get-white-ball []
  (first (filter #(= :white (:color @%)) @balls)))
