(ns data.entities.dictionary
  (:require [clojure.string :as str]
            [data.db.dynamo-ops :as ops]
            [data.utils :as utils]))

(defn list-readers [] (ops/list! (ops/db-access :reader-flag) 100))

(defn get-default-flags [] (into {} (map (fn [r] {(keyword (r :id)) :DONT_CARE}) (list-readers))))

(defn get-reader
  [k]
  (ops/read! (ops/db-access :reader-flag) k))

(defn list-dictionary [] (ops/list! (ops/db-access :dictionary-combined) 100))

(defn get-dictionary-item [k]
  (when-not (str/blank? k)
    (ops/read! (ops/db-access :dictionary-combined) k)))

(defn text->phrase
  [text parent-id default-usage default-flags]
  {:id    (format "%s/%s" parent-id (utils/gen-uuid))
   :text  text
   :flags (assoc default-flags :default default-usage)})

(defn create-dictionary-item
  [{:keys [key name phrases partOfSpeech]}]
  (when-not (str/blank? name)
    (let [default-flags (get-default-flags)
          result {:name         name
                  :partOfSpeech partOfSpeech
                  :phrases      (doall (map #(text->phrase % key :YES default-flags) phrases))}]
      (ops/write! (ops/db-access :dictionary-combined) key result))))

(defn delete-dictionary-item
  [k]
  (ops/delete! (ops/db-access :dictionary-combined) k))

(defn update-dictionary-item
  [{k :key :as item}]
  (ops/update! (ops/db-access :dictionary-combined) k (dissoc item :key)))