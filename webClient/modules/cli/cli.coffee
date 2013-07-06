# Class: cli
# This is the commandline module for swe.

# Class: cli.controller
controller = (sb) ->

  model = null
  view  = null

  # Function: init
  init = ->

    model = sb.getModel()
    model.subscribe @
    view = new sb.getView() sb, model
    view.init()

  destroy = ->

  update = -> sb.publish "cli", model.cmd

  # public API
  init:    init
  destroy: destroy
  update:  update

# Class: cli.model
model = cmd: "", enterPressed: false

# Class: cli.view
view = (sb, model) ->

  cli = null

  # Function: onKeyUp
  onKeyUp = (ev) ->

    if ev.which is '27' # on escape
      model.cmd = ''
      cli.val model.cmd
    else
      model.cmd = ($ @).val()
      model.enterPressed = ev.which is '13' # on enter
  model.notify()

  #Function: init
  init = ->

    cli = sb.tmpl "cli", {}
    cli.appendTo sb.getContainer()
    cli.attr "placeholder", sb._ "placeholder"
    cli.keyup onKeyUp

  init: init

# public API
swe.modules.cli ?=
  controller: controller
  model: model
  view: view
