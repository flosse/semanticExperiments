package swe.backend.service

import java.net._
import com.sun.net.httpserver._
import scala.collection.JavaConversions._
import java.io._
import scala.io.Source
import java.nio._
import org.apache.commons.logging._

import swe.backend._

class OntologyHandler( backend:Backend, lang:String ) extends HttpHandler {

  private val log:Log = LogFactory.getLog( this.getClass )

  def handle( exchange: HttpExchange ) {

    val writer = new BufferedWriter( new OutputStreamWriter( exchange.getResponseBody ) )

    var mime:String = lang match {
      case "N3"         =>  "text/n3"
      case "RDF/XML"    =>  "application/rdf+xml"
      case _            =>  "text"
    }

    exchange.getResponseHeaders.put("Content-Type: ", List[String]( mime ) )
    exchange.sendResponseHeaders(200, 0)

    try{
      writer.write( backend.getModel( lang ) )
    }catch{
      case e:Exception  => log.error("Error: Could not write model: " + e.getMessage )
      case _            => log.error("Error: Could not write model" )
    }

    writer.flush
    exchange.close
  }

}
