(ns billiards.gui.main
  (use [seesaw core color graphics behave])
  (:use [billiards.globals]))

(defn draw-board [c g]
  (draw g (rect
            (- board-start-x border-size)
            (- board-start-y border-size)
            (+ board-width (* 2 border-size))
            (+ board-height (* 2 border-size)))
    (style :background :brown))
  (draw g (rect board-start-x board-start-y board-width board-height) (style :background :green)))

(defn draw-balls [c g]
  (doseq [ball @balls]
    (let [x (:x @ball)
          y (:y @ball)
          color (:color @ball)]
      (draw g (circle (+ board-start-x x) (+ board-start-y y) ball-size) (style :background color)))))

(defn draw-table [c g]
  (dosync
    (draw-board c g)
    (draw-balls c g)))


(defn make-panel []
  (border-panel
    :center (canvas :paint draw-table
              :class :world
              :background :black)
    :border 5))

(defn make-frame []
  (frame :title   "Billiards"
    :size    [window-width :by window-height]
    :content (make-panel)))


(defn redisplay [root]
  (config! (select root [:.world]) :paint draw-table))

(defn start-game []
  (let [frame (make-frame)]
    (reset! main-frame frame)
    (native!)
    (config! frame :content (make-panel))
    (show! frame)))
