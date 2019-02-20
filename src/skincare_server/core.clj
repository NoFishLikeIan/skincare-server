(ns skincare-server.core
  (:gen-class)
  (:require [org.httpkit.server :as http-server]
            [clj-time.local :as l]
            [clj-time.coerce :as time-utils]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [skincare_server.database :refer [default-push recover-data-by-unix]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :only [response]]
            [dotenv :refer [env]]
            [skincare_server.utils :refer [stringify-mongo-map]]))

(def port (or (env "PORT") 8000))
(def really-big-unix 32503680000)

(defn handle-push-data
  [request]
  (let
    [body-request (get request :body)
     decorated-request (assoc body-request :time (time-utils/to-long (l/local-now)))
     document (default-push decorated-request)
     mongoId (str (get document :_id))
     response {:mongoId mongoId :data decorated-request}]
    {:status 200 :body response}))

(defn handle-time-filter
  [request]
  (let
    [body-request (get request :body)
     from (get body-request :from)
     to (get body-request :to)
     response (if (nil? to) (recover-data-by-unix from) (recover-data-by-unix from to))
     parsed-response (stringify-mongo-map response)]
    {:status 200 :body {:data parsed-response}}))

(defroutes app-routes
           (GET "/" [] "Maronn, I am running! Try cache me!")
           (POST "/post-data" req (handle-push-data req))
           (GET "/filter-time" req (handle-time-filter req))
           (route/not-found "404 page not found"))

(def app
  (-> (handler/site app-routes)
      (wrap-json-body)
      (wrap-json-response)
      ))

(defn -main [& args]
  (http-server/run-server  app {:port port})
  (println (format "Server started on port %s" port)))
  
