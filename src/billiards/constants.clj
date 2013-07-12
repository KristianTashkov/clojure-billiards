(ns billiards.constants)

(def window-width 800.0)
(def window-height 600.0)
(def board-width 650.0)
(def board-height 325.0)
(def board-padding 13.0)
(def border-size 20.0)
(def outside-border-size 22)
(def ball-size 10.0)
(def pocket-size 20)
(def ball-max-speed 12.5)
(def ball-max-power 6.5)
(def cushion-effect 0.85)
(def friction-counter-start 2)
(def friction 0.015)
(def friction-step (/ friction friction-counter-start))
(def corner-pocket-angle-percent 1.5)
(def middle-pocket-angle-percent 0.7)

(def board-start-x
  (/ (- window-width board-width) 2))

(def board-start-y
  (/ (- window-height board-height) 2))

(def long-side-length
  (/ (- board-width (* 3 pocket-size)) 2))

(def short-side-length
  (- board-height (* 2 pocket-size)))
