(ns user
  (:require [full.core.config :refer [opt] :as conf]
            [full.core.log :as log]
            [full.async :refer [go-try]]
            [proxinius.api :as api]
            [proxinius.mutations :as mutations]))

(conf/configure)
(log/configure)

(def mutation-percentage (opt :mutation-percentage :default 10))

(defn should-mutate? [_]
  (>= @mutation-percentage (rand-int 100)))

(defn do-random-mutation> [request]
  (go-try
   (let [mutation (rand-nth mutations/mutations)]
     (log/info "Mutating request"
               mutation
               (:uri request)
               (:headers request))
     (mutation request @api/requests))))

(api/add-request-mutation should-mutate? do-random-mutation>)
