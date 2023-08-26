(ns core
  (:require [clojure.spec.alpha :as s]
            [clojure.data.json :as json]
            [invoice-spec :as spec]
            [clojure.walk :as walk])
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

; TODO Couldn't transform the [invoice] element in a valid element
(defn json-invoice
  "Validate invoice item inside a json file"
  ([]
   (println "Missing file name - using the \"invoice.json\" as default")
   (json-invoice "invoice.json"))
  ([file]
    (def -json (->> file (slurp) (json/read-str) (walk/keywordize-keys) (:invoice)))
    (print "Validation result -> ")
    (s/valid? ::spec/invoice -json)))

(defn -main
  "Project Main"
  ([]
   (println "Missing input: executing first solution")
   (-main 1 "invoice.json"))
  ([option]
   (println "Missing filename: if the option is 2 the filename will be invoice.json by default")
   (-main option "invoice.json"))
  ([option file]
    (case option
      "1" (println (filtrate-item))
      "2" (println (json-invoice file))
      (println "Invalid input: the valid options are 1 or 2"))))
