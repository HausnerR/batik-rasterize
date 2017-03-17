(ns batik.rasterize-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [batik.rasterize :refer :all]))

(deftest rasterize-test-render-file
  (testing "Tests rendering to file"
    (let [svg "clj_logo.svg"
          out "clj_logo.png"
          result (render-svg-uri svg out)
          out-len (.length (io/as-file out))]

      (try
        (io/delete-file out)
        (catch Exception e nil))

      (is (and
            (pos? out-len)
            (= result out))))))


(deftest rasterize-test-render-byte-array
  (testing "Tests rendering to byte array"
    (let [svg "clj_logo.svg"
          result (render-svg-uri svg nil {:type :png})]
      (is (and
            (= (str (type result)) "class [B")
            (pos? (count result)))))))


(deftest rasterize-test-render-byte-array-no-type-provided
  (testing "Tests rendering without provided output type"
    (let [svg "clj_logo.svg"]
      (is (thrown-with-msg? Exception
                            #"unable to select transcoder for reqested type"
                            (render-svg-uri svg nil))))))


(deftest rasterize-test-render-invalid-svg
  (testing "Tests rendering of invalid SVG"
    (is (thrown-with-msg? Exception
                          #"Root element namespace does not match that requested"
                          (render-svg-string "<svg></svg>" nil {:type :png})))))
