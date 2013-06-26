(ns billiards.physics.geometry
  (:use [clojure.contrib.generic.math-functions]))

(defn get-circle-point-from-angle [[centerX centerY] radius angle]
  (let [newX (+ centerX (* radius (sin angle)))
        newY (+ centerY (* radius (cos angle)))]
    [newX newY]))

(defn normalize-vector [[x y]]
  (if (and (zero? x) (zero? y))
    [0 0]
    (let [len (sqrt (+ (* x x) (* y y)))]
      [(/ x len) (/ y len)])))

(defn get-vector-from-angle [angle]
  [(* -1 (sin angle)) (* -1 (cos angle))])

(defn get-perp-of-vector [[x y]]
  [y (* -1 x)])

(defn sub-vect [[x1 y1] [x2 y2]]
  [(- x1 x2) (- y1 y2)])

(defn dot-product [[x1 y1] [x2 y2]]
  (+ (* x1 x2) (* y1 y2)))

(defn vect-length [vect]
  (sqrt (dot-product vect vect)))

(defn reverse-vector [[x y]]
  [(* -1 x) (* -1 y)])

(defn sum-pair [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn product-vector-scalar [[x y] a]
  [(* x a) (* y a)])

(defn distance-point-to-point [[x1 y1] [x2 y2]]
  (let [deltaX (- x2 x1)
        deltaY (- y2 y1)]
    (sqrt (+ (* deltaX deltaX) (* deltaY deltaY)))))

(defn circle-collision-circle? [[x1 y1 radius1] [x2 y2 radius2]]
  (let [dist (distance-point-to-point [x1 y1] [x2 y2])]
    (< dist (+ radius1 radius2))))

(defn reflect-vector-from-normal [vect normal]
  (normalize-vector (sum-pair vect
                      (product-vector-scalar
                        normal
                        (* -2 (dot-product vect normal))))))

(defn closest-point-segment-point [a b c]
  (let [segment-vect (sub-vect b a)
        circle-vect (sub-vect c a)
        segment-vect-normal (normalize-vector segment-vect)
        proj (dot-product segment-vect-normal circle-vect)]
    (cond
      (<= proj 0) a
      (>= proj (vect-length segment-vect)) b
      :else (sum-pair a (product-vector-scalar segment-vect-normal proj)))))

(defn segment-collision-circle? [seg-a seg-b [c radius]]
  (let [closest (closest-point-segment-point seg-a seg-b c)
        dist-vect (sub-vect c closest)]
    (< (vect-length dist-vect) radius)))
