(ns acc-text.nlg.core
  (:require [acc-text.nlg.gf.generator :as generator]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph.conditions :as conditions]
            [acc-text.nlp.utils :as nlp]
            [acc-text.nlg.enrich.core :as enrich]
            [clojure.tools.logging :as log]))

(defn select-context [context constants]
  (update context :amr #(reduce-kv (fn [m k v]
                                     (assoc m k (cond-> v
                                                        (contains? v :semantic-graph)
                                                        (update
                                                          :semantic-graph
                                                          (fn [sg]
                                                            (conditions/select sg constants))))))
                                   {}
                                   %)))

(defn generate-text [semantic-graph {data :data :as context} lang]
  (log/debugf "Processing generate request for `%s`..." lang)
  (log/debugf "Semantic graph: %s" semantic-graph)
  (log/debugf "Context: %s" context)
  (->> (grammar/build "Default" "Instance" (conditions/select semantic-graph data) (select-context context {:lang lang}))
       (log/spyf "Grammar: %s")
       (generator/generate lang)
       (map (comp nlp/annotate nlp/process-sentence))))

(defn enrich-text
  [context text]
  (get (enrich/enrich-request context text) :result text))
