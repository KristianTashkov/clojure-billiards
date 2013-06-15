(ns billiards.logic.main
  (:use
    [billiards.globals]
    [billiards.physics.ball_physics]))

(defn step []
  (dosync
    (doseq [ball @balls]
      (move-ball ball))))

(defn collisions []
  (dosync
    (doseq [ball @balls]
      (collision-border-ball ball))))

(defn turn [redisplay]
  (let [painter (atom (future (1)))]
    (while (not-every? #(= 0 (:speed @%)) @balls)
      (step)
      (collisions)
      (when (realized? @painter)
        (reset! painter (future ((redisplay)))))
      (Thread/sleep 15))))
