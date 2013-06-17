(ns billiards.physics.ball_physics
  (:use [billiards.globals]
    [billiards.physics.geometry]))

(defn apply-direction [ball [dirx diry]]
  (dosync
    (alter ball update-in [:dirx] (fn [x] dirx))
    (alter ball update-in [:diry] (fn [x] diry))))

(defn collision-border-ball [ball]
  (dosync
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
      (alter ball update-in [:speed] (fn [x] (* x cushion-effect))))))

(defn fix-position [ball1 ball2]
  (let [dir (normalize-vector [(- (:x @ball2) (:x @ball1)) (- (:y @ball2) (:y @ball1))])
        distance (distance-point-to-point [(:x @ball1) (:y @ball1)] [(:x @ball2) (:y @ball2)])
        change-vector (product-vector-scalar dir (/ (- (* 2 ball-size) distance) 2))
        newPosition1 (sum-pair
                       [(:x @ball1) (:y @ball1)]
                       (reverse-vector change-vector))
        newPosition2 (sum-pair
                       [(:x @ball2) (:y @ball2)]
                       change-vector)]
    (dosync
      (alter ball1 update-in [:x] (fn [x] (first newPosition1)))
      (alter ball1 update-in [:y] (fn [x] (second newPosition1)))
      (alter ball2 update-in [:x] (fn [x] (first newPosition2)))
      (alter ball2 update-in [:y] (fn [x] (second newPosition2))))))

(defn fix-direction [ball]
  (when ((complement pos?) (:speed @ball))
    (dosync
      (alter ball update-in [:speed] (fn [x] 0.0))
      (apply-direction ball [0.0 0.0]))))

(defn calculate-new-direction [ball1 ball2]
  (let [dir-1-to-2 (normalize-vector [(- (:x @ball2) (:x @ball1)) (- (:y @ball2) (:y @ball1))])
        dir-2-to-1 (reverse-vector dir-1-to-2)
        new-direction-1 (normalize-vector (sum-pair [(:dirx @ball1) (:diry @ball1)] dir-2-to-1))
        new-direction-2 (normalize-vector (sum-pair [(:dirx @ball2) (:diry @ball2)] dir-1-to-2))]
    (apply-direction ball1 new-direction-1)
    (apply-direction ball2 new-direction-2)))

(defn calculate-new-speed [ball1 ball2]
  (let [new-speed (/ (+ (:speed @ball1) (:speed @ball2)) 2)]
    (dosync
      (alter ball1 update-in [:speed] (fn [x] new-speed))
      (alter ball2 update-in [:speed] (fn [x] new-speed)))))

(defn collision-ball-ball [[ball1 ball2]]
  (when (circle-collision-circle? [(:x @ball1) (:y @ball1) ball-size] [(:x @ball2) (:y @ball2) ball-size])
    (fix-position ball1 ball2)
    (fix-direction ball1)
    (fix-direction ball2)
    (calculate-new-direction ball1 ball2)
    (calculate-new-speed ball1 ball2)))

(defn move-ball [ball]
  (let [speed (min ball-max-speed (:speed @ball))
        speedx (* speed (:dirx @ball))
        speedy (* speed (:diry @ball))]
    (dosync
      (if (pos? speed)
        (do
          (alter ball update-in [:x] #(+ % speedx))
          (alter ball update-in [:y] #(+ % speedy))
          (alter ball update-in [:speed] (fn [x] (- x friction))))
        (alter ball update-in [:speed] (fn [x] 0))))))
