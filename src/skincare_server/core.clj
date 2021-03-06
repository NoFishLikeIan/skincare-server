(ns skincare-server.core
  (:gen-class)
  (:require [org.httpkit.server :as http-server]
            [clj-time.local :as l]
            [clj-time.coerce :as time-utils]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [skincare_server.database :refer [default-push recover-data-by-unix recover-data-by-product]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :only [response]]
            [dotenv :refer [env]]
            [skincare_server.utils :refer [stringify-mongo-map]]))

(def port (or (env "PORT") 8000))

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
    [from (get-in request [:body "from"])
     to (get-in request [:body "to"])
     response (if (nil? to) (recover-data-by-unix from) (recover-data-by-unix from to))
     parsed-response (map stringify-mongo-map response)]
    {:status 200 :body {:data parsed-response}}))

(defn handle-product-request
  [request]
  (let
    [fields (get-in request [:body "products"] [])
     response (if (empty? fields) {:status 422 :body {:error "no fields passed"}}
                                  {:status 200 :body {:data (map stringify-mongo-map (recover-data-by-product fields))}})]
      response))


(defroutes app-routes
           (GET "/" [] "Maronn, I am running! Try cache me!")
           (POST "/post-data" req (handle-push-data req))
           (GET "/filter-time" req (handle-time-filter req))
           (GET "/filter-product" req (handle-product-request req))
           (route/not-found "404 page not found"))

(def app
  (-> (handler/site app-routes)
      (wrap-json-body)
      (wrap-json-response)))

(defn -main []
  (http-server/run-server  app {:port port})
  (println (format "Server started on port %s" port)))
  
