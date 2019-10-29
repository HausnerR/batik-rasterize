(defproject batik-rasterize "0.1.3-SNAPSHOT"
  :description "Clojure batik wrapper for rasterizing SVG graphics"
  :url "https://github.com/hausnerr/batik-rasterize"
  :license {:name "Apache License version 2.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [xerces/xerces "2.4.0"]
                 [org.apache.xmlgraphics/batik-transcoder "1.10"
                  :exclusions [[xerces/xercesImpl]]]
                 [org.apache.xmlgraphics/batik-codec "1.10"]
                 [org.apache.xmlgraphics/batik-anim "1.10"]
                 [org.apache.xmlgraphics/xmlgraphics-commons "2.3"]])
