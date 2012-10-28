(ns metric-time
  (:require [goog.Timer :as timer]
            [goog.events :as events]))

(def fps 60)

(defn ^:export go [w h]
  (js/p.size w h)
  (js/p.background 255 255 255)
  (events/listen
   (doto (goog.Timer. (/ 1000 fps))
     (. start)) goog.Timer/TICK draw))

(def n 1)
(defn draw []
  (js/p.fill 0 0 0)
  (js/p.stroke 255 255 255)
  (js/p.rect 0 0 js/p.width (* 20 n))
  (def n (+ 0.001 n)))
