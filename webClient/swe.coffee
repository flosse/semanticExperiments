modules = { }

init = ->

  scaleApp.register "cli", swe.modules.cli.controller,
    models:     { model:  swe.modules.cli.model   }
    views:      { view:   swe.modules.cli.view    }
    templates:  { cli:    "modules/cli/cli.html"  }

  scaleApp.register "simpleQuery", swe.modules.simpleQuery.controller,
    models:     { model:  swe.modules.simpleQuery.model     }
    views:      { view:   swe.modules.simpleQuery.view      }
    templates:  { result: "modules/simpleQuery/result.html" }

  scaleApp.register "resourceInfo", swe.modules.resourceInfo.controller,
    models:     { model:  swe.modules.resourceInfo.model  }
    views:      { view:   swe.modules.resourceInfo.view   }
    templates:  { result: "modules/resourceInfo/resourceInfo.html" }

  scaleApp.register "filter", swe.modules.filter.controller,
    models:     { model:  swe.modules.filter.model     }
    views:      { view:   swe.modules.filter.view      }
    templates:  { filter: "modules/filter/filter.html" }

  scaleApp.start "cli"
  scaleApp.start "simpleQuery"
  scaleApp.start "resourceInfo"
  scaleApp.start "filter"

window.swe ?=
  init: init
  modules:  modules
  RDFS_NS:  "http://www.w3.org/2000/01/rdf-schema#"
  RDF_NS:   "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

($ document).ready -> swe.init()
