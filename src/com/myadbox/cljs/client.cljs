(ns com.myadbox.cljs.client
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer [html] :include-macros true]
            [cljs.core.async :as async :refer [chan <! put!]]
            [ajax.core :as ajax]))

(def server-chan (chan))

(ajax/GET "http://localhost:3000/"
          {:handler (fn [response]
                      (put! server-chan response))})
(def app-state
  (atom
    {:people
     [{:type :student :first "Ben" :last "Bitdiddle" :email "benb@mit.edu"}
      {:type :student :first "Alyssa" :middle-initial "P" :last "Hacker"
       :email "aphacker@mit.edu"}
      {:type :professor :first "Gerald" :middle "Jay" :last "Sussman"
       :email "metacirc@mit.edu" :classes [:6001 :6946]}
      {:type :student :first "Eva" :middle "Lu" :last "Ator" :email "eval@mit.edu"}
      {:type :student :first "Louis" :last "Reasoner" :email "prolog@mit.edu"}
      {:type :professor :first "Hal" :last "Abelson" :email "evalapply@mit.edu"
       :classes [:6001]}]
     :classes
     {:6001 "The Structure and Interpretation of Computer Programs"
      :6946 "The Structure and Interpretation of Classical Mechanics"
      :1806 "Linear Algebra"}}))


(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as contact}]
  (str last ", " first (middle-name contact)))

(defmulti entry-view (fn [person _] (:type person)))

(defn student-view [student owner]
  (reify
    om/IRender
    (render [_]
      (dom/li nil (display-name student)))))

(defn professor-view [professor owner]
  (reify
    om/IRender
    (render [_]
      (dom/li nil
              (dom/div nil (display-name professor))
              (dom/label nil "Classes")
              (apply dom/ul nil
                     (map #(dom/li nil %) (:classes professor)))))))

(defmethod entry-view :student
  [person owner] (student-view person owner))

(defmethod entry-view :professor
  [person owner] (professor-view person owner))

(defn people [app]
  (->> (:people app)
       (mapv (fn [person]
               (if (:classes person)
                 (update-in person [:classes]
                            (fn [cs] (mapv (:classes app) cs)))
                 person)))))

(defn registry-view [cursor owner]
  (reify
    om/IRenderState
    (render-state [_ state]
      (.log js/console cursor)
      (.log js/console state)
      (dom/div #js {:id "registry"}
               (dom/h2 nil "Registry")
               (apply dom/ul nil
                      (om/build-all entry-view (people cursor)))))))

(om/root
  registry-view
  app-state
  {:target (. js/document (getElementById "registry"))})
