(ns billiards.gui.actions
  (:use
    [seesaw mouse]
    [billiards.globals]
    [billiards.physics.geometry]
    [billiards.logic.main :only [turn]]))

(defn change-power [change]
  (swap! cue-power (fn [old]
                     (let [newVal (+ old change)]
                       (cond
                         (< newVal 0) 0
                         (> newVal 100) 100
                         :else newVal)))))

(defn shoot [redisplay]
  (reset! is-playing false)
  (let [ball (get-white-ball)
        dir (get-vector-from-angle @cue-angle)
        [dirX dirY] (normalize-vector dir)]
    (dosync
      (alter ball update-in [:speed] (fn [old] (/ (* ball-max-power @cue-power) 100)))
      (alter ball update-in [:dirx] (fn [old] dirX))
      (alter ball update-in [:diry] (fn [old] dirY))))
  (turn redisplay)
  (reset! is-playing true)
  (redisplay))

(defn start-shooting [redisplay]
  (reset! is-shooting true)
  (while @is-shooting
    (change-power 1)
    (Thread/sleep 15)
    (redisplay))
  (reset! cue-power 0))

(defn place-free-ball [redisplay]
  (let [white-ball (get-white-ball)]
    (when (every? #(= % false) (for [ball @balls
                                     :when (not= ball white-ball)]
                                 (circle-collision-circle?
                                   [(:x @white-ball) (:y @white-ball) ball-size]
                                   [(:x @ball) (:y @ball) ball-size])))
      (reset! is-free-ball false)
      (redisplay))))

(defn mouse-released [event redisplay]
  (when @is-playing
    (if @is-free-ball
      (place-free-ball redisplay)
      (do
        (if @is-shooting
          (do
            (shoot redisplay)
            (reset! is-shooting false))
          (start-shooting redisplay))))))

(defn adjust-cue [mousex mousey white-ball]
  (let [dir (sub-vect [mousex mousey] [(+ board-start-x (:x @white-ball)) (+ board-start-y (:y @white-ball))])]
    (reset! cue-angle (get-angle-from-vector dir))))

(defn adjust-ball [mousex mousey white-ball]
  (dosync
    (alter white-ball update-in [:x] (fn [x]
                                       (- (coerce-number-in-range
                                            mousex
                                            (+ (* 2 ball-size) board-padding board-start-x)
                                            (+ board-start-x (- board-width (* 2 ball-size) board-padding))) board-start-x)))
    (alter white-ball update-in [:y] (fn [x]
                                       (- (coerce-number-in-range
                                            mousey
                                            (+ (* 2 ball-size) board-padding board-start-y)
                                            (+ board-start-y (- board-height (* 2 ball-size) board-padding))) board-start-y)))))
(defn mouse-moved [event redisplay]
  (when @is-playing
    (let [[mousex mousey] (location event)
          [mousex mousey] [(- mousex 10) (- mousey 30)]
          white-ball (get-white-ball)]
      (if @is-free-ball
        (adjust-ball mousex mousey white-ball)
        (adjust-cue mousex mousey white-ball))
      (redisplay))))
