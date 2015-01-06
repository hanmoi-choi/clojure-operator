(ns com.myadbox.clj.components.imageconverter.image-magic-wrapper
  (:require [clojure.java.shell :only [sh] :as sh :reload true]))

(def allowed-convert-args [:density :resize :src :dst])

(defn to-shell-args [args]
  (let [opts (dissoc args :src :dst)
        convert-opts (mapv (fn [[k v]] [(str "-" (name k)) v]) opts)
        src (:src args)
        dst (:dst args) ]
    (flatten (conj convert-opts src dst))))


(defn imagemagic-convert
  [opts]
  {:pre (every? #(contains? opts %) allowed-convert-args)}                         ;;only allowed args
  (let [convert-args (to-shell-args opts)]
    (apply sh/sh "convert" convert-args)
    (println "finished converting!!")))


