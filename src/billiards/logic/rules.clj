(ns billiards.logic.rules
  (:use
    [billiards.state.board]
    [billiards.state.global]
    [billiards.state.rules]
    [billiards.constants]
    [billiards.utilities]))

(defn win [player-one]
  (reset! game-ended (if player-one 1 -1)))

(defn pocketed-black []
  (let [color (if @player-one-turn @player-one-color @player-two-color)]
    (if (or
          (= color :none)
          (pos? (remaining-balls color)))
      (win (not @player-one-turn))
      (win (if @commited-foul (not @player-one-turn) @player-one-turn)))))

(defn check-first-collision-color []
  (let [color (if @player-one-turn @player-one-color @player-two-color)]
    (when @first-collision
      (if (= @first-collision :black)
        (when (or (not @players-colors-decided) (pos? (remaining-balls color)))
          (reset! commited-foul true))
        (when (and
                @players-colors-decided
                (= @first-collision (other-color-ball color)))
          (reset! commited-foul true))))))

(defn check-no-border-hit []
  (when-not (or @hit-border @pocketed-ball)
    (reset! commited-foul true)))

(defn check-no-ball-hit []
  (when-not @first-collision
    (reset! commited-foul true)))

(defn check-pocketed-black []
  (when (= 0 (remaining-balls :black))
    (pocketed-black)))

(defn update-colors-decided []
  (when (not= @player-one-color :none)
    (reset! players-colors-decided true)))

(defn check-rules []
  (check-no-ball-hit)
  (check-no-border-hit)
  (check-first-collision-color)
  (update-colors-decided)
  (check-pocketed-black)
  (when (or @commited-foul (not @pocketed-ball))
    (swap! player-one-turn #(not %)))
  (when @commited-foul
    (reset! is-free-ball true))
  (when-not (get-white-ball)
    (dosync
      (alter balls conj (create-ball (/ board-width 2) (/ board-height 2) :white)))))
