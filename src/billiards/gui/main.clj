(ns billiards.gui.main
  (use [seesaw core color graphics behave]))

(declare the-frame)
(def window-width 800)
(def window-height 600)
(def board-width 650)
(def board-height 325)
(def border-size 20)

(def board-start-x
  ( / (- window-width (+ board-width (* 2 border-size))) 2))

(def board-start-y
  ( / (- window-height (+ board-height (* 2 border-size))) 2))

(defn draw-board [c g]
  (draw g (rect
            (- board-start-x border-size)
            (- board-start-y border-size)
            (+ board-width (* 2 border-size))
            (+ board-height (* 2 border-size)))
    (style :background :brown))
  (draw g (rect board-start-x board-start-y board-width board-height) (style :background :green)))

(defn make-panel []
  (border-panel
    :center (canvas :paint draw-board
              :class :world
              :background :black)
    :border 5))

(defn make-frame []
  (frame :title   "Billiards"
    :size    [window-width :by window-height]
    :content (make-panel)))


(defn redisplay [root]
  (config! (select root [:.world]) :paint draw-board))

(defonce the-frame (make-frame))

(defn start-game []
  (native!)
  (config! the-frame :content (make-panel))
  (show! the-frame)
  (redisplay the-frame))
