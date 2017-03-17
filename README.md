# batik-rasterize

Clojure batik wrapper for rasterizing SVG graphics.

Inspired by Clojure dali library.

## Usage

Add dependency

```clojure
[batik-rasterize "0.1.2"]
```

require it

```clojure
(require '[batik.rasterize :as b])
```

and rasterize SVGs!

## Functions

```clojure
(b/parse-svg-uri "https://github.com/HausnerR/batik-rasterize/blob/master/clj_logo.svg")
(b/parse-svg-uri "clj_logo.svg")
```

Creates batik SVG document object from file/uri


```clojure
(b/parse-svg-string "<?xml version="1.0" encoding="UTF-8" standalone="no"?><svg>...</svg>")
```

Creates batik SVG document object from given SVG string


```clojure
(b/render-svg-document document filename options)
```

Render rasterized version of SVG ```document```, and save it at ```filename``` path.

Filename can be ```nil``` then result is returned as byte-array.

Available optional ```options```:

- ```:width``` - define output image width. By default it tries to determine width from source SVG,
- ```:scale``` - you can define scale instead of width (2 times bigger, 0.5 times bigger etc.),
- ```:type``` - output type. Possible values ```:jpg, :jpeg, :tif, :tiff, :png```. Default by output extension,
- ```:quality``` - define JPEG output quality. By default 95%


```clojure
(b/render-svg-uri "clj_logo.svg" filename options)
```

Conjunction of parsing uri & rendering in one function


```clojure
(b/render-svg-string "<?xml version="1.0" encoding="UTF-8" standalone="no"?><svg>...</svg>" filename options)
```

Conjunction of parsing SVG string & rendering in one function

## Known bugs

Due to bug in batik library, **exporting to JPEG doesn't work :(**


## License

Copyright © 2017 Jakub Pachciarek

Distributed under the Apache License version 2.0.
