var swe = swe || (function( window, undefined ){
  
  var modules = { };

  var init = function(){

    scaleApp.register("rdfquery", swe.modules.rdfquery.controller,{
      models: { model: swe.modules.rdfquery.model },
      views: { view: swe.modules.rdfquery.view },
      templates: { result: "modules/rdfquery/result.html" }
    });

    scaleApp.startAll();

  };

  return ({ 
    init: init, 
    modules: modules 
  });

})( window );

$(document).ready(function(){
  swe.init();
});