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

		scaleApp.register("resourceInfo", swe.modules.resourceInfo.controller,{
			models: { model: swe.modules.resourceInfo.model },
			views: { view: swe.modules.resourceInfo.view },
			templates: { result: "modules/resourceInfo/resourceInfo.html" }
		});

		scaleApp.register("filter", swe.modules.filter.controller,{
			models: { model: swe.modules.filter.model },
			views: { view: swe.modules.filter.view },
			templates: { filter: "modules/filter/filter.html" }
		});

		scaleApp.start("cli");
		scaleApp.start("simpleQuery");
		scaleApp.start("resourceInfo");
		scaleApp.start("filter");

	};

	return ({
		init: init,
		modules: modules
	});

})( window );

$(document).ready(function(){
	swe.init();
});
