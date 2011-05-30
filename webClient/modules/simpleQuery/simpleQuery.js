swe.modules.simpleQuery = swe.modules.simpleQuery || (function( window, undefined ){

  var controller = function( sb ){

    var model;
    var view;
    var rdfsNS = "http://www.w3.org/2000/01/rdf-schema#"
		var rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

    var init = function(){

      model = sb.getModel( "model" );
      model.subscribe( this );

      view = new sb.getView( "view" )( sb, model );
      view.init();
			sb.subscribe("cli", onCli  );
			sb.subscribe("filter", onFilter );
			sb.subscribe("simpleQuery/select", onSelect )
    };

		var onCli = function( term ){
			model.searchTerm = term;
			search();
		};

		var onFilter = function( filter ){
			model.filter = filter;
			search();
		};

		var onSelect = function( id ){
			model.selected = id 
			model.notify();
		}

		var search = function(){

			var classes = $.map( model.filter.classes, function( c, i ){
					return "?s <"+ rdfsNS + "subClassOf> <" + c + "> ." 
				}).join( " " )

			var sparql = "SELECT DISTINCT ?s " +
				"WHERE { ?s ?p ?o . " + classes + " " +
				"FILTER ( regex( str(?s) , '(?i)" + model.searchTerm + "' ) ) } " +
				"ORDER BY ?s";

      $.ajax({
					url: "sparql?" + $.param({ query: sparql }), 
					dataType: "text",
					success: function( res ){
						model.results = parseResult( res )
						model.notify();	
					}
			});
		}

		var parseResult = function( res ){

			return $.map( res.split(/\n/), function( resource ){
				return { string: resource, fragment: resource.split('#')[1] };
			});
		};
	
		var update = function(){};

    var destroy = function(){
      delete view;
      delete model;
    };

    // public API
    return ({
      init: init,
      destroy: destroy,
      update: update
    });
  };

  var model = {
		searchTerm: "",
    results: [],
		filter: {
			classes:[],
			properties:[]
		},
		selected: ""
  };

  var view = function( sb, model ){

    var result;
    var c;
    var tmpl;

    var update = function( ev ){ 

      c.empty();
			sb.tmpl( tmpl, { results: model.results, selected: model.selected } ).appendTo( c );
    };

		var select = function( ev ){
			sb.publish("simpleQuery/select",$(this).attr('rel') )
		}

    var init = function(){
      model.subscribe( this );
      tmpl = sb.getTemplate("result");
      c = sb.getContainer();
			c.delegate("li", "click", select );
    };

    return ({ 
      init: init, 
      update: update
    });

  };

  // public classes
  return ({
    controller: controller,
    model: model,
    view: view
  });

})( window );
