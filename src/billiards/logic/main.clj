(ns billiards.logic.main
  (:use
    [billiards.globals]
    [billiards.physics.ball_physics]))

(defn step []
  (doseq [ball @balls]
    (move-ball ball)
    (apply-friction-ball ball)))

(defn get-pairs-balls [balls]
  (loop [current (first balls) other (rest balls) result []]
    (if (seq other)
      (recur (first other) (rest other) (into result (map (fn [x] [current x]) other)))
      result)))

(defn pocket-white-ball [ball]
  (reset! is-free-ball true))

(defn pocket-colored-ball [ball])

(defn pocket-ball [ball]
  (dosync
    (alter balls (fn [coll] (remove #{ball} coll))))
  (if (= (:color @ball) :white)
    (pocket-white-ball ball)
    (pocket-colored-ball ball)))

(defn collisions []
  (doseq [ball @balls pocket @pockets]
    (when (collision-ball-pocket? ball pocket)
      (pocket-ball ball)))
  (doseq [pair (get-pairs-balls @balls)]
    (collision-ball-ball pair))
  (doseq [ball @balls]
    (collision-borders-ball ball)))

(defn turn [redisplay]
  (let [painter (atom (future (1)))]
    (while (not-every? #((complement pos?) (:speed @%)) @balls)
      (step)
      (collisions)
      (when (realized? @painter)
        (reset! painter (future ((redisplay)))))
      (Thread/sleep 3)))
  (when @is-free-ball
    (dosync
      (alter balls conj (create-ball
                          (+ board-padding (* 2 pocket-size))
                          (+ board-padding (* 2 pocket-size))
                          :white)))
    (redisplay)))
