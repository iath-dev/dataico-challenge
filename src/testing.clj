(ns testing
  (:require [invoice-item]
            [clojure.test :refer :all]))

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

(deftest invoice
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
