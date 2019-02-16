(ns skincare-server.core
  (:gen-class)
  (:require [org.httpkit.server :refer [run-server]]
            [clj-time.core :as t]
            [clj-time.local :as l]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [skincare_server.database :refer [default-push]]
            [ring.middleware.json :refer [wrap-json-params]))
(def port 8000)


(defn save-data
  [mood skin products food exercise period]
  (let [resp {:mood mood :skin skin :products products :food food :period period :exercise exercise}
        resp (assoc resp :time (str (l/local-now)))]
    (default-push resp)))


(defroutes app
           (GET "/" [] "Maronn, I am running! Try cache me!")
           (route/not-found "404!")
           (POST "/input_value" request
             (let [{request-params :params} request
                   response {:status 200 :result done}]
               (println request))))
   

(defn -main [& args]
  (run-server app {:port port})
  (println (format "Server started on port %s" port)))
  
