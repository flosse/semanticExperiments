swe.modules.filter = swe.modules.filter || (function( window, undefined ){

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
			search();
			sb.subscribe("cli", search );
    };

		var search = function(){
			searchForClasses()
			searchForProperties()
		}

		var searchForClasses = function(){

			var sparql = "SELECT DISTINCT ?s " +
				"WHERE { ?s <" + rdfNS + "type> <"+ rdfsNS + "Class> . } " +
				"ORDER BY ?s";

      $.ajax({
					url: "sparql?" + $.param({ query: sparql }), 
					dataType: "text",
					success: function( res ){
						model.classesAvailable = parseResult( res );
						model.notify();	
					}
			});
		}

		var searchForProperties = function(){

			var sparql = "SELECT DISTINCT ?s " +
				"WHERE { ?s <" + rdfNS + "type> <"+ rdfNS + "Property> . } " +
				"ORDER BY ?s";

      $.ajax({
					url: "sparql?" + $.param({ query: sparql }), 
					dataType: "text",
					success: function( res ){
						model.propertiesAvailable = parseResult( res ); 
						model.notify();	
					}
			});
		};

		var parseResult = function( res ){

			var results = {};

			$.each( res.split(/\n/), function( i, resource ){
					results[ resource ] = resource.split('#')[1];
			});
			return results;
		};

		var update = function(){
			sb.publish("filter", {
				classes : obj2arr( model.classesSelected ),
				properties : obj2arr( model.propertiesSelected )
			});
		};                                           

		var obj2arr = function( obj ){
			return $.map( obj, function( val ){ return val });
		};

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
		classesAvailable:{},
		classesSelected:{},
		propertiesAvailable:{},
		propertiesSelected:{}
	};

  var view = function( sb, model ){

    var c;
    var tmpl;

    var update = function( ev ){ 

      c.empty();
      sb.tmpl( tmpl, model ).appendTo( c );
    };

    var init = function(){
      model.subscribe( this );
      tmpl = sb.getTemplate("filter");
      c = sb.getContainer();
			c.delegate(".classes li", "click", toggleClass );
			c.delegate(".properties li", "click", toggleProperty );
			update();
    };

		var toggleClass = function( ev ){
			toggle( model.classesSelected, $(this).attr('rel') ); 
			model.notify();
		};

		var toggleProperty = function( ev ){
			toggle( model.propertiesSelected, $(this).attr('rel') ); 
			model.notify();
		};

		var toggle = function( obj, id ){

			if( obj[ id ] ){
				delete obj[ id ];
			}else{
				obj[ id ] = id;
			}

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
