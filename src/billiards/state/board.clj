(ns billiards.state.board)

(def border-points (atom []))
(def borders (atom []))
(def pockets (atom []))
(def balls (ref []))
(def cue-angle (atom 0))
(def cue-power (atom 0))

(defn reset-board []
  (reset! border-points [])
  (reset! borders [])
  (reset! pockets [])
  (reset! cue-angle 0)
  (reset! cue-power 0)
  (dosync
    (ref-set balls [])))
