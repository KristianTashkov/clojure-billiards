(ns billiards.physics.ball_physics
  (:use
    [billiards.physics.geometry]
    [billiards.constants]
    [billiards.utilities :only [apply-direction-ball]]))

(defn move-ball [ball]
  (let [speed (min ball-max-speed (:speed @ball))
        speedx (* speed (:dir-x @ball))
        speedy (* speed (:dir-y @ball))]
    (dosync
      (if (pos? speed)
        (do
          (alter ball update-in [:x] #(+ % speedx))
          (alter ball update-in [:y] #(+ % speedy)))
        (alter ball update-in [:speed] (fn [x] 0.0))))))

(defn fix-position [ball1 ball2]
  (let [dir (normalize-vect [(- (:x @ball2) (:x @ball1)) (- (:y @ball2) (:y @ball1))])
        distance (distance-point-to-point [(:x @ball1) (:y @ball1)] [(:x @ball2) (:y @ball2)])
        change-vect (product-vect-scalar dir (/ (- (* 2 ball-size) distance) 2))
        new-position1 (sum-pair
                        [(:x @ball1) (:y @ball1)]
                        (reverse-vect change-vect))
        new-position2 (sum-pair
                        [(:x @ball2) (:y @ball2)]
                        change-vect)]
    (dosync
      (alter ball1 update-in [:x] (fn [x] (first new-position1)))
      (alter ball1 update-in [:y] (fn [x] (second new-position1)))
      (alter ball2 update-in [:x] (fn [x] (first new-position2)))
      (alter ball2 update-in [:y] (fn [x] (second new-position2))))))

(defn calculate-new-direction [ball1 ball2]
  (let [v1 (product-vect-scalar [(:dir-x @ball1) (:dir-y @ball1)] (:speed @ball1))
        v2 (product-vect-scalar [(:dir-x @ball2) (:dir-y @ball2)] (:speed @ball2))
        normal (normalize-vect [(- (:x @ball1) (:x @ball2)) (- (:y @ball1) (:y @ball2))])
        reverse-normal (reverse-vect normal)
        vnormal1 (product-vect-scalar reverse-normal (dot-product-vect v1 reverse-normal))
        vnormal2 (product-vect-scalar normal (dot-product-vect v2 normal))
        vect-tangent1 (subtract-pair v1 vnormal1)
        vect-tangent2 (subtract-pair v2 vnormal2)
        new-direction-1 (sum-pair vect-tangent1 vnormal2)
        new-direction-2 (sum-pair vect-tangent2 vnormal1)
        speed1 (vect-length new-direction-1)
        speed2 (vect-length new-direction-2)
        new-direction-1 (normalize-vect new-direction-1)
        new-direction-2 (normalize-vect new-direction-2)]
    (apply-direction-ball ball1 new-direction-1 speed1)
    (apply-direction-ball ball2 new-direction-2 speed2)))

(defn apply-friction-ball [ball]
  (dosync
    (if (zero? (:friction-counter (ensure ball)))
      (do
        (alter ball update-in [:friction-counter] (fn [x] friction-counter-start))
        (alter ball update-in [:speed] (fn [x] (- x friction-step))))
      (alter ball update-in [:friction-counter] dec))))
