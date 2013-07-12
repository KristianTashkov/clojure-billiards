(ns billiards.utilities
  (:use
    [billiards.constants :only [friction-counter-start]]
    [billiards.state.board :only [balls]]))

(defn create-ball [x y color]
  (ref {:x x :y y :color color :speed 0.0 :dirx 0.0 :diry 0.0 :friction-counter friction-counter-start}))

(defn get-white-ball []
  (first (filter #(= :white (:color @%)) @balls)))

(defn other-color-ball [color]
  (if (= color :red)
    :yellow
    :red))

(defn remaining-balls [color]
  (count (filter #(= color (:color @%)) @balls)))
