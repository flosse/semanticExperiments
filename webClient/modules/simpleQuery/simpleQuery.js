swe.modules.simpleQuery = swe.modules.simpleQuery || (function( window, undefined ){

  var controller = function( sb ){

    var model;
    var view;

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

			var param = {
				resourceName: model.searchTerm,
				classes: model.filter.classes.join(','),
				properties: model.filter.properties.join(',')
			};

      $.ajax({
					url: "query?" + $.param( param ), 
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
		filter: {},
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
