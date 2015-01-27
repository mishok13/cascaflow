(defproject cascaflow "0.1.0-SNAPSHOT"
  :description "Clojure wrapper for Google's Dataflow"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.google.cloud.dataflow/google-cloud-dataflow-java-sdk-all "0.3.150109"]]
  :aot :all
  :main ^:skip-aot cascaflow.dataflow.wordcount)
