(ns com.myadbox.clj.util
  (:require [clojure.java.io :as io]))

 (defn load-config [filename]
   (with-open [r (io/reader filename)]
     (read (java.io.PushbackReader. r))))

(load-config "config/s3.edn")
