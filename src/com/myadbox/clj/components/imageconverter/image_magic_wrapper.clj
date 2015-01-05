(ns com.myadbox.clj.components.imageconverter.image-magic-wrapper1
  (:require [clojure.java.shell :only [sh] :as sh :reload true]))

(def allowed-convert-args [:density :resize :src :dst])

(defn imagemagic-convert
  #_{:pre (every? #(contains? opts %) allowed-convert-args)}                         ;;only allowed args
  [opts]
  (let [convert-args (to-shell-args opts)]
    (apply sh/sh "convert" convert-args)))

(imagemagic-convert test-arg)
(def test-arg {:density "400"
               :resize "25%"
               :src "/Users/daniel/Downloads/cheatsheet-usletter-color.pdf"
               :dst "/Users/daniel/Downloads/image.jpg"
               })

#_(defn get-opts [args]
  (let [opts ()]))

(defn to-shell-args [args]
  (let [opts (dissoc test-arg :src :dst)
        convert-opts (mapv (fn [[k v]] [(str "-" (name k)) v]) opts)
        src (:src args)
        dst (:dst args) ]
     (flatten (conj convert-opts src dst))))

(to-shell-args test-arg)

(:src test-arg)

(dissoc test-arg :src :dst)
(map #(name %) (keys
                 {:density "400"
                  :resize  "25%"
                  :src "/Users/daniel/Downloads/cheatsheet-usletter-color.pdf"
                  :dst "/Users/daniel/Downloads/image.jpg"}))




