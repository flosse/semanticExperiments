package swe

import java.net._
import com.sun.net.httpserver._

import com.hp.hpl.jena.rdf.model._
import scala.collection.JavaConversions._ 
import java.io._
import scala.io.Source
import java.nio._
 
class OntologyHandler( model:Model, lang:String ) extends HttpHandler {

  def handle( exchange: HttpExchange ) {

      val writer = new BufferedWriter( new OutputStreamWriter( exchange.getResponseBody ) )

      var mime:String = lang match {
        case "N3"         =>  "text/n3"
        case "RDF/XML"    =>  "application/rdf+xml"
        case _            =>  "text"
      }
      
      exchange.getResponseHeaders.put("Content-Type: ", List[String]( mime ) )
      exchange.sendResponseHeaders(200, 0)
      
      model.write( writer, lang )

      writer.flush

      exchange.close
  }

}

class WebServer( model:Model ){

  println("create WebServer")

  val server = HttpServer.create( new InetSocketAddress(6666), 10 )
  
  server.createContext("/model/n3", new OntologyHandler( model, "N3" ) )
  server.createContext("/model/rdf", new OntologyHandler( model, "RDF/XML" ) )

  server.start

}
