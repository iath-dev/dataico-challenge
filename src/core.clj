(ns core
  (:require [clojure.spec.alpha :as s]
            [clojure.data :as d]
            [clojure.data.json :as json]
            [invoice-spec :as spec])
  (:gen-class))

; Problem 1

(def filename "invoice.edn")                                ; Invoice EDN filepath
(def invoice (->> filename (slurp) (clojure.edn/read-string)))    ; Invoice element

(defn val-tax
  "Method to validate the tax values"
  [{taxes :taxable/taxes}]
  (->> taxes (some #(= (:tax/rate (into {} %)) 19))))

(defn val-ret
  "Method to validate the tax values"
  [{refs :retentionable/retentions}]
  (->> refs (some #(= (:retention/rate (into {} %)) 1))))

(defn fil-items
  "Method to filter the items"
  ([]
   (fil-items invoice))
  ([invoice]
    (def items (->> invoice (:invoice/items)))
    (def taxes (->> items (filter val-tax) (map :invoice-item/id) (into [])))
    (def refs (->> items (filter val-ret) (map :invoice-item/id) (into [])))
    (def res (->> taxes (d/diff refs) (take 2) (concat) (flatten) (filter #(some? %)) (into [])))
    (println (str "Valid items:\t" res))))

; Problem 2

(defn json-invoice
  "Get invoice data from the json"
  [file]
  (def -json (json/read-str (slurp file)))
  (s/valid? ::spec/invoice -json))

(defn -main
  "Project Main"
  [& args]
  (println args))
