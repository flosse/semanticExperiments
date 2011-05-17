/**
 * Class: cli
 * This is the commandline module for swe.
 */
swe.modules.cli = swe.modules.cli || (function( window, undefined ){

  /**
   * Class: cli.controller
   */
  var controller = function( sb ){

		var model;
		var view;

    /**
     * Function: init
     */
    var init = function(){

			model = sb.getModel();
			model.subscribe( this );
			view = new sb.getView()( sb, model );
			view.init();
    };

    /**
     * Function: destrory
     */
    destroy = function(){
      delete view;
      delete model;
    };
		
		var update = function(){
			sb.publish("cli/search", model.cmd );
		}

    // public API
    return ({
      init: init,
      destroy: destroy,
			update: update
    });
  };

  /**
   * Class: cli.model
   */
  var model = {
    cmd: "",
    enterPressed: false
  };

  /**
   * Class: cli.view
   */
  var view = function( sb, model ){
    
    var cli;
    
    /**
     * Function: update
     */
    var update = function( ev ){
      cli.val( model.cmd );      
    };
    
    /**
     * Function: onKeyUp
     */
    var onKeyUp = function( ev ){
	  
      if( ev.which == '27' ){		// on escape
				model.cmd = '';
				model.notify();
      }else{
				model.cmd = $(this).val();
				if( ev.which == '13' ){		// on enter
					model.enterPressed = true;
					model.notify();
				}
      }
			model.notify();
		};
    
    /**
     * Function: init
     */
    var init = function(){
	
				cli = sb.tmpl("cli", {} );
	
				model.subscribe( this );
				
				cli.appendTo( sb.getContainer() );
				cli.attr("placeholder", sb._("placeholder"));
				cli.keyup( onKeyUp );
	
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
