(ns billiards.gui.main
  (:use
    [seesaw core color graphics behave keymap]
    [billiards.globals]
    [billiards.physics.geometry]
    [billiards.gui.actions]))

(defn polygon-rectangle-from-middle-points [a b vect width]
  (let [distVect (product-vector-scalar vect width)
        point1 (sum-pair a distVect)
        point2 (sum-pair a (reverse-vector distVect))
        point3 (sum-pair b (reverse-vector distVect))
        point4 (sum-pair b distVect)]
    (polygon point1 point2 point3 point4)))

(defn draw-borders [c g]
  (loop [[a b c d] (take 4 @border-points) other (take-last (- (count @border-points) 4) @border-points)]
    (when (and a b c d)
      (let [a [(+ board-start-x (first a)) (+ board-start-y (second a))]
            b [(+ board-start-x (first b)) (+ board-start-y (second b))]
            c [(+ board-start-x (first c)) (+ board-start-y (second c))]
            d [(+ board-start-x (first d)) (+ board-start-y (second d))]]
        (draw g (polygon a b c d) (style :background :brown))
        (recur (take 4 other) (take-last (- (count other) 4) other))))))

(defn draw-pockets [c g]
  (doseq [pocket @pockets]
    (let [x (first pocket)
          y (second pocket)]
      (draw g (circle (+ board-start-x x) (+ board-start-y y) pocket-size) (style :background :black)))))

(defn draw-board [c g]
  (draw g (rect board-start-x board-start-y board-width board-height)
    (style :background :green))
  (draw-pockets c g)
  (draw-borders c g))

(defn draw-cue [c g]
  (let [ball (get-white-ball)
        ballX (+ board-start-x (:x @ball))
        ballY (+ board-start-y (:y @ball))
        dir (get-perp-of-vector (get-vector-from-angle @cue-angle))
        tip (get-circle-point-from-angle [ballX ballY] (+ 3 ball-size @cue-power) @cue-angle)
        tipEnd (get-circle-point-from-angle [ballX ballY] (+ 10 (+ 3 ball-size @cue-power)) @cue-angle)
        cueEnd (get-circle-point-from-angle [ballX ballY] (+ 250 (+ 3 ball-size @cue-power)) @cue-angle)]
    (draw g (polygon-rectangle-from-middle-points tip tipEnd dir 5) (style :background :white))
    (draw g (polygon-rectangle-from-middle-points tipEnd cueEnd dir 5) (style :background :darkgray))))

(defn draw-balls [c g]
  (doseq [ball @balls]
    (let [x (:x @ball)
          y (:y @ball)
          color (:color @ball)]
      (draw g (circle (+ board-start-x x) (+ board-start-y y) ball-size) (style :background :black))
      (draw g (circle (+ board-start-x x) (+ board-start-y y) (- ball-size 1)) (style :background color)))))

(defn draw-decorations [c g]
  (draw g
    (rect
      (- board-start-x outside-border-size)
      (- board-start-y outside-border-size)
      (+ board-width (* 2 outside-border-size))
      outside-border-size)
    (style :background :brown))
  (draw g
    (rect
      (- board-start-x outside-border-size)
      (+ (- board-height 1) board-start-y)
      (+ board-width (* 2 outside-border-size))
      outside-border-size)
    (style :background :brown))
  (draw g
    (rect
      (+ board-start-x board-width)
      (- board-start-y outside-border-size)
      outside-border-size
      (+ board-height outside-border-size))
    (style :background :brown))
  (draw g
    (rect
      (- board-start-x outside-border-size)
      (- board-start-y outside-border-size)
      outside-border-size
      (+ board-height outside-border-size))
    (style :background :brown)))

(defn draw-table [c g]
  (dosync
    (draw-board c g)
    (draw-balls c g)
    (draw-decorations c g)
    (when @is-playing
      (draw-cue c g))))

(defn redisplay [root]
  (dosync
    (config! (select root [:.world]) :paint draw-table)))

(defn make-panel []
  (border-panel
    :center (canvas :paint draw-table
              :class :world
              :background :aqua)))

(defn make-frame []
  (frame :title   "Billiards"
    :size    [window-width :by window-height]
    :content (make-panel)))


(defn add-bindings [frame]
  (map-key frame "A"
    (fn [e] (move-cue 0.05) (redisplay frame)) :scope :global)
  (map-key frame "D"
    (fn [e] (move-cue -0.05) (redisplay frame)) :scope :global)
  (map-key frame "W"
    (fn [e] (change-power -5) (redisplay frame)) :scope :global)
  (map-key frame "S"
    (fn [e] (change-power 5) (redisplay frame)) :scope :global)
  (map-key frame "F"
    (fn [e] (future (try
                      (shoot (fn [] (redisplay frame)))
                      (catch Exception e (do
                                           (println e)
                                           (.printStackTrace e)))))) :scope :global))

(defn start-game []
  (let [frame (make-frame)]
    (reset! main-frame frame)
    (add-bindings frame)
    (native!)
    (config! frame :content (make-panel))
    (show! frame)))
