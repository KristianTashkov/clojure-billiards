(ns billiards.collisions_test
  (:use
    [clojure.test]
    [billiards.state.initial :only [reset-game]]
    [billiards.state.board :only [borders]]
    [billiards.physics.collisions]
    [billiards.physics.geometry :only [vect-length normalize-vect circle-collision-circle?]]
    [billiards.constants]
    [billiards.utilities]
    [clojure.contrib.generic.math-functions :only [sqrt abs]]))

(deftest collision-border-ball-test
  (testing "Collision ball with border"
    (let [ball (create-ball 50.0 (/ ball-size 2) :white)
          border (create-border [0 0] [100 0])]
      (reset! borders [border])
      (apply-direction-ball ball [0.0 -1.0] 10.0)
      (is (collision-border-ball? border ball))
      (is (not (collision-border-ball-check? border ball)))
      (is (== (* 10 cushion-effect) (:speed @ball)))
      (is (== (:dir-x @ball) 0.0))
      (is (== (:dir-y @ball) 1.0))))

  (testing "No collision ball with border"
    (let [ball (create-ball 50.0 50.0 :white)
          border (create-border [0 0] [100 0])]
      (reset! borders [border])
      (apply-direction-ball ball [0.0 -1.0] 10.0)
      (is (not (collision-border-ball? border ball)))
      (is (== 10.0 (:speed @ball)))
      (is (== (:x @ball) 50.0))
      (is (== (:x @ball) 50.0))
      (is (== (:dir-x @ball) 0.0))
      (is (== (:dir-y @ball) -1.0)))))

(deftest collision-ball-ball-test
  (testing "No collision ball with ball"
    (let [ball1 (create-ball 50.0 50.0 :white)
          ball2 (create-ball (+ (* 2 ball-size) 50.0) 50.0 :white)]
      (apply-direction-ball ball1 [1.0 0] 10.0)
      (is (not (collision-ball-ball? [ball1 ball2])))
      (is (== (:x @ball1) 50.0))
      (is (== (:y @ball1) 50.0))
      (is (== (:x @ball2) (+ (* 2 ball-size) 50.0)))
      (is (== (:y @ball2) 50.0))
      (is (== (:dir-x @ball1) 1.0))
      (is (== (:dir-y @ball1) 0.0))
      (is (== (:speed @ball1) 10.0))
      (is (== (:dir-x @ball2) 0.0))
      (is (== (:dir-y @ball2) 0.0))
      (is (== (:speed @ball2) 0.0))))
  (testing "Collision ball with ball | one ball at a stop | head on"
    (let [ball1 (create-ball 50.0 50.0 :white)
          ball2 (create-ball (- (+ (* 2 ball-size) 50.0) 1.0) 50.0 :white)]
      (apply-direction-ball ball1 [1.0 0] 10.0)
      (is (collision-ball-ball? [ball1 ball2]))
      (is (not (circle-collision-circle? [(:x @ball1) (:y @ball1) ball-size] [(:x @ball2) (:y @ball2) ball-size])))
      (is (== (:dir-x @ball1) 0.0))
      (is (== (:dir-y @ball1) 0.0))
      (is (== (:speed @ball1) 0.0))
      (is (== (:dir-x @ball2) 1.0))
      (is (== (:dir-y @ball2) 0.0))
      (is (== (:speed @ball2) 10.0))))
  (testing "Collision ball with ball | opposite directions | head on"
    (let [ball1 (create-ball 50.0 50.0 :white)
          ball2 (create-ball (- (+ (* 2 ball-size) 50.0) 1.0) 50.0 :white)]
      (apply-direction-ball ball1 [1.0 0] 10.0)
      (apply-direction-ball ball2 [-1.0 0] 5.0)
      (is (collision-ball-ball? [ball1 ball2]))
      (is (not (circle-collision-circle? [(:x @ball1) (:y @ball1) ball-size] [(:x @ball2) (:y @ball2) ball-size])))
      (is (== (:dir-x @ball1) -1.0))
      (is (== (:dir-y @ball1) 0.0))
      (is (== (:speed @ball1) 5.0))
      (is (== (:dir-x @ball2) 1.0))
      (is (== (:dir-y @ball2) 0.0))
      (is (== (:speed @ball2) 10.0))))
  (testing "Collision ball with ball | same directions | head on"
    (let [ball1 (create-ball 50.0 50.0 :white)
          ball2 (create-ball (- (+ (* 2 ball-size) 50.0) 1.0) 50.0 :white)]
      (apply-direction-ball ball1 [1.0 0] 10.0)
      (apply-direction-ball ball2 [1.0 0] 5.0)
      (is (collision-ball-ball? [ball1 ball2]))
      (is (not (circle-collision-circle? [(:x @ball1) (:y @ball1) ball-size] [(:x @ball2) (:y @ball2) ball-size])))
      (is (== (:dir-x @ball1) 1.0))
      (is (== (:dir-y @ball1) 0.0))
      (is (== (:speed @ball1) 5.0))
      (is (== (:dir-x @ball2) 1.0))
      (is (== (:dir-y @ball2) 0.0))
      (is (== (:speed @ball2) 10.0))))
  (testing "Collision ball with ball | perpendicular directions"
    (let [ball1 (create-ball 50.0 50.0 :white)
          ball2 (create-ball (- (+ (* 2 ball-size) 50.0) 1.0) 50.0 :white)]
      (apply-direction-ball ball1 [0.0 1] 10.0)
      (apply-direction-ball ball2 [-1.0 0] 10.0)
      (is (collision-ball-ball? [ball1 ball2]))
      (is (not (circle-collision-circle? [(:x @ball1) (:y @ball1) ball-size] [(:x @ball2) (:y @ball2) ball-size])))
      (is (== (:dir-x @ball1) (/ -1.0 (sqrt 2.0))))
      (is (== (:dir-y @ball1) (/ 1.0 (sqrt 2.0))))
      (is (== (:speed @ball1) (/ 10.0 (/ 1.0 (sqrt 2.0)))))
      (is (== (:dir-x @ball2) 0.0))
      (is (== (:dir-y @ball2) 0.0))
      (is (== (:speed @ball2) 0.0))))
  (testing "Collision ball with ball | opposite directions | at an angle"
    (let [ball1 (create-ball 50.0 50.0 :white)
          ball2 (create-ball (+ 50.0 ball-size -1.0) (+ 50.0 ball-size -1.0) :white)]
      (apply-direction-ball ball1 [1.0 0] 10.0)
      (apply-direction-ball ball2 [-1.0 0] 10.0)
      (is (collision-ball-ball? [ball1 ball2]))
      (is (not (circle-collision-circle? [(:x @ball1) (:y @ball1) ball-size] [(:x @ball2) (:y @ball2) ball-size])))
      (is (< (abs (:dir-x @ball1)) 0.00001))
      (is (< (abs (- (:dir-y @ball1) -1.0)) 0.00001))
      (is (< (abs (- (:speed @ball1) 10.0)) 0.00001))
      (is (< (abs (:dir-x @ball2)) 0.00001))
      (is (< (abs (- (:dir-y @ball2) 1.0)) 0.00001))
      (is (< (abs (- (:speed @ball2) 10.0)) 0.00001)))))
