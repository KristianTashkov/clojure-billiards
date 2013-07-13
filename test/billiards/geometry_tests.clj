(ns billiards.geometry_tests
  (:use
    [clojure.test]
    [billiards.physics.geometry]))

(deftest closest-point-segment-point-test
  (let [seg-a [0.0 0.0]
        seg-b [10.0 0.0]]
    (testing "left of segment"
      (is (= (closest-point-segment-point seg-a seg-b [-10.0 10.0]) seg-a)))
    (testing "above left point"
      (is (= (closest-point-segment-point seg-a seg-b [0.0 10.0]) seg-a)))
    (testing "above segment"
      (is (= (closest-point-segment-point seg-a seg-b [5.0 10.0]) [5.0 0.0])))
    (testing "above right segment"
      (is (= (closest-point-segment-point seg-a seg-b [10.0 10.0]) seg-b)))
    (testing "right of segment"
      (is (= (closest-point-segment-point seg-a seg-b [20.0 10.0]) seg-b)))))

(deftest segment-collision-circle-test
  (let [seg-a [0.0 0.0]
        seg-b [10.0 0.0]]
    (testing "above segment with long-enough radius"
      (is (= (segment-collision-circle? seg-a seg-b [[5.0 5.0] 6]) true)))
    (testing "above segment with not long-enough radius"
      (is (= (segment-collision-circle? seg-a seg-b [[5.0 5.0] 5]) false)))
    (testing "below segment with long-enough radius"
      (is (= (segment-collision-circle? seg-a seg-b [[5.0 -5.0] 6]) true)))
    (testing "below segment with not long-enough radius"
      (is (= (segment-collision-circle? seg-a seg-b [[5.0 -5.0] 5]) false)))
    (testing "left of segment with long-enough radius"
      (is (= (segment-collision-circle? seg-a seg-b [[-5.0 0.0] 6]) true)))
    (testing "left of segment with not long-enough radius"
      (is (= (segment-collision-circle? seg-a seg-b [[-5.0 0.0] 5]) false)))
    (testing "right of segment with long-enough radius"
      (is (= (segment-collision-circle? seg-a seg-b [[15.0 0.0] 6]) true)))
    (testing "right of segment with not long-enough radius"
      (is (= (segment-collision-circle? seg-a seg-b [[15.0 0.0] 5]) false)))
    (testing "segment through circle"
      (is (= (segment-collision-circle? seg-a seg-b [[5.0 0.0] 1]) true)))))

(deftest reflect-vect-from-normal-test
  (testing "testing reflection"
    (is (= (reflect-vect-from-normal [1 1] [0 1]) (normalize-vect [1 -1])))
    (is (= (reflect-vect-from-normal [0 1] [0 1]) (normalize-vect [0 -1])))
    (is (= (reflect-vect-from-normal [1 1] [0 -1]) (normalize-vect [1 -1])))))

(deftest coerce-number-in-range-test
  (testing "Coercing number in range"
    (is (= (coerce-number-in-range 15 10 20) 15))
    (is (= (coerce-number-in-range 30 10 20) 20))
    (is (= (coerce-number-in-range 5 10 20) 10))
    (is (= (coerce-number-in-range 0 -10 10) 0))
    (is (= (coerce-number-in-range -20 -10 10) -10))))


