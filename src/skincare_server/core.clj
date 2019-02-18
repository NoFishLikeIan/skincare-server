(ns skincare-server.core
  (:gen-class)
  (:require [org.httpkit.server :as http-server]
            [clj-time.local :as l]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [skincare_server.database :refer [default-push]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :only [response]]
            ))

(def port 8000)


(defn handle-push-data
  [request]
  (let
    [decorated-request (assoc request :time (str (l/local-now)))
     document (default-push decorated-request)]
    {:status 200 :body document}))

(defroutes app-routes
           (GET "/" [] "Maronn, I am running! Try cache me!")
           (POST "/post-data" req (handle-push-data req))
           (route/not-found "404 page not found"))

(def app
  (-> (handler/site app-routes)
      (wrap-json-body)
      (wrap-json-response)
      ))

(defn -main [& args]
  (http-server/run-server  app {:port port})
  (println (format "Server started on port %s" port)))
  
