(ns billiards.physics.geometry
  (:use [clojure.contrib.generic.math-functions]))

(defn get-circle-point-from-angle [[centerX centerY] radius angle]
  (let [newX (+ centerX (* radius (sin angle)))
        newY (+ centerY (* radius (cos angle)))]
    [newX newY]))

(defn normalize-vector [[x y]]
   (let [len (sqrt (+ (* x x) (* y y)))]
      [(/ x len) (/ y len)]))

(defn get-vector-from-angle [angle]
   [(* -1 (sin angle)) (* -1 (cos angle))])

(defn get-perp-of-vector [[x y]]
   [y (* -1 x)])

(defn sum-vect [[x1 y1] [x2 y2]]
   (normalize-vector [(+ x1 x2) (+ y1 y2)]))

(defn reverse-vector [[x y]]
   [(* -1 x) (* -1 y)])

(defn sum-points [[x1 y1] [x2 y2]]
   [(+ x1 x2) (+ y1 y2)])

(defn product-vector-scalar [[x y] a]
   [(* x a) (* y a)])