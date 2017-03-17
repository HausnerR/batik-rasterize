(ns batik.rasterize
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.awt RenderingHints]
           [java.io File ByteArrayInputStream ByteArrayOutputStream]
           [java.nio.charset StandardCharsets]
           [org.apache.batik.anim.dom SAXSVGDocumentFactory]
           [org.apache.batik.transcoder TranscoderInput TranscoderOutput SVGAbstractTranscoder]
           [org.apache.batik.transcoder.image PNGTranscoder JPEGTranscoder TIFFTranscoder]))

(defn- get-high-quality-hints []
  (let [add-hint (fn [hints k v] (.add hints (RenderingHints. k v)))]
    (doto (RenderingHints. nil nil)
      (add-hint RenderingHints/KEY_ALPHA_INTERPOLATION RenderingHints/VALUE_ALPHA_INTERPOLATION_QUALITY)
      (add-hint RenderingHints/KEY_INTERPOLATION       RenderingHints/VALUE_INTERPOLATION_BICUBIC)
      (add-hint RenderingHints/KEY_ANTIALIASING        RenderingHints/VALUE_ANTIALIAS_ON)
      (add-hint RenderingHints/KEY_COLOR_RENDERING     RenderingHints/VALUE_COLOR_RENDER_QUALITY)
      (add-hint RenderingHints/KEY_DITHERING           RenderingHints/VALUE_DITHER_DISABLE)
      (add-hint RenderingHints/KEY_RENDERING           RenderingHints/VALUE_RENDER_QUALITY)
      (add-hint RenderingHints/KEY_STROKE_CONTROL      RenderingHints/VALUE_STROKE_PURE)
      (add-hint RenderingHints/KEY_FRACTIONALMETRICS   RenderingHints/VALUE_FRACTIONALMETRICS_ON)
      (add-hint RenderingHints/KEY_TEXT_ANTIALIASING   RenderingHints/VALUE_TEXT_ANTIALIAS_OFF))))

(defn- high-quality-png-transcoder []
  (proxy [PNGTranscoder] []
    (createRenderer []
      (let [renderer (proxy-super createRenderer)]
        (.setRenderingHints renderer (get-high-quality-hints))
        renderer))))

(defn- high-quality-tiff-transcoder []
  (proxy [TIFFTranscoder] []
    (createRenderer []
      (let [renderer (proxy-super createRenderer)]
        (.setRenderingHints renderer (get-high-quality-hints))
        renderer))))

(defn- high-quality-jpeg-transcoder []
  (proxy [JPEGTranscoder] []
    (createRenderer []
      (let [renderer (proxy-super createRenderer)]
        (.setRenderingHints renderer (get-high-quality-hints))
        renderer))))

(defn- document-dimensions [svg-document]
  (let [root-element (-> svg-document .getRootElement)]
    {:width  (-> root-element .getWidth .getCheckedValue)
     :height (-> root-element .getHeight .getCheckedValue)}))

(defn parse-svg-uri [uri]
  (let [factory (SAXSVGDocumentFactory. "org.apache.xerces.parsers.SAXParser")]
    (.createDocument factory uri)))

(defn parse-svg-string [s]
  (let [factory (SAXSVGDocumentFactory. "org.apache.xerces.parsers.SAXParser")]
    (with-open [in (ByteArrayInputStream. (.getBytes s StandardCharsets/UTF_8))]
      (.createDocument factory nil in))))

(defn render-svg-document
  ([svg-document filename]
   (render-svg-document svg-document filename {}))
  ([svg-document filename options]
   (let [extenstion (keyword (last (str/split (str filename) #"\.")))
         dimensions (document-dimensions svg-document)

         type (or (:type options) extenstion)
         quality (or (:quality options) 0.95)
         scale (or (:scale options) 1)
         width (or (:width options) (:width dimensions))

         transcoder (case type
                      (:jpeg :jpg) (high-quality-jpeg-transcoder)
                      (:tiff :tif) (high-quality-tiff-transcoder)
                      (:png) (high-quality-png-transcoder)
                      nil)]

     (cond
       (not transcoder)
       (throw (ex-info "Cannot transcode - unable to select transcoder for reqested type"
                       {:options options}))
       (not width)
       (throw (ex-info "Cannot transcode - can't determine SVG document width"
                       {:options    options
                        :dimensions dimensions})))

     (when (instance? JPEGTranscoder transcoder)
       (.addTranscodingHint transcoder JPEGTranscoder/KEY_QUALITY (float quality)))

     (.addTranscodingHint transcoder SVGAbstractTranscoder/KEY_WIDTH (float (* scale width)))

     (with-open [out-stream (if filename
                              (io/output-stream filename)
                              (ByteArrayOutputStream.))]
       (let [in (TranscoderInput. svg-document)
             out (TranscoderOutput. out-stream)]
         (.transcode transcoder in out)
         (or filename (.toByteArray out-stream)))))))

(defn render-svg-uri
  ([uri filename]
   (render-svg-document (parse-svg-uri uri) filename {}))
  ([uri filename options]
   (render-svg-document (parse-svg-uri uri) filename options)))

(defn render-svg-string
  ([uri filename]
   (render-svg-document (parse-svg-string uri) filename {}))
  ([uri filename options]
   (render-svg-document (parse-svg-string uri) filename options)))