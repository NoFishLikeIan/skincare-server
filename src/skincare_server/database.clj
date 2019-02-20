(ns skincare_server.database
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.conversion :refer [from-db-object]]
            [monger.result :refer [acknowledged?]]
            [dotenv :refer [env]])
  (:import [org.bson.types ObjectId]))

(def db-name (or (env "DB_NAME") "test"))
(def collection-name (or (env "COLLECTION_NAME") "test"))

(def conn (mg/connect))
(def db (mg/get-db conn db-name))

;; Retrieves data given a query object
(defn mongo-single-query-factory
  "A factory that abstracts the monger query options"
  [db collection-name]
  (fn [param query] (mc/find db collection-name {param query})))

;; Connects to default port
(defn mongo-pusher-factory
  "A factory that returns a function that pushes onto mongo"
  [db collection-name]
  (fn [post-data]
    (let [post (assoc post-data :_id (ObjectId.))
          document (mc/insert-and-return db collection-name post)]
      document)))

(def default-query (mongo-single-query-factory db collection-name))
(def default-push (mongo-pusher-factory db collection-name))

(defn recover-data-by-unix
  ([from]
   (let
     [response (default-query :time {"$gte" from})]
     (map (fn [cat] (from-db-object cat true)) response)))
  ([from to]
   (let
     [response (default-query :time {"$gte" from "$lte" to})]
     (map (fn [cat] (from-db-object cat true)) response))))