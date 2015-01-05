(ns com.myadbox.clj.components.s3.downloader
  (:require [com.myadbox.clj.util :as myadbox-util]
            [clojure.java.io :as io]
            [aws.sdk.s3 :as s3]))

(def s3-config (myadbox-util/load-config "config/s3.edn"))
(def s3-cred (:cred s3-config))
(def s3-bucket (:bucket s3-config))

(s3/bucket-exists? s3-cred s3-bucket)

(def obj-key "profiles/656/small")

(println (slurp (:content (s3/get-object s3-cred s3-bucket obj-key))))
(s3/get-object-metadata s3-cred s3-bucket obj-key)


(def root-dir "/Users/daniel/Downloads/")
(defn write-to-file [in path]
  (with-open [out (io/output-stream (io/file path))]
    (io/copy in out)))


(defn download-file-from-s3 [key]
  (let [file-ext (:content-type (s3/get-object-metadata s3-cred s3-bucket key))
        path (str root-dir "profile" ".jpg")
        s3-obj-instream (:content (s3/get-object s3-cred s3-bucket obj-key))]
    (println (class s3-obj-instream))
    (write-to-file s3-obj-instream path)))


(download-file-from-s3 obj-key)
