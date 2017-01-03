(ns proxinius.mutations)

(defn internal-server-error [& _]
  {:status 500})

(defn empty-body [& _]
  {:status 200
   :body {}})

(defn return-random-response
  "returns a random response from request-response pairs"
  [_ requests]
  (:response (get requests (rand-int (count requests)))))

(def mutations [internal-server-error empty-body return-random-response])
