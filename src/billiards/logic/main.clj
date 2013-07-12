(ns billiards.logic.main
  (:use
    [billiards.physics.collisions :only [collision-ball-pocket? collision-ball-ball? collision-borders-ball?]]
    [billiards.physics.ball_physics :only [move-ball apply-friction-ball]]
    [billiards.state.board]
    [billiards.state.gui]
    [billiards.state.global]
    [billiards.state.rules]
    [billiards.logic.pocketed_ball :only [pocket-ball]]
    [billiards.logic.rules :only [check-rules]]))

(defn step []
  (doseq [ball @balls]
    (move-ball ball)
    (apply-friction-ball ball)))

(defn get-pairs-balls [balls]
  (loop [current (first balls) other (rest balls) result []]
    (if (seq other)
      (recur (first other) (rest other) (into result (map (fn [x] [current x]) other)))
      result)))

(defn collisions []
  (doseq [ball @balls pocket @pockets]
    (when (collision-ball-pocket? ball pocket)
      (pocket-ball ball)))
  (doseq [pair (get-pairs-balls @balls)]
    (when (and (collision-ball-ball? pair) (nil? @first-collision))
      (if (= (:color @(first pair)) :white)
        (reset! first-collision (:color @(second pair)))
        (reset! first-collision (:color @(first pair))))))
  (doseq [ball @balls]
    (when (collision-borders-ball? ball)
      (reset! hit-border true))))

(defn turn []
  (while (not-every? #((complement pos?) (:speed @%)) @balls)
    (step)
    (collisions)
    (Thread/sleep 3))
  (check-rules)
  (reset-rules false)
  (when (zero? @game-ended)
    (reset! is-playing true)))
