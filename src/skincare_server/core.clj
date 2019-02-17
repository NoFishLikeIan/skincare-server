(ns skincare-server.core
  (:gen-class)
  (:require [org.httpkit.server :refer [run-server]]
            [clj-time.core :as t]
            [clj-time.local :as l]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [skincare_server.database :refer [default-push]]
            [ring.middleware.json :only [wrap-json-body]]
            [ring.util.response :only [response]]))

(def port 8000)
(defn handler [request]
  (let
    [[mood skin prod food period exercise] (get-in request [:mood :skin :products :food :period :exercise])]
    (save-data [mood skin prod food period exercise])
    ))

(defn handler [request]
  (let [] (get-in request [:mood :skin :products :food :period :exercise]))



(defn save-data
  [mood skin products food exercise period]
  (let [resp {:mood mood :skin skin :products products :food food :period period :exercise exercise}
        resp (assoc resp :time (str (l/local-now)))]
    (default-push resp)))


(defroutes app-routes
           (GET "/" [] "Maronn, I am running! Try cache me!")
           (POST "/post-data"  req {:status 200 :body req})
           (route/not-found "404 page not found"))

(def app
  (-> ()))

(defn -main [& args]
  (run-server app {:port port})
  (println (format "Server started on port %s" port)))
  
