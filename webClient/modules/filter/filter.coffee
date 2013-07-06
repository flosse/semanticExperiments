{ RDFS_NS, RDF_NS } = window.swe

controller = (sb) ->

  model  = null
  view   = null

  init = ->

    model = sb.getModel "model"
    model.subscribe @
    view = new sb.getView("view") sb, model
    view.init()
    search()
    sb.subscribe "cli", search

  search = ->
    searchForClasses()
    searchForProperties()

  searchForClasses = ->

    sparql = """
      SELECT DISTINCT ?s
      WHERE { ?s <#{ RDF_NS }type> <#{ RDFS_NS }"Class> . }
      ORDER BY ?s
      """

    $.ajax
      url: "sparql?" + $.param({ query: sparql }),
      dataType: "text",
      success: (res) ->
        model.classesAvailable = parseResult res
        model.notify()

  searchForProperties = ->

    sparql = """
      SELECT DISTINCT ?s
      WHERE { ?s <#{ RDF_NS }type> <#{ RDF_NS }Property> . }
      ORDER BY ?s
      """

    $.ajax
      url: "sparql?" + $.param query: sparql
      dataType: "text"
      success: (res) ->
        model.propertiesAvailable = parseResult res
        model.notify()

  parseResult = (res) ->

    results = {}

    $.each res.split(/\n/), (i, resource) ->
      results[ resource ] = resource.split('#')[1]
    results

  update = ->
    sb.publish "filter",
      classes :    obj2arr model.classesSelected
      properties : obj2arr model.propertiesSelected

  obj2arr = (obj) -> (v for k,v of obj)

  destroy = ->

  # public API
  init: init
  destroy: destroy
  update: update

model =
  classesAvailable:   {}
  classesSelected:    {}
  propertiesAvailable:{}
  propertiesSelected: {}

view = (sb, model) ->

  c    = null
  tmpl = null

  update = (ev) ->
    c.empty()
    sb.tmpl(tmpl, model).appendTo c

  init = ->
    model.subscribe @
    tmpl = sb.getTemplate("filter")
    c = sb.getContainer()
    c.delegate ".classes li", "click", toggleClass
    c.delegate ".properties li", "click", toggleProperty
    update()

  toggleClass = (ev) ->
    toggle model.classesSelected, ($ @).attr('rel')
    model.notify()

  toggleProperty = (ev) ->
    toggle model.propertiesSelected, ($ @).attr('rel')
    model.notify()

  toggle = (obj, id) ->

    if obj[id] then delete obj[id]
    else obj[id] = id

  init: init,
  update: update

# public API
swe.modules.filter ?=
  controller: controller
  model: model
  view: view
