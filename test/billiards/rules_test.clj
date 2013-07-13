(ns billiards.rules_test
  (:use
    [clojure.test]
    [billiards.state.global :only [player-one-turn game-ended]]
    [billiards.state.board :only [balls borders pockets]]
    [billiards.state.rules]
    [billiards.state.initial :only [reset-game]]
    [billiards.logic.main :only [step collisions]]
    [billiards.logic.rules :only [check-rules]]
    [billiards.utilities :only [create-ball create-border apply-direction-ball]]))

(deftest rules-test
  (testing "Correct shot with colors decided"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          red-ball (create-ball 10 50 :red)
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball red-ball]))
      (reset! borders [border])
      (reset! player-one-turn true)
      (reset! player-one-color :red)
      (reset! players-colors-decided true)
      (apply-direction-ball white-ball [1.0 0.0] 20)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is (not @commited-foul))))
  (testing "Correct shot with colors not decided"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          red-ball (create-ball 10 50 :red)
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball red-ball]))
      (reset! borders [border])
      (apply-direction-ball white-ball [1.0 0.0] 20)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is (not @commited-foul))))
  (testing "Correct shot with colors only black ball left"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          black-ball (create-ball 10 50 :black)
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball black-ball]))
      (reset! borders [border])
      (reset! player-one-turn true)
      (reset! player-one-color :red)
      (reset! players-colors-decided true)
      (apply-direction-ball white-ball [1.0 0.0] 20)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is (not @commited-foul))))
  (testing "No border hit rule"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          red-ball (create-ball 100 50 :red)]
      (dosync
        (ref-set balls [white-ball red-ball]))
      (apply-direction-ball white-ball [1.0 0.0] 10)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is @commited-foul)))
  (testing "No ball hit rule"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball]))
      (reset! borders [border])
      (apply-direction-ball white-ball [1.0 0.0] 20)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is @commited-foul)))
  (testing "First collision wrong color"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          yellow-ball (create-ball 10 50 :yellow)
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball yellow-ball]))
      (reset! borders [border])
      (reset! player-one-turn true)
      (reset! player-one-color :red)
      (reset! players-colors-decided true)
      (apply-direction-ball white-ball [1.0 0.0] 10)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is @commited-foul)))
  (testing "First collision black ball when color is undecided"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          black-ball (create-ball 10 50 :black)
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball black-ball]))
      (reset! borders [border])
      (apply-direction-ball white-ball [1.0 0.0] 10)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is @commited-foul)))
  (testing "Pocketed wrong color"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          yellow-ball (create-ball -10 50 :yellow)
          red-ball (create-ball 10 50 :red)
          pocket [-50 50]
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball yellow-ball red-ball]))
      (reset! borders [border])
      (reset! pockets [pocket])
      (reset! player-one-turn true)
      (reset! player-one-color :red)
      (reset! players-colors-decided true)
      (apply-direction-ball white-ball [1.0 0.0] 10)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is @commited-foul)))
  (testing "Pocketed white ball"
    (reset-rules true)
    (let [white-ball (create-ball 10 50 :white)
          red-ball (create-ball 50 50 :red)
          pocket [-50 50]
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball red-ball]))
      (reset! borders [border])
      (reset! pockets [pocket])
      (apply-direction-ball white-ball [1.0 0.0] 10)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is @commited-foul)))
  (testing "Pocketed black with other balls left"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          black-ball (create-ball -10 50 :black)
          red-ball (create-ball 10 50 :red)
          pocket [-50 50]
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball black-ball red-ball]))
      (reset! borders [border])
      (reset! pockets [pocket])
      (reset! player-one-turn true)
      (reset! player-one-color :red)
      (reset! players-colors-decided true)
      (apply-direction-ball white-ball [1.0 0.0] 10)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is (= @game-ended -1))))
  (testing "Pocketed black with no other balls left"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          black-ball (create-ball -10 50 :black)
          pocket [-50 50]
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball black-ball]))
      (reset! borders [border])
      (reset! pockets [pocket])
      (reset! player-one-turn true)
      (reset! player-one-color :red)
      (reset! players-colors-decided true)
      (apply-direction-ball white-ball [1.0 0.0] 10)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is (= @game-ended 1))))
  (testing "Pocketed black with no other balls left and commiting foul"
    (reset-rules true)
    (let [white-ball (create-ball 50 50 :white)
          black-ball (create-ball -10 50 :black)
          yellow-ball (create-ball 10 50 :yellow)
          pocket [-50 50]
          border (create-border [70 20] [70 70])]
      (dosync
        (ref-set balls [white-ball black-ball yellow-ball]))
      (reset! borders [border])
      (reset! pockets [pocket])
      (reset! player-one-turn true)
      (reset! player-one-color :red)
      (reset! players-colors-decided true)
      (apply-direction-ball white-ball [1.0 0.0] 10)
      (while (not-every? #((complement pos?) (:speed @%)) @balls)
        (step)
        (collisions))
      (check-rules)
      (is (= @game-ended -1)))))
