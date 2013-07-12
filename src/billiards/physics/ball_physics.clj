(ns billiards.physics.ball_physics
  (:use
    [billiards.physics.geometry]
    [billiards.constants]))

(defn apply-direction [ball [dirx diry] speed]
  (dosync
    (alter ball update-in [:dirx] (fn [x] dirx))
    (alter ball update-in [:diry] (fn [x] diry))
    (alter ball update-in [:speed] (fn [x] speed))))

(defn move-ball [ball]
  (let [speed (min ball-max-speed (:speed @ball))
        speedx (* speed (:dirx @ball))
        speedy (* speed (:diry @ball))]
    (dosync
      (if (pos? speed)
        (do
          (alter ball update-in [:x] #(+ % speedx))
          (alter ball update-in [:y] #(+ % speedy)))
        (alter ball update-in [:speed] (fn [x] 0))))))

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

(defn calculate-new-direction [ball1 ball2]
  (let [v1 (product-vector-scalar [(:dirx @ball1) (:diry @ball1)] (:speed @ball1))
        v2 (product-vector-scalar [(:dirx @ball2) (:diry @ball2)] (:speed @ball2))
        normal (normalize-vector [(- (:x @ball1) (:x @ball2)) (- (:y @ball1) (:y @ball2))])
        reverse-normal (reverse-vector normal)
        vnormal1 (product-vector-scalar reverse-normal (dot-product v1 reverse-normal))
        vnormal2 (product-vector-scalar normal (dot-product v2 normal))
        vector-tangent1 (sub-vect v1 vnormal1)
        vector-tangent2 (sub-vect v2 vnormal2)
        new-direction-1 (sum-pair vector-tangent1 vnormal2)
        new-direction-2 (sum-pair vector-tangent2 vnormal1)
        speed1 (vect-length new-direction-1)
        speed2 (vect-length new-direction-2)
        new-direction-1 (normalize-vector new-direction-1)
        new-direction-2 (normalize-vector new-direction-2)]
    (apply-direction ball1 new-direction-1 speed1)
    (apply-direction ball2 new-direction-2 speed2)))

(defn apply-friction-ball [ball]
  (dosync
    (if (zero? (:friction-counter (ensure ball)))
      (do
        (alter ball update-in [:friction-counter] (fn [x] friction-counter-start))
        (alter ball update-in [:speed] (fn [x] (- x friction-step))))
      (alter ball update-in [:friction-counter] dec))))
