(ns metric-time
  (:require [goog.Timer :as timer]
            [goog.events :as events]))

(def fps 60)
(def background-color [255 255 255])
(def foreground-color [0 0 0])

(defn ^:export go [w h]
  (js/p.size w h)
  (events/listen (doto (goog.Timer. (/ 1000 fps))
                   (. start))
                 goog.Timer/TICK draw))

;;; Scale normalized output to size of canvas
(defn scaled [[x y]]
  [(* x js/p.width) (* y js/p.height)])

;;; Draw and scale a two-point processing function
(defn process [fun pa pb]
  (apply fun (concat (scaled pa) (scaled pb))))

;;; Execute a function in a translated and rotated environment
(defn transformed [fun & args]
  (js/p.pushMatrix)
  (js/p.translate (/ js/p.width 2) (/ js/p.height 2))
  (js/p.rotate (- js/p.HALF_PI))
  (apply fun args)
  (js/p.popMatrix))

(defn replace-html [id html]
  (set! (. (js/document.getElementById id) -innerHTML) html))

;; Can certainly make the cleanup more efficient by only clearing dirty areas
(defn cleanup []
  (apply js/p.fill background-color)
  (apply js/p.stroke background-color)
  (process js/p.rect [0 0] [1 1]))

(defn clock-base []
  (js/p.noFill)
  (apply js/p.stroke foreground-color)

  ; Outline
  (js/p.strokeWeight 2)
  (dotimes [i 2] (process js/p.ellipse [0 0] (repeat 2 (/ (+ i 1) 3))))

  ; Notches (would look a bit cleaner if Ratio were supported in CLJS)
  (js/p.strokeWeight 1)
  (let [a (/ js/p.TWO_PI 100)
        end (/ 11 30)]
    (dotimes [i 100]
      (let [start (if (= 0 (mod i 10)) (/ 9 30) (/ 3))]
        (process js/p.line [start 0]  [end 0])
        (js/p.rotate a)))))

(defn clock-hands [day-frac]
  (let [period-frac (- (* 100 day-frac) (int (* 100 day-frac)))]
    (js/p.pushMatrix)
    (js/p.strokeWeight 2)
    (js/p.rotate (* period-frac js/p.TWO_PI))
    (process js/p.line [0 0] [(/ 11 30) 0])
    (js/p.popMatrix))
  (js/p.strokeWeight 3)
  (js/p.rotate (* day-frac js/p.TWO_PI))
  (process js/p.line [0 0] [(/ 11 30) 0]))

(defn jdn [ms]
  (int (+ (/ ms 86400000) 2440587.5)))

(defn draw []
  (let [now (js/Date.)
        ;; Calculate day fraction manually to avoid time zone fiddling
        day-frac (/ (+ (* 60 (+ (* 60 (. now getHours))
                                (. now getMinutes)))
                       (. now getSeconds))
                    86400)]
    (cleanup)
    (transformed clock-base)
    (transformed clock-hands day-frac)
    (replace-html "frac" (js/p.nf (* 100 day-frac) 2 3))
    (replace-html "day" (jdn (. now getTime)))))
