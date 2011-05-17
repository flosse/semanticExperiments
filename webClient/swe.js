var swe = swe || (function( window, undefined ){

	var modules = { };

	var init = function(){

		scaleApp.register("cli", swe.modules.cli.controller,{
			models: { model: swe.modules.cli.model },
			views: { view: swe.modules.cli.view },
			templates: { cli: "modules/cli/cli.html" }
		});

		scaleApp.register("rdfquery", swe.modules.rdfquery.controller,{
			models: { model: swe.modules.rdfquery.model },
			views: { view: swe.modules.rdfquery.view },
			templates: { result: "modules/rdfquery/result.html" }
		});

		scaleApp.register("simpleQuery", swe.modules.simpleQuery.controller,{
			models: { model: swe.modules.simpleQuery.model },
			views: { view: swe.modules.simpleQuery.view },
			templates: { result: "modules/simpleQuery/result.html" }
		});

		scaleApp.start("cli");
		scaleApp.start("simpleQuery");

	};

	return ({
		init: init,
		modules: modules
	});

})( window );

$(document).ready(function(){
	swe.init();
});
