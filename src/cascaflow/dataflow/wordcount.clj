(ns cascaflow.dataflow.wordcount
  (:require [clojure.string :as str])
  (:import [com.google.cloud.dataflow.sdk Pipeline]
           [com.google.cloud.dataflow.sdk.transforms Count$PerElement]
           [com.google.cloud.dataflow.sdk.values TypedPValue]
           [com.google.cloud.dataflow.sdk.coders VarLongCoder]
           [com.google.cloud.dataflow.sdk.io TextIO$Read TextIO$Write]
           [com.google.cloud.dataflow.sdk.options PipelineOptions PipelineOptionsFactory$Builder PipelineOptionsFactory]
           [com.google.cloud.dataflow.sdk.transforms Count Count$PerElement]
           [com.google.cloud.dataflow.sdk.transforms DoFn DoFn$ProcessContext]
           [com.google.cloud.dataflow.sdk.transforms PTransform ParDo]
           [com.google.cloud.dataflow.sdk.coders StringUtf8Coder])
  (:gen-class))

(defn extract-words []
  (proxy [com.google.cloud.dataflow.sdk.transforms.DoFn] []
    (processElement [cxt]
      (doseq [word (->> (-> cxt .element (str/split #"[^a-zA-Z']+"))
                        (remove nil?)
                        (remove str/blank?)
                        (map str/lower-case))]
        (.output cxt ^String word)))))

(defn format-words []
  (proxy [com.google.cloud.dataflow.sdk.transforms.DoFn] []
    (processElement [cxt]
      (.output cxt (str (.. cxt element getKey) ": " (.. cxt element getValue))))))

(defn word-count
  []
  (proxy [com.google.cloud.dataflow.sdk.transforms.PTransform] []
    (apply [lines]
      (let [string-coder (StringUtf8Coder/of)
            counter-coder (com.google.cloud.dataflow.sdk.coders.KvCoder/of (StringUtf8Coder/of) (VarLongCoder/of))]
        (.. lines
            (apply (ParDo/of (extract-words)))
            (setCoder string-coder)
            (apply (Count$PerElement. ))
            (setCoder counter-coder)
            (apply (ParDo/of (format-words)))
            (setCoder string-coder))))))

(defn run!
  "Execute "
  [args]
  (let [pipeline (-> (->> args (into-array String) PipelineOptionsFactory/fromArgs)
                     (.withValidation)
                     (.as PipelineOptions)
                     (Pipeline/create))]
    (.. pipeline
        (apply (.from (TextIO$Read/named "ReadLines") "/tmp/othello.txt"))
        (apply (word-count))
        (apply (.to (TextIO$Write/named "WriteLines") "/tmp/out.txt")))
    (.run pipeline)))

(defn -main
  []
  (run! []))
