package swe

import java.net._
import com.sun.net.httpserver._
import com.hp.hpl.jena.rdf.model._
import scala.collection.JavaConversions._ 
import java.io._
import scala.io.Source
import java.nio._
import org.apache.commons.logging._
 
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
    
    try{
      model.write( writer, lang )
    }catch{   
      case e:Exception  => writer.write("Error: Could not write model: " + e )
      case _            => writer.write("Error: Could not write model" )
    }

    writer.flush

    exchange.close
  }

}

class QueryHandler( queryModule:QueryModule ) extends HttpHandler {

  def handle( exchange: HttpExchange ) {

    val writer = new BufferedWriter( new OutputStreamWriter( exchange.getResponseBody ) )

    exchange.getResponseHeaders.put("Content-Type: ", List[String]( "text/plain" ) )
    exchange.sendResponseHeaders(200, 0)
    
    try{      
      val uri = exchange.getRequestURI
			val path = uri.getRawPath
			val params = parseQuery( uri.getRawQuery )
      queryModule.searchForResources( params("resourceName") )
        .foreach( r => writer.write( r.toString + "\n" ) )
    }catch{   
      case e:Exception  => writer.write("Error: Could not execute query: " + e )
      case _            => writer.write("Error: Could not execute query" )
    }

    writer.flush

    exchange.close
  }

  def parseQuery( query:String ):Map[String,String] =
    query
      .split("&")
      .map( _.split("=") )
      .filter( _.length == 2 )
      .map( a => a(0) -> a(1) )
      //.map( a => URLDecoder.decode( a, "UTF-8" ) )
      .toMap
}

class WebServer( model:Model, queryModule:QueryModule ){

  private val log:Log = LogFactory.getLog( this.getClass )

  val server = HttpServer.create( new InetSocketAddress(6666), 10 )
  
  server.createContext("/model/n3", new OntologyHandler( model, "N3" ) )
  server.createContext("/model/rdf", new OntologyHandler( model, "RDF/XML" ) )
  server.createContext("/query", new QueryHandler( queryModule ) )

  server.start
  log.info("WebServer started")

}
