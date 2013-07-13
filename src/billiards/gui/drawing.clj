(ns billiards.gui.drawing
  (:use
    [seesaw core graphics color]
    [billiards.physics.geometry]
    [billiards.state.board]
    [billiards.state.global]
    [billiards.state.gui]
    [billiards.constants]
    [billiards.utilities]))

(defn polygon-rectangle-from-middle-points [a b vect width]
  (let [dist-vect (product-vect-scalar vect width)
        point1 (sum-pair a dist-vect)
        point2 (sum-pair a (reverse-vect dist-vect))
        point3 (sum-pair b (reverse-vect dist-vect))
        point4 (sum-pair b dist-vect)]
    (polygon point1 point2 point3 point4)))

(defn draw-borders [g]
  (loop [[a b c d] (take 4 @border-points) other (take-last (- (count @border-points) 4) @border-points)]
    (when (and a b c d)
      (let [a [(+ board-start-x (first a)) (+ board-start-y (second a))]
            b [(+ board-start-x (first b)) (+ board-start-y (second b))]
            c [(+ board-start-x (first c)) (+ board-start-y (second c))]
            d [(+ board-start-x (first d)) (+ board-start-y (second d))]]
        (draw g (polygon a b c d) (style :background :brown  :stroke 1 :foreground :black))
        (recur (take 4 other) (take-last (- (count other) 4) other))))))

(defn draw-pockets [g]
  (doseq [pocket @pockets]
    (let [x (first pocket)
          y (second pocket)]
      (draw g (circle (+ board-start-x x) (+ board-start-y y) pocket-size) (style :background :black)))))

(defn draw-board [g]
  (draw g (rect
            (- (- board-start-x outside-border-size) 1)
            (- (- board-start-y outside-border-size) 1)
            (+ (+ board-width 2) (* 2 outside-border-size))
            (+ (+ board-height 1) (* 2 outside-border-size)))
    (style :background :black))
  (draw g (rect board-start-x board-start-y board-width board-height)
    (style :background :green))
  (draw-pockets g)
  (draw-borders g))

(defn draw-cue [g]
  (let [ball (get-white-ball)
        ball-x (+ board-start-x (:x @ball))
        ball-y (+ board-start-y (:y @ball))
        dir (get-perpendicular-of-vect (get-vect-from-angle @cue-angle))
        tip (get-circle-point-from-angle [ball-x ball-y] (+ 3 ball-size @cue-power) @cue-angle)
        tip-end (get-circle-point-from-angle [ball-x ball-y] (+ 10 (+ 3 ball-size @cue-power)) @cue-angle)
        cue-end (get-circle-point-from-angle [ball-x ball-y] (+ 250 (+ 3 ball-size @cue-power)) @cue-angle)]
    (draw g (polygon-rectangle-from-middle-points tip tip-end dir 5) (style :background :white))
    (draw g (polygon-rectangle-from-middle-points tip-end cue-end dir 5) (style :background :darkgray))))

(defn draw-balls [g]
  (doseq [ball @balls]
    (let [x (:x @ball)
          y (:y @ball)
          color (:color @ball)]
      (draw g (circle (+ board-start-x x) (+ board-start-y y) (- ball-size 1)) (style :background color :stroke 1 :foreground :black)))))

(defn draw-decorations [g]
  (draw g
    (rect
      (- board-start-x outside-border-size)
      (- board-start-y outside-border-size)
      (+ board-width (* 2 outside-border-size))
      (+ 1 outside-border-size))
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
      (+ outside-border-size 1)
      (+ board-height outside-border-size))
    (style :background :brown)))

(defn draw-turn-indicator [g x y player-one]
  (draw g
    (circle x y ball-size)
    (style :background (if (= player-one @player-one-turn) :white "#534B4F") :stroke 1 :foreground :black)))

(defn draw-pocketed-balls [g x y player-one]
  (let [balls (if player-one @player-one-pocketed @player-two-pocketed)
        direction (if player-one 1 -1)]
    (doseq [i (range (count balls))]
      (draw g (circle (+ (* direction i 2 ball-size) x 2) y ball-size)
        (style :background (nth balls i) :stroke 1 :foreground :black)))))

(defn draw-info-panel [g]
  (let [starting-x-one (+ (- board-start-x outside-border-size) ball-size)
        starting-x-two (+ starting-x-one board-width outside-border-size)
        starting-y (- board-start-y outside-border-size (* 2 ball-size))]
    (draw g (rect
              (- starting-x-one ball-size 5)
              (- starting-y ball-size 5)
              (+ board-width outside-border-size (* ball-size 2) 10)
              (+ (* 2 ball-size) 10))
      (style :background "#8F9779" :stroke 1 :foreground :black))
    (draw-turn-indicator g starting-x-one starting-y true)
    (draw-turn-indicator g starting-x-two starting-y false)
    (draw-pocketed-balls g (+ starting-x-one (* 4 ball-size)) starting-y true)
    (draw-pocketed-balls g (-  starting-x-two (* 4 ball-size)) starting-y false)))

(defn draw-table [c g]
  (dosync
    (draw-board g)
    (draw-balls g)
    (draw-decorations g)
    (draw-info-panel g)
    (when (and @is-playing (not @is-free-ball))
      (draw-cue g))))
