###
Copyright (c) 2011 - 2013 Markus Kohlhase <mail@markus-kohlhase.de>
###

{ RDFS_NS, RDF_NS } = window.swe

controller = (sb) ->

    model = null
    view  = null

    init = ->

      model = sb.getModel( "model" )
      model.subscribe( this )

      view = new sb.getView("view") sb, model
      view.init()
      sb.subscribe "cli", onCli
      sb.subscribe "filter", onFilter
      sb.subscribe "simpleQuery/select", onSelect

    onCli = (term) ->
      model.searchTerm = term
      search()

    onFilter = (filter) ->
      model.filter = filter
      search()

    onSelect = (id) ->
      model.selected = id
      model.notify()

    search = ->

      classes = (for c,i of model.filter.classes
        "?s <#{ RDFS_NS }subClassOf> <#{ c }> ."
      ).join " "

      properties = (for p,i of model.filter.properties
        "?x <#{ p }> ?s ."
      ).join " "

      sparql = """
        SELECT DISTINCT ?s
        WHERE { ?s ?p ?o . #{ classes } #{ properties }
        FILTER ( regex( str(?s) , '(?i)#{ model.searchTerm }' ) ) }
        ORDER BY ?s
        """

      sb.debug sparql

      $.ajax
          url: "sparql?" + $.param query: sparql
          dataType: "text",
          success: (res) ->
            model.results = parseResult res
            model.notify()

    parseResult = (res) ->

      $.map res.split(/\n/), (resource) ->
        string:   resource
        fragment: resource.split('#')[1]

    update = ->

    destroy = ->

    # public API
    init: init,
    destroy: destroy,
    update: update

  model =
    searchTerm: ""
    results: []
    filter:
      classes:    []
      properties: []
    selected: ""

  view = (sb,model) ->

    result = null
    c      = null
    tmpl   = null

    update = (ev) ->
      c.empty()
      sb.tmpl(tmpl, results: model.results, selected: model.selected).appendTo c

    select = (ev) -> sb.publish "simpleQuery/select", ($ @).attr('rel')

    init = ->
      model.subscribe @
      tmpl = sb.getTemplate "result"
      c = sb.getContainer()
      c.delegate "li", "click", select

    init: init,
    update: update

# public classes
swe.modules.simpleQuery ?=
  controller: controller
  model: model
  view: view
