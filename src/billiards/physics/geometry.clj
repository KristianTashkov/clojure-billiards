(ns billiards.physics.geometry
  (:use [clojure.contrib.generic.math-functions]))

(def pi 3.14159265359)

(defn get-circle-point-from-angle [[centerX centerY] radius angle]
  (let [newX (+ centerX (* radius (sin angle)))
        newY (+ centerY (* radius (cos angle)))]
    [newX newY]))

(defn degrees-to-radians [degrees]
  (* degrees (/ pi 180)))

(defn radians-to-degrees [radians]
  (* radians (/ 180 pi)))

(defn normalize-vector [[x y]]
  (if (and (zero? x) (zero? y))
    [0 0]
    (let [len (sqrt (+ (* x x) (* y y)))]
      [(/ x len) (/ y len)])))

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

(defn get-vector-from-angle [angle]
  [(* -1 (sin angle)) (* -1 (cos angle))])

(defn get-angle-from-vector [vect]
  (let [radians (apply atan2 (sub-vect vect [1.0 0.0]))
        degrees (radians-to-degrees radians)
        fixed-degrees (+ 180 degrees)
        fixed-radians (degrees-to-radians fixed-degrees)]
    fixed-radians))

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

(defn coerce-number-in-range [number range-start range-end]
  (cond
    (<= number range-start) range-start
    (>= number range-end) range-end
    :else number))
