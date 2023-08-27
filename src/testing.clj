(ns testing
  (:require [invoice-item]
            [core]
            [clojure.test :refer :all]))

(deftest val-tax-item
  (testing "Validate tax item"
    (testing "True output"
      (is (->> {:tax/rate 19 :tax/category :iva} (core/val-tax-item) (true?))))
    (testing "False output"
      (is (->> {:tax/rate 20 :tax/category :iva} (core/val-tax-item) (false?))))
      (is (->> {:tax/rate 20 :tax/category :test} (core/val-tax-item) (false?)))
      (is (->> {:tax/rate 20 :tax/category "iva"} (core/val-tax-item) (false?)))))

(deftest val-ret-item
  (testing "Validate tax item"
    (testing "True output"
      (is (->> {:retention/rate 1 :retention/category :ret_fuente} (core/val-ret-item) (true?))))
    (testing "False output"
      (is (->> {:retention/rate 2 :retention/category :ret_fuente} (core/val-ret-item) (false?)))
      (is (->> {:retention/rate 2 :retention/category :ret} (core/val-ret-item) (false?)))
      (is (->> {:retention/rate 2 :retention/category "ret_fuente"} (core/val-ret-item) (false?))))))

(def test-duplicate-one {:invoice-item/id          "ii1"
                         :invoice-item/sku         "SKU 1"
                         :taxable/taxes            [{:tax/id       "t1"
                                                     :tax/category :iva
                                                     :tax/rate     19}]
                         :retentionable/retentions [{:retention/id       "r1"
                                                     :retention/category :ret_fuente
                                                     :retention/rate     1}]})
(def test-duplicate-two {:invoice-item/id          "ii1"
                         :invoice-item/sku         "SKU 1"
                         :taxable/taxes            [{:tax/id       "t1"
                                                     :tax/category :iva
                                                     :tax/rate     19}]})
(def test-duplicate-three {:invoice-item/id          "ii1"
                         :invoice-item/sku         "SKU 1"
                         :retentionable/retentions [{:retention/id       "r1"
                                                     :retention/category :ret_fuente
                                                     :retention/rate     1}]})

(deftest check-duplicate
  (testing "Validate duplicate filter"
    (testing "Invalid out"
      (is (->> test-duplicate-one (core/check-duplicate) (false?))))
    (testing "Valid out"
      (is (->> test-duplicate-two (core/check-duplicate) (true?)))
      (is (->> test-duplicate-three (core/check-duplicate) (true?))))))

(def filename "invoice.edn")                                ; Invoice EDN filepath
(def invoice (->> filename (slurp) (clojure.edn/read-string)))    ; Invoice element

(deftest filtrate-item
  (testing "Method to filtrate valid invoice items"
    (testing "Valid input"
      (is (->> invoice (core/filtrate-item) (map :invoice-item/id) (into []) (compare ["ii3" "ii4"]) (= 0))))))

;;; Problem 2

(deftest key-namespace
  (testing "Valid input"
    (is (= ":a/b" (core/key-namespace "a" "b")))
    (is (= ":a/b" (core/key-namespace :a :b)))))

;;; Problem 3

(def invoice-basic {:invoice-item/precise-quantity 10.0,
                    :invoice-item/precise-price 2.0,
                    :invoice-item/discount-rate 50.0})

(def invoice-minus {:invoice-item/precise-quantity 15.0,
                    :invoice-item/precise-price 3.0,
                    :invoice-item/discount-rate -50.0})

(def invoice-no-discount {:invoice-item/precise-quantity 5.0,
                          :invoice-item/precise-price 3.0})

(def invoice-error {:invoice-item/precise-quantity "10.0",
                    :invoice-item/precise-price 2.0,
                    :invoice-item/discount-rate 50.0})

(deftest three
  (testing "Subtotal method inside invoice-item"
    (testing "Empty input"
      (is (thrown? Exception (invoice-item/subtotal {}))))
    (testing "Invalid input"
      (is (thrown? Exception (invoice-item/subtotal invoice-error))))
    (testing "Valid input"
      (is (->> (invoice-item/subtotal invoice-basic) (= 10.0))))
    (testing "Input without discount field"
      (is (->> (invoice-item/subtotal invoice-no-discount) (= 15.0))))
    (testing "Input with a minus discount"
      (is (->> (invoice-item/subtotal invoice-minus) (= 67.5))))))

(run-tests 'testing)
