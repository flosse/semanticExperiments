package swe.backend

import jade.core._

class BackendAgent extends Agent {

  override def setup {
    new BackendModule
  } 
}
