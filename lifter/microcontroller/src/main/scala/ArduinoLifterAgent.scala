package swe.lifter.microcontroller

import jade.core._

class ArduinoLifterAgent extends Agent {

  override def setup {
    new ArduinoLifterModule
  } 
}
