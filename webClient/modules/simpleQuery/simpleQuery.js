swe.modules.simpleQuery = swe.modules.simpleQuery || (function( window, undefined ){

  var controller = function( sb ){

    var model;
    var view;

    var init = function(){

      model = sb.getModel( "model" );
      sb.mixin( model, sb.observable );
      model.subscribe( this );

      view = new sb.getView( "view" )( sb, model );
      view.init();
			sb.subscribe("cli/search", search );
			search("")
    };

		var search = function( searchTerm ){

      $.ajax({
					url: "query?resourceName=" + searchTerm, 
					dataType: "text",
					success: function( res ){
						model.results = res.split(/\n/);
						model.notify();	
					}
			});
		}

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
      sb.info( model.results )
      sb.tmpl( tmpl, { results: model.results } ).appendTo( c );
    };

    var init = function(){
      model.subscribe( this );
      tmpl = sb.getTemplate("result");
      c = sb.getContainer();
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
