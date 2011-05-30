swe.modules.resourceInfo = swe.modules.resourceInfo || (function( window, undefined ){

  var controller = function( sb ){

    var model;
    var view;
    var rdfsNS = "http://www.w3.org/2000/01/rdf-schema#"
		var rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

    var init = function(){

      model = sb.getModel( "model" );
      sb.mixin( model, sb.observable );
      model.subscribe( this );

      view = new sb.getView( "view" )( sb, model );
      view.init();
			sb.subscribe("simpleQuery/select", search );
			search("")
    };

		var search = function( resource ){
			if( resource.trim !== "" ){
				var sparql = "CONSTRUCT " +
					"{ <" + resource + "> ?p ?o } " +
					"WHERE { <" + resource + "> ?p ?o . }"

				$.ajax({
						url: "sparql?" + $.param( { query: sparql } ), 
						dataType: "text",
						success: function( res ){
							model.results = parseRDF( res );
							model.notify();	
						}
				});
			}
		};
		
		var parseRDF = function( res ){

		 var rdf = $.rdf().load(res, {});
		 var triples = rdf.databank.tripleStore
		 sb.debug( triples )
		 var lines = []
		 $.each( triples, function( i, triple ){
				 lines.push( { property: triple.property.value , object: triple.object.value } ) 
		 });
			return lines
		};

    destroy = function(){
      delete view;
      delete model;
    };

    // public API
    return ({
      init: init,
      destroy: destroy
    });
  };

  var model = {
    rdf: undefined,
    results: []
  };

  var view = function( sb, model ){

    var result;
    var c;
    var tmpl;

    var update = function( ev ){ 

      c.empty();
      sb.tmpl( tmpl, { results: model.results } ).appendTo( c );
    };

		var select = function( ev ){
			var id = $(this).attr('rel');
			sb.publish("simpleQuery/select", id )
		};

    var init = function(){
      model.subscribe( this );
      tmpl = sb.getTemplate("result");
      c = sb.getContainer();
			c.delegate("td a", "click", select );
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
