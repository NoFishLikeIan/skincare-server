(ns skincare_server.utils
  (:require [monger.conversion :refer [from-db-object]]))

(defn stringify-mongo-map [map] (assoc map :_id (str (map :_id))))

(defn from-db-array-to-obj-array [array-like] (map (fn [cat] (from-db-object cat true)) array-like))