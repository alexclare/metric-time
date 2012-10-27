(ns metric-time
  (:require [goog.async.Delay :as delay]))

(defn ^:export go [w h]
  (let [n 1]
    (js/p.size w h)
    (js/p.background 255 255 255)
    (draw 1)))

(defn draw [n]
  (js/p.fill 0 0 0)
  (js/p.stroke 255 255 255)
  (js/p.rect 0 0 js/p.width (* 20 n))
  (doto (goog.async.Delay. #(draw (+ 1 n)) (* 1000 n))
    (. start)))
