(ns com.myadbox.clj.components.imageconverter.image-magic-wrapper1
  (:require [clojure.java.shell :only [sh] :as sh :reload true]))

(def allowed-convert-args [:density :resize :src :dest])

(defn imagemagic-convert
  {:pre (every? #(contains? opts %) allowed-convert-args)}                         ;;only allowed args
  [opts]
  (letfn [(convert [&args] (sh/sh "convert" args))]
    (apply convert opts src dest)))

(imagemagic-convert
  {:density "400"
   :resize "25%"
   :src "/Users/daniel/Downloads/cheatsheet-usletter-color.pdf"
   :dest "/Users/daniel/Downloads/image.jpg"
   })

(map #(name %) (keys
                 {:density "400"
                  :resize  "25%"
                  :src "/Users/daniel/Downloads/cheatsheet-usletter-color.pdf"
                  :dest "/Users/daniel/Downloads/image.jpg"}))



