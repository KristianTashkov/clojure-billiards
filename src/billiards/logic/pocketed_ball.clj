(ns billiards.logic.pocketed_ball
  (:use
    [billiards.state.global]
    [billiards.state.gui]
    [billiards.state.board]
    [billiards.state.rules]
    [billiards.utilities :only [other-color-ball]]))

(defn pocket-white-ball [ball]
  (reset! commited-foul true))

(defn pocket-black-ball [ball]
  (let [player (if @player-one-turn player-one-pocketed player-two-pocketed)]
    (swap! player conj :black)))

(defn pocket-colored-ball [ball]
  (let [color (if @player-one-turn @player-one-color @player-two-color)]
    (when (= color :none)
      (let [current (if @player-one-turn player-one-color player-two-color)
            other (if-not @player-one-turn player-one-color player-two-color)]
        (reset! current (:color @ball))
        (reset! other (other-color-ball (:color @ball))))))
  (let [which (if (= (:color @ball) @player-one-color) player-one-pocketed player-two-pocketed)]
    (swap! which conj (:color @ball)))
  (let [color (if @player-one-turn @player-one-color @player-two-color)]
    (if (= color (:color @ball))
      (reset! pocketed-ball true)
      (when @players-colors-decided
        (reset! commited-foul true)))))

(defn pocket-ball [ball]
  (dosync
    (alter balls (fn [coll] (remove #{ball} coll))))
  (cond
    (= (:color @ball) :white) (pocket-white-ball ball)
    (= (:color @ball) :black) (pocket-black-ball ball)
    :else (pocket-colored-ball ball)))
