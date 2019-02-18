(ns skincare_server.database
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [dotenv :refer [env]])
  (:import [org.bson.types ObjectId]))

;; Connects to default port
(defn mongo-pusher-factory
  "A factory that returns a function that pushes onto mongo"
  [& [dbname collection-name]]
  (let [dbname (or dbname (env "DB_NAME"))
        collection-name (or collection-name (env "COLLECTION_NAME"))
        conn (mg/connect)
        db (mg/get-db conn dbname)]
    (fn [post-data]
      (let [post (assoc post-data :_id (ObjectId.))
            document (mc/insert-and-return db collection-name post)]
        document))))


(def default-push (mongo-pusher-factory))