(ns proxinius.api
  (:require [full.core.log :as log]
            [full.core.config :refer [opt] :as config]
            [full.core.dev :refer [start-nstracker]]
            [full.http.server :as serv]
            [full.http.client :refer [req> raw-json-response-parser]]
            [full.async :refer [<?]])
  (:gen-class))

(def port (opt :port :default 8080))
(def request-logger (org.slf4j.LoggerFactory/getLogger "proxinius.request"))

(serv/defroutes app-routes)

(defn -main [& _]
  (config/configure)
  (log/configure)
  (start-nstracker)
  (serv/run-server (serv/json-api #'app-routes :logger request-logger)
                   {:port @port})
  (println "proxinius running on" @port))
