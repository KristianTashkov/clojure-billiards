(ns billiards.gui.actions
  (:use
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
(defn move-cue [change]
  (when @is-playing
    (swap! cue-angle (fn [old] (+ change old)))))

(defn shoot [redisplay]
  (when @is-playing
    (reset! is-playing false)
    (let [ball (get-white-ball)
          dir (get-vector-from-angle @cue-angle)
          [dirX dirY] (normalize-vector dir)]
      (dosync
        (alter ball update-in [:speed] (fn [old] (/ (* ball-max-power @cue-power) 100)))
        (alter ball update-in [:dirx] (fn [old] dirX))
        (alter ball update-in [:diry] (fn [old] dirY))))
    (turn redisplay)
    (reset! cue-power 0)
    (reset! is-playing true)
    (redisplay)))
