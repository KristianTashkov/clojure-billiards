(ns billiards.gui.main
  (use [seesaw core color graphics behave])
  (:use [billiards.core :only [board-width board-height]]))

(declare the-frame)

(defn draw-board [c g]
  (draw g (rect 50 50 board-width board-height) (style :background :green)))

(defn make-panel []
  (border-panel
    :center (canvas :paint draw-board
              :class :world
              :background :black)
    :vgap 5
    :hgap 5
    :border 5))

(defn make-frame []
  (frame :title   "Billiards"
    :size    [800 :by 600]
    :content (make-panel)))


(defn redisplay [root]
  (config! (select root [:.world]) :paint draw-board))

(defonce the-frame (make-frame))

(defn start-game []
  (native!)
  (config! the-frame :content (make-panel))
  (show! the-frame)
  (redisplay the-frame))
