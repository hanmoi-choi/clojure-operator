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
    {:contacts
     [{:first "Ben" :last "Bitdiddle" :email "benb@mit.edu"}
      {:first "Alyssa" :middle-initial "P" :last "Hacker" :email "aphacker@mit.edu"}
      {:first "Eva" :middle "Lu" :last "Ator" :email "eval@mit.edu"}
      {:first "Louis" :last "Reasoner" :email "prolog@mit.edu"}
      {:first "Cy" :middle-initial "D" :last "Effect" :email "bugs@mit.edu"}
      {:first "Lem" :middle-initial "E" :last "Tweakit" :email "morebugs@mit.edu"}]}))

(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as contact}]
  (str last ", " first (middle-name contact)))

(defn contact-view [contact owner]
  (reify
    om/IRender
    (render [_]
      (html/html
        [:li (display-name contact)])
      #_(dom/li nil (display-name contact)))))

(defn contacts-view [app owner]
  (reify
    om/IRender
    (render [this]
      (html/html
        [:div
         [:h2 "Contact list"]
         [:ul (om/build-all contact-view (:contacts app))]])
      #_(dom/div nil
               (dom/h2 nil "Contact list")
               (apply dom/ul nil
                      (om/build-all contact-view (:contacts app)))))))

(defn draw-list [app owner]
  (reify
    om/IRender
    (render [_]
      (html/html [:ul (map #(vector :li %) (:list app))]))))

(defn widget [app owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (go
        (om/set-state!
          owner
          :om-message
          (str (:compojure-message (<! server-chan)) ", and Om!"))))
    om/IRender
    (render [_]
      (html/html [:div.message
             [:div.container
              [:h1 (om/get-state owner :om-message)]
              [:p (str "If you can read the message above, then you have successfully "
                       "launched your brand-new DACOM-based webapp.")]]]))))


(om/root
  contacts-view
  app-state
  {:target (. js/document (getElementById "app"))})
