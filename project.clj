(defproject skincare-server "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.9.0"] [http-kit "2.2.0"] [clj-time "0.15.0"] [compojure "1.6.0"] [com.novemberain/monger "3.1.0"] [com.novemberain/monger "3.1.0"] [lynxeyes/dotenv "1.0.2"] [org.clojure/tools.namespace "0.2.11"] [ring/ring-json "0.4.0"]
                 ]
  :aot [skincare-server.core]
  :jvm-opts ["--add-modules" "java.xml.bind"]
  :main skincare-server.core)