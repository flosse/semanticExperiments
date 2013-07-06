controller = (sb) ->

  model = null
  view  = null
  rdfsNS = "http://www.w3.org/2000/01/rdf-schema#"
  rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

  init = ->

    model = sb.getModel "model"
    sb.mixin model, sb.observable
    model.subscribe @

    view = new sb.getView("view") sb, model
    view.init()
    sb.subscribe "simpleQuery/select", search
    search ''

  search = (resource) ->

    if resource.trim isnt ''
      sparql = """
        CONSTRUCT { <#{ resource }> ?p ?o   }
        WHERE     { <#{ resource }> ?p ?o . }
        """

      $.ajax
        url: "sparql?" + $.param( query: sparql ),
        dataType: "text",
        success: (res) ->
          model.results = parseRDF res
          model.notify()

  parseRDF = (res) ->

    rdf = $.rdf().load(res, {})
    triples = rdf.databank.tripleStore
    sb.debug triples
    for i, triple of triples
      property: triple.property.value
      object: triple.object.value

  destroy = ->

  # public API
  init: init,
  destroy: destroy

model = rdf: undefined, results: []

view = (sb, model) ->

  result = null
  c      = null
  tmpl   = null

  update = (ev) ->

    c.empty()
    sb.tmpl(tmpl, results: model.results).appendTo c

  select = (ev) -> sb.publish "simpleQuery/select", $(@).attr('rel')

  init = ->
    model.subscribe @
    tmpl = sb.getTemplate("result")
    c = sb.getContainer()
    c.delegate "td a", "click", select

  init: init
  update: update

# public classes
swe.modules.resourceInfo ?=

  controller: controller
  model: model
  view: view
