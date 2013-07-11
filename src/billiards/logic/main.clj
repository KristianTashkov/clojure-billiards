(ns billiards.logic.main
  (:use
    [billiards.globals]
    [billiards.physics.ball_physics]))

(defn win [player-one]
  (reset! game-ended (if player-one 1 -1)))

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
  (reset! commited-foul true))

(defn pocket-black-ball [ball]
  (let [player (if @player-one-turn player-one-pocketed player-two-pocketed)
        color (if @player-one-turn player-one-color player-two-color)]
    (swap! player conj :black)
    (if (or
          (= color :none)
          (pos? (count (filter #(= (:color %) color) @balls))))
      (win (not @player-one-turn))
      (win @player-one-turn))))

(defn pocket-colored-ball [ball]
  (let [color (if @player-one-turn @player-one-color @player-two-color)]
    (when (= color :none)
      (let [current (if @player-one-turn player-one-color player-two-color)
            other (if-not @player-one-turn player-one-color player-two-color)]
        (reset! current (:color @ball))
        (reset! other (other-color-ball (:color @ball))))))
  (let [which (if (= (:color @ball) @player-one-color) player-one-pocketed player-two-pocketed)]
    (swap! which conj (:color @ball)))
  (let [color (if @player-one-turn @player-one-color @player-two-color)]
    (if (= color (:color @ball))
      (reset! pocketed-ball true)
      (reset! commited-foul true))))

(defn pocket-ball [ball]
  (dosync
    (alter balls (fn [coll] (remove #{ball} coll))))
  (cond
    (= (:color @ball) :white) (pocket-white-ball ball)
    (= (:color @ball) :black) (pocket-black-ball ball)
    :else (pocket-colored-ball ball)))

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

(defn check-rules []
  (when-not @first-collision
    (reset! commited-foul true))
  (when-not (or @hit-border @pocketed-ball)
    (reset! commited-foul true))
  (let [color (if @player-one-turn @player-one-color @player-two-color)]
    (when (and (not= color :none) (not= @first-collision color))
      (reset! commited-foul true)))
  (when (or @commited-foul (not @pocketed-ball))
    (swap! player-one-turn #(not %)))
  (when @commited-foul
    (reset! is-free-ball true))
  (when-not (get-white-ball)
    (dosync
      (alter balls conj (create-ball
                          (+ board-padding (* 2 pocket-size))
                          (+ board-padding (* 2 pocket-size))
                          :white))))
  (reset! is-playing true))

(defn end-game [])

(defn turn []
  (reset! commited-foul false)
  (reset! pocketed-ball false)
  (reset! hit-border false)
  (reset! first-collision nil)
  (while (not-every? #((complement pos?) (:speed @%)) @balls)
    (step)
    (collisions)
    (Thread/sleep 3))
  (if (zero? @game-ended)
    (check-rules)
    (end-game)))
