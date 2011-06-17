package swe.lifter.opc

import jade.core._

class OPCLifterAgent extends Agent{

  override def setup{
    new OPCLifterModule
  }
}
