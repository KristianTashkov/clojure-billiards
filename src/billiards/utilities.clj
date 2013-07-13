(ns billiards.utilities
  (:use
    [billiards.constants :only [friction-counter-start]]
    [billiards.state.board :only [balls]]
    [billiards.physics.geometry]))

(defn create-ball [x y color]
  (ref {:x x :y y :color color :speed 0.0 :dir-x 0.0 :dir-y 0.0 :friction-counter friction-counter-start}))

(defn create-border [start end]
  {:start start
   :end end
   :normal (normalize-vect (get-perpendicular-of-vect (subtract-pair start end)))})

(defn get-white-ball []
  (first (filter #(= :white (:color @%)) @balls)))

(defn other-color-ball [color]
  (if (= color :red)
    :yellow
    :red))

(defn remaining-balls [color]
  (count (filter #(= color (:color @%)) @balls)))

(defn apply-direction-ball [ball [dir-x dir-y] speed]
  (dosync
    (alter ball update-in [:dir-x] (fn [x] dir-x))
    (alter ball update-in [:dir-y] (fn [x] dir-y))
    (alter ball update-in [:speed] (fn [x] speed))))
