(defproject batik-rasterize "0.1.2"
  :description "Clojure batik wrapper for rasterizing SVG graphics"
  :url "https://github.com/hausnerr/batik-rasterize"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [xerces/xerces "2.4.0"]
                 [org.apache.xmlgraphics/batik-transcoder "1.8"
                  :exclusions [[xerces/xercesImpl]
                               #_[batik/batik-script]]]
                 [org.apache.xmlgraphics/batik-codec "1.8"]
                 [org.apache.xmlgraphics/batik-anim "1.8"]
                 [org.apache.xmlgraphics/xmlgraphics-commons "2.0.1"]])
