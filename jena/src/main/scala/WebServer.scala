package swe

import java.net._
import com.sun.net.httpserver._

import com.hp.hpl.jena.rdf.model._
import scala.collection.JavaConversions._ 
import java.io._
import scala.io.Source
import java.nio._
 
class QueryHandler( model:Model ) extends HttpHandler {

  def handle( exchange: HttpExchange ) {

      val writer = new BufferedWriter( new OutputStreamWriter( exchange.getResponseBody ) )

      exchange.getResponseHeaders.put("Content-Type: ", List[String]( "text/n3") )
      exchange.sendResponseHeaders(200, 0)
      
      model.write( writer, "N3" )

      writer.flush

      exchange.close
  }

}

class WebServer( model:Model ){

  println("create WebServer")

  val server = HttpServer.create( new InetSocketAddress(6666), 10 )

  server.createContext("/model", new QueryHandler(model) )

  server.start

}
