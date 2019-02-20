(ns skincare_server.database
  (:require [monger.core :as mg]
            [monger.collection :as mc]
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
  (fn [param query]
    (mc/find db collection-name {param query})))

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
   (recoverDataByTime from really-big-unix))
  ([from to]
   (let
     [query {:gte from :lte to}
      response (default-query :time query)]
     response)))