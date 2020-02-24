(ns data.entities.dictionary
  (:require [api.config :refer [conf]]
            [clj-yaml.core :as yaml]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]
            [clojure.java.io :as io]
            [clojure.edn :as edn])
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

(defn create-multilang-dict-item [data]
  (log/debugf "Creating multilang dict item: %s" data)
  (db/write! dictionary-multilang-db (utils/gen-uuid) data))

(defn search-multilang-dict [key sense]
  (db/scan! dictionary-multilang-db {:key key :sense sense}))

(defn list-multilang-dict [limit]
  (db/list! dictionary-multilang-db limit))

(defn initialize-multilang []
  (->> (file-seq (io/file (or (System/getenv "DICT_PATH") "grammar/dictionary")))
       (filter #(.isFile ^File %))
       (filter #(str/ends-with? (.getName %) "edn"))
       (map #(edn/read-string (slurp (io/file %))))
       (flatten)
       (map #(create-multilang-dict-item %))))

(defn initialize []
  (do
    (doall (initialize-multilang))
    (doseq [f (list-dict-files)]
      (let [{:keys [phrases partOfSpeech name]} (yaml/parse-string (slurp f))
            filename (utils/get-name f)]
        (when-not (get-dictionary-item filename)
          (create-dictionary-item
           {:key          filename
            :name         (or name filename)
            :phrases      phrases
            :partOfSpeech (when (some? partOfSpeech)
                            (keyword partOfSpeech))}))))))
