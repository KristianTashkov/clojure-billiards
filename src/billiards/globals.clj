(ns billiards.globals)

(def main-frame (atom {}))
(def borders (atom {}))
(def balls (atom {}))
(def cue-angle (atom 0))
(def cue-power (atom 0))
(def is-playing (atom true))

(def window-width 800)
(def window-height 600)
(def board-width 650)
(def board-height 325)
(def border-size 20)
(def ball-size 9)
(def ball-max-speed 100.0)
(def ball-max-power 5.0)
(def cushion-effect 0.95)
(def friction 0.005)
(def answer 24)

(def board-start-x
  ( / (- window-width (+ board-width (* 2 border-size))) 2))

(def board-start-y
  ( / (- window-height (+ board-height (* 2 border-size))) 2))

(defn get-white-ball []
   (first (filter #(= :white (:color @%)) @balls)))
