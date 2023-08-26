(ns core
  (:require [clojure.spec.alpha :as s]
            [clojure.data.json :as json]
            [invoice-spec :as spec]
            [cheshire.core :as c]
            [clojure.edn :as edn])
  (:gen-class))

; Problem 1

(def filename "invoice.edn")                                ; Invoice EDN filepath
(def invoice (->> filename (slurp) (clojure.edn/read-string)))    ; Invoice element

(defn val-tax-item
  "Method to validate a tax item"
  [{rate :tax/rate cat :tax/category}]
  (def vrate (= rate 19))
  (def vcat (= cat :iva))
  (and vrate vcat))

(defn val-tax
  "Method to validate the tax values"
  [{taxes :taxable/taxes}]
  (->> taxes (some val-tax-item)))

(defn val-ret-item
  [{rate :retention/rate cat :retention/category}]
  (def vrate (= rate 1))
  (def vcat (= cat :ret_fuente))
  (and vrate vcat))

(defn val-ret
  "Method to validate the tax values"
  [{refs :retentionable/retentions}]
  (->> refs (some val-ret-item)))

(defn val-item
  "Method to validate an invoice item"
  [item]
  (def tax (val-tax item))
  (def ret (val-ret item))
  (or tax ret))

(defn check-duplicate
  "Method to check for item that have both conditions"
  [item]
  (def tax (val-tax item))
  (def ret (val-ret item))
  (not (= tax ret)))

(defn filtrate-item
  "Method to filter the item"
  ([]
   (filtrate-item invoice))
  ([invoice]
   (println "Search results:")
   (->> invoice (:invoice/items) (filter val-item) (filter check-duplicate))))

; Problem 2

(defn json-invoice
  "Get invoice data from the json"
  ([]
   (println "Missing file name - using the \"invoice.json\" as default")
   (json-invoice "invoice.json"))
  ([file]
    (def -json (->> file (slurp) (json/read-str) (into {})))
    ;(def -json (->> file (slurp) (c/parse-string)))
    ;(def -json (->> file (slurp) (json/read-str) (into {}) (prn-str)))
    (println -json)
    (println (s/explain ::spec/invoice -json))
    (s/valid? ::spec/invoice -json)))

(defn -main
  "Project Main"
  [& args]
  (println args))
