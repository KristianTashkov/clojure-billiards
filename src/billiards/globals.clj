(ns billiards.globals)

(def main-frame (atom {}))
(def borders (atom {}))
(def balls (atom {}))
(def window-width 800)
(def window-height 600)
(def board-width 650)
(def board-height 325)
(def border-size 20)
(def ball-size 10)
(def ball-max-speed 25.0)
(def cushion-effect 0.95)
(def friction 0.05)
(def answer 24)

(def board-start-x
  ( / (- window-width (+ board-width (* 2 border-size))) 2))

(def board-start-y
  ( / (- window-height (+ board-height (* 2 border-size))) 2))
