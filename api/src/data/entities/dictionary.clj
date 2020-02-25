(ns data.entities.dictionary
  (:require [api.config :refer [conf]]
            [clj-yaml.core :as yaml]
            [clojure.string :as str]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [acc-text.nlg.dictionary.morphology :as morphology])
  (:import (java.io File)))

(defstate reader-flags-db :start (db/db-access :reader-flag conf))
(defstate dictionary-combined-db :start (db/db-access :dictionary-combined conf))
(defstate dictionary-multilang-db :start (db/db-access :dictionary-multilang conf))


(defn list-readers []
  (db/list! reader-flags-db 100))

(defn get-default-flags []
  {:English  :YES
   :Estonian :NO})

(defn get-reader [key]
  (db/read! reader-flags-db key))

(defn list-dictionary []
  (db/list! dictionary-combined-db 100))

(defn get-dictionary-item [key]
  (when-not (str/blank? key)
    (db/read! dictionary-combined-db key)))

(defn text->phrase
  ([text parent-id default-usage]
   (text->phrase text parent-id default-usage (get-default-flags)))
  ([text parent-id default-usage default-flags]
   {:id    (format "%s/%s" parent-id (utils/gen-uuid))
    :text  text
    :flags (assoc default-flags :default default-usage)}))

(defn create-dictionary-item [{:keys [key name phrases partOfSpeech]}]
  (when-not (str/blank? name)
    (db/write! dictionary-combined-db key {:name         name
                                           :partOfSpeech partOfSpeech
                                           :phrases      (map #(text->phrase % key :YES) phrases)})))

(defn delete-dictionary-item [key]
  (db/delete! dictionary-combined-db key))

(defn update-dictionary-item [item]
  (db/update! dictionary-combined-db (:key item) (dissoc item :key)))

(defn list-dict-files []
  (->> (file-seq (io/file (or (System/getenv "DICT_PATH") "grammar/dictionary")))
       (filter #(.isFile ^File %))
       (filter #(str/ends-with? (.getName %) "yaml"))))

(defn create-multilang-dict-item [{::morphology/keys [key language gender pos sense tenses inflections]}]
  (db/write! dictionary-multilang-db (utils/gen-uuid) {:key         key
                                                       :language    language
                                                       :gender      gender
                                                       :pos         pos
                                                       :sense       sense
                                                       :tenses      tenses
                                                       :inflections inflections}))

(defn get-multidict-items [key]
  (db/read! dictionary-multilang-db key))

(defn search-multilang-dict [key pos senses]
  (db/scan! dictionary-multilang-db {:key key :pos pos :senses senses}))

(defn list-multilang-dict [limit]
  (db/list! dictionary-multilang-db limit))

(defn initialize []
  (->> (file-seq (io/file (or (System/getenv "DICT_PATH") "grammar/dictionary")))
       (filter #(.isFile ^File %))
       (filter #(str/ends-with? (.getName %) "edn"))
       (map #(edn/read-string (slurp (io/file %))))
       (flatten)
       (map #(create-multilang-dict-item %))))
