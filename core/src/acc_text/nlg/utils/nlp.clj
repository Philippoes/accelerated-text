(ns acc-text.nlg.utils.nlp
  (:require [clojure.string :as str]))

(defn split-into-sentences [s]
  (str/split s #"(?<![\p{L}\p{N}]\.[\p{L}\p{N}].)(?<![A-Z][a-z]\.)(?<=\.|\?)\s"))

(defn tokenize [s]
  (map first (re-seq #"(([\p{L}\p{N}][.,-_][\p{L}\p{N}]+)|(:([\p{L}\p{N}]|[_-])+:)|(\{\{([\p{L}\p{N}]|[_-])+\}\})|([\p{L}\p{N}'`]+|[^\s\p{L}\p{N}'`]+))" s)))

(defn tokenize-incl-space [s]
  (map first (re-seq #"([\p{L}\p{N}'`]+|[\s]+|[^\s\p{L}\p{N}'`]+)" s)))

(defn word? [s]
  (some? (re-seq #"[\p{L}\p{N}]" s)))

(defn ends-with-s? [token] (= "s" (str (last token))))

(defn starts-with-capital? [[s & _]]
  (and (Character/isLetter s)  (= (str s) (str/upper-case s))))

(defn token-type [token] (if (word? token) "WORD" "PUNCTUATION"))

(defn capitalize-first-word [[head & tail]]
  (str/join [(str/capitalize head) (apply str tail)]))

(defn wrap-sentence [s]
  (cond-> (str/trim s)
          (re-find #"[^.?!\s]\s*$" s) (str ".")))

(defn process-sentence [s]
  (if-not (str/blank? s)
    (wrap-sentence
     (capitalize-first-word s))
    ""))


(defn clean-whitespace-before-punct
  [text]
  (str/replace text #"\s(?:[.,!:?])" #(str/trim %1)))

(defn rebuild-sentences [tokens]
  (->> (str/join " "  tokens)
       (split-into-sentences)
       (map clean-whitespace-before-punct)
       (map str/trim)
       (map process-sentence)
       (str/join " ")))

(defn annotate [text]
  {:text   text
   :tokens (loop [[token & tokens] (tokenize-incl-space text)
                  idx 0
                  annotations []]
             (if (nil? token)
               annotations
               (recur tokens (+ idx (count token)) (cond-> annotations
                                                     (re-matches #"[^\s]+" token) (conj {:text token
                                                                                         :idx  idx})))))})
