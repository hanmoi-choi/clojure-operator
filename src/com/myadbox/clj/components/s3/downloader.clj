(ns com.myadbox.clj.components.s3.downloader
  (:require [com.myadbox.clj.util :as myadbox-util]
            [com.myadbox.clj.components.imageconverter.image-magic-wrapper :as converter]
            [clojure.java.io :as io]
            [clojure.core.async :as async :refer [<!! >!! >! <! put! go]]
            [aws.sdk.s3 :as s3])
  (:import (javax.imageio ImageIO)))

(def test-arg {:density "400"
               :resize "25%"
               :src "/Users/daniel/Downloads/cheatsheet-usletter-color.pdf"
               :dst "/Users/daniel/Downloads/image.jpg"
               })

(defn to-proc< [in]
  (let [out (async/chan 1)]
    (async/pipe in out)
    out))

;; These code are from Tim's screencast - https://tbaldridge.pivotshare.com/
(defn pipeline<
  "docstring"
  [desc c]
  (let [p (partition 2 desc)]
    (reduce
      (fn [prev-c [f n]]
        (-> (for [_ (range n)]
              (->
                (async/map< f prev-c)
                to-proc<))
            (async/merge)))
      c
      p))
  )

;; create your own s3.edn
(def s3-config (myadbox-util/load-config "config/s3.edn"))
(def s3-cred (:cred s3-config))
(def s3-bucket (:bucket s3-config))

(def obj-key "ads/29889/cFAiWrRiM5MFBL47p.jpg")

(def root-dir "/Users/daniel/Downloads/")
(defn copy-stream [in path]
  (with-open [out (io/output-stream (io/file path))]
    (io/copy in out)))

(def count (atom 0))
(def test-arg {:density "400"
               :resize "200%"
               })

(defn download-file [key]
  (let [s3-obj (s3/get-object s3-cred s3-bucket key)
        src-path (str root-dir (format "source-%s.jpeg" (swap! count inc)))
        dst-path (str root-dir (format "dst-%s.jpeg" @count))
        s3-obj-instream (:content s3-obj)]
    ;;(println (class s3-obj-instream))   ;; com.amazonaws.services.s3.model.S3ObjectInputStream
    (copy-stream s3-obj-instream src-path)
    (println "Finished downloading")
    (assoc test-arg :src src-path :dst dst-path)))

(def c (async/chan 20))
(for [i (range 15)]
  (go
    (>! c obj-key)
    (<! (pipeline< [download-file 10
                 converter/imagemagic-convert 10] c))))

;(def images
;  (take 10
;        (repeatedly
;          #(ImageIO/read (clojure.java.io/input-stream
;                           "/Users/blake/Pictures/blake_signature.jpg")))))
;
;(defn save-image [image filename]
;  (with-open [os (io/ByteArrayOutputStream.)]
;    (ImageIO/write image "jpg" os)
;    (let [request
;          (s3/put-object credentials bucket-name filename
;                         (io/input-stream (.toByteArray os)))]
;      {:finished true :request request})))
;
;(defn upload-images [images]
;  (doall
;    (map-indexed
;      (fn [image i]
;        (future (upload-image image (format "myimage-%s.jpg" i))))
;      images)))
