(defproject dataico-challenge "0.1.0-SNAPSHOT"
  :description "Clojure Project - Dataico Challenge"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"] [org.clojure/data.json "2.4.0"]]
  :main ^:skip-aot core
  :target-path "target/%s"
  :test-paths ["src"]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
