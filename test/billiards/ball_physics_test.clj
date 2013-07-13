(ns billiards.ball_physics_test
  (:use
    [clojure.test]
    [billiards.physics.ball_physics :only [move-ball apply-friction-ball]]
    [billiards.constants :only [ball-max-speed friction-counter-start friction-step]]
    [billiards.utilities :only [create-ball apply-direction-ball]]))

(deftest move-ball-test
  (testing "Move ball with positive speed"
    (let [ball (create-ball 50.0 50.0 :white)]
      (apply-direction-ball ball [1.0 0.0] ball-max-speed)
      (move-ball ball)
      (is (= (:x @ball) (+ 50.0 ball-max-speed)))
      (is (= (:y @ball) 50.0))))
  (testing "Move ball with negative speed"
    (let [ball (create-ball 50.0 50.0 :white)]
      (apply-direction-ball ball [1.0 0.0] -10)
      (move-ball ball)
      (is (= (:x @ball) 50.0))
      (is (= (:y @ball) 50.0))
      (is (= (:speed @ball) 0.0)))))

(deftest apply-friction-ball-test
  (testing "Applying friction test"
    (let [ball (create-ball 50.0 50.0 :white)]
      (apply-direction-ball ball [1.0 0.0] 10.0)
      (doseq [i (range (+ friction-counter-start 1))]
        (apply-friction-ball ball))
      (is (= (:speed @ball) (- 10.0 friction-step)))
      (is (= (:friction-counter @ball) friction-counter-start)))))
