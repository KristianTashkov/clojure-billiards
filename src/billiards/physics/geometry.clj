(ns billiards.physics.geometry
  (:use [clojure.contrib.generic.math-functions]))

(def pi 3.14159265359)

(defn get-circle-point-from-angle [[center-x center-y] radius angle]
  (let [new-x (+ center-x (* radius (sin angle)))
        new-y (+ center-y (* radius (cos angle)))]
    [new-x new-y]))

(defn degrees-to-radians [degrees]
  (* degrees (/ pi 180)))

(defn radians-to-degrees [radians]
  (* radians (/ 180 pi)))

(defn normalize-vect [[x y]]
  (if (and (zero? x) (zero? y))
    [0 0]
    (let [len (sqrt (+ (* x x) (* y y)))]
      [(/ x len) (/ y len)])))

(defn get-perpendicular-of-vect [[x y]]
  [y (* -1 x)])

(defn subtract-pair [[x1 y1] [x2 y2]]
  [(- x1 x2) (- y1 y2)])

(defn dot-product-vect [[x1 y1] [x2 y2]]
  (+ (* x1 x2) (* y1 y2)))

(defn vect-length [vect]
  (sqrt (dot-product-vect vect vect)))

(defn reverse-vect [[x y]]
  [(* -1 x) (* -1 y)])

(defn sum-pair [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn product-vect-scalar [[x y] a]
  [(* x a) (* y a)])

(defn get-vect-from-angle [angle]
  [(* -1 (sin angle)) (* -1 (cos angle))])

(defn get-angle-from-vect [vect]
  (let [radians (apply atan2 (subtract-pair vect [1.0 0.0]))
        degrees (radians-to-degrees radians)
        fixed-degrees (+ 180 degrees)
        fixed-radians (degrees-to-radians fixed-degrees)]
    fixed-radians))

(defn distance-point-to-point [[x1 y1] [x2 y2]]
  (let [delta-x (- x2 x1)
        delta-y (- y2 y1)]
    (sqrt (+ (* delta-x delta-x) (* delta-y delta-y)))))

(defn circle-collision-circle? [[x1 y1 radius1] [x2 y2 radius2]]
  (let [dist (distance-point-to-point [x1 y1] [x2 y2])]
    (< dist (+ radius1 radius2))))

(defn reflect-vect-from-normal [vect normal]
  (normalize-vect (sum-pair vect
                    (product-vect-scalar
                      normal
                      (* -2 (dot-product-vect vect normal))))))

(defn closest-point-segment-point [a b c]
  (let [segment-vect (subtract-pair b a)
        circle-vect (subtract-pair c a)
        segment-vect-normal (normalize-vect segment-vect)
        proj (dot-product-vect segment-vect-normal circle-vect)]
    (cond
      (<= proj 0) a
      (>= proj (vect-length segment-vect)) b
      :else (sum-pair a (product-vect-scalar segment-vect-normal proj)))))

(defn segment-collision-circle? [seg-a seg-b [c radius]]
  (let [closest (closest-point-segment-point seg-a seg-b c)
        dist-vect (subtract-pair c closest)]
    (< (vect-length dist-vect) radius)))

(defn coerce-number-in-range [number range-start range-end]
  (cond
    (<= number range-start) range-start
    (>= number range-end) range-end
    :else number))
