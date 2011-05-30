package swe.backend

import org.apache.commons.logging._

import swe.backend._
import swe.backend.jena._
import swe.backend.service._

object Main {

  private val log:Log = LogFactory.getLog( this.getClass )
  
  def main( args:Array[String] ){

    val backend = new JenaBackend
    backend.loadFromFileIntoModel( "../ontologies/simpleOntology.n3", "N3" )

    log.debug("start webserver")
    new WebServer( backend, 8000 ).start
    log.debug("webserver started on port 8000")

  }
  
}
