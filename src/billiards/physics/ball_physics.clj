(ns billiards.physics.ball_physics
  (:use [billiards.globals]))

(defn collision-border-ball [ball]
  (when (< (:x @ball) ball-size)
    (alter ball update-in [:x] (fn [x] ball-size))
    (alter ball update-in [:dirx] (fn [x] (* -1 x)))
    (alter ball update-in [:speed] (fn [x] (* x cushion-effect))))
  (when (< (:y @ball) ball-size)
    (alter ball update-in [:y] (fn [x] ball-size))
    (alter ball update-in [:diry] (fn [x] (* -1 x)))
    (alter ball update-in [:speed] (fn [x] (* x cushion-effect))))
  (when (> (:x @ball) (- board-width ball-size))
    (alter ball update-in [:x] (fn [x] (- board-width ball-size)))
    (alter ball update-in [:dirx] (fn [x] (* -1 x)))
    (alter ball update-in [:speed] (fn [x] (* x cushion-effect))))
  (when (> (:y @ball) (- board-height ball-size))
    (alter ball update-in [:y] (fn [x] (- board-height ball-size)))
    (alter ball update-in [:diry] (fn [x] (* -1 x)))
    (alter ball update-in [:speed] (fn [x] (* x cushion-effect)))))

(defn move-ball [ball]
  (let [speed (min ball-max-speed (:speed @ball))
        speedx (* speed (:dirx @ball))
        speedy (* speed (:diry @ball))]
    (if (pos? speed)
      (do
        (alter ball update-in [:x] #(+ % speedx))
        (alter ball update-in [:y] #(+ % speedy))
        (alter ball update-in [:speed] (fn [x] (- x friction))))
      (alter ball update-in [:speed] (fn [x] 0)))))
