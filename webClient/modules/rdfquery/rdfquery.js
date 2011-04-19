swe.modules.rdfquery = swe.modules.rdfquery || (function( window, undefined ){

  var controller = function( sb ){

    var model;
    var view;

    var init = function(){

      model = sb.getModel( "model" );
      sb.mixin( model, sb.observable );
      model.subscribe( this );

      view = new sb.getView( "view" )( sb, model );
      view.init();

      $.get("ontologies/simpleOntology.rdf", function( res ){
	model.rdf = $.rdf().load(res, {});

	model.results = [];
	model.rdf.prefix( "so", "http://github.com/flosse/semanticExperiments/ontologies/simpleOntology#")
	.where("?sensor so:hastUnit so:celsius")
	.each(function(){ 
	  model.results.push( this.sensor.value.toString() );
	});
	model.notify();
      });

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