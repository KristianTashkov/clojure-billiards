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

(defn pocket-colored-ball [ball]
  (let [color (if @player-one-turn @player-one-color @player-two-color)]
    (when (= color :none)
      (let [current (if @player-one-turn player-one-color player-two-color)
            other (if-not @player-one-turn player-one-color player-two-color)]
        (reset! current (:color @ball))
        (reset! other (other-color-ball (:color @ball))))))
  (let [which (if (= (:color @ball) @player-one-color) player-one-pocketed player-two-pocketed)]
    (swap! which conj (:color @ball))))

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

(defn turn []
  (while (not-every? #((complement pos?) (:speed @%)) @balls)
    (step)
    (collisions)
    (Thread/sleep 3))
  (swap! player-one-turn #(not %))
  (when @is-free-ball
    (dosync
      (alter balls conj (create-ball
                          (+ board-padding (* 2 pocket-size))
                          (+ board-padding (* 2 pocket-size))
                          :white)))))
