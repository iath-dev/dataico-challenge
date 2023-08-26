(ns core
  (:require [clojure.spec.alpha :as s]
            [clojure.data :as d]
            [clojure.data.json :as json]
            [invoice-spec :as spec]))

; Problem 1

(def filename "invoice.edn")                                ; Invoice EDN filepath
(def invoice (clojure.edn/read-string (slurp filename)))    ; Invoice element

(defn val-tax
  "Method to validate the tax values"
  [{taxes :taxable/taxes}]
  (->> taxes (some #(= (:tax/rate (into {} %)) 19))))

(defn val-ret
  "Method to validate the tax values"
  [{refs :retentionable/retentions}]
  (->> refs (some #(= (:retention/rate (into {} %)) 1))))

(defn concat-vector
  "Method to take the difference between two vectors"
  [one two]
  (concat (clojure.set/difference one two) (clojure.set/difference one two)))

(defn fil-items
  "Method to filter the items"
  ([]
   (fil-items invoice))
  ([invoice]
    (def items (->> invoice (:invoice/items)))
    (def taxes (->> items (filter val-tax) (map :invoice-item/id) (into [])))
    (def refs (->> items (filter val-ret) (map :invoice-item/id) (into [])))
    (def res (->> taxes (d/diff refs) (take 2) (concat) (flatten) (filter #(some? %)) (into [])))
    (str "Valid items:\t" res)))

; Problem 2

(defn json-invoice
  "Get invoice data from the json"
  [file]
  (def -json (json/read-str (slurp file)))
  (s/valid? ::spec/invoice -json))

(defn -main
  "Project Main"
  [& args]
  fil-items)
