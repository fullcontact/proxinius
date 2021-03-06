(defproject fullcontact/proxinius "latest"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [fullcontact/full.bootstrap "0.10.3"]]
  :main proxinius.api
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}
  :resource-paths ["resources"]
  :target-path "target/%s"
  :plugins [[lein-bikeshed "0.3.0"]
            [lein-cloverage "1.0.7"]
            [jonase/eastwood "0.2.3"]]
  :repl-options {:init-ns user}
  :profiles {:uberjar {:aot [proxinius.api]}
             :dev {:source-paths ["dev"]}})
