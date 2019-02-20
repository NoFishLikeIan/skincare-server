(defn stringify-mongo-map [map]
  (assoc map :_id (str (map :_id))))