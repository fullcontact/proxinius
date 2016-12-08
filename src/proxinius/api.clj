(ns proxinius.api
  (:require [full.core.log :as log]
            [full.core.config :refer [opt] :as config]
            [full.core.dev :refer [start-nstracker]]
            [full.http.server :as serv]
            [full.http.client :refer [req> raw-json-response-parser]]
            [full.async :refer [go-try <?]])
  (:gen-class))

(def port (opt :port :default 8080))
(def request-logger (org.slf4j.LoggerFactory/getLogger "proxinius.request"))

(def base-url (opt :base-url))

(defonce requests (ref #{}))

(defn save-req [request response]
  (dosync (ref-set requests (conj @requests {:request request
                                             :response response}))))

(defonce request-mutations (ref #{}))

(defn add-request-mutation
  [matcher mutation]
  (dosync (ref-set request-mutations (conj @request-mutations
                                           {:matcher matcher
                                            :mutation mutation}))))

(defn remove-request-mutation
  [matcher mutation]
  (dosync (ref-set request-mutations (disj @request-mutations
                                           {:matcher matcher
                                            :mutation mutation}))))

(defn remove-request-mutations []
  (dosync (ref-set request-mutations #{})))

(defn match-and-mutate
  [request]
  (some #(when ((:matcher %) request) (:mutation %)) @request-mutations))

(defn forward-request>
  [request]
  (go-try
   (let [response (<? (req> {:base-url @base-url
                             :resource (subs (:uri request) 1)
                             :method (:request-method request)
                             :params (dissoc (:params request) :*)
                             :body (:body request)
                             :headers (dissoc (:headers request) "host")
                             :response-parser raw-json-response-parser}))]
     (save-req request response)
     {:body response})))

(serv/defroutes app-routes
  (serv/ANY
   "*" request
   (if-let [mutation> (match-and-mutate request)]
     (<? (mutation> request))
     (<? (forward-request> request)))))

(defn -main [& _]
  (config/configure)
  (log/configure)
  (start-nstracker)
  (serv/run-server (serv/json-api #'app-routes :logger request-logger)
                   {:port @port})
  (println "proxinius running on" @port))
