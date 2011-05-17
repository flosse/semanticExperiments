package swe

import java.net._
import com.sun.net.httpserver._
import com.hp.hpl.jena.rdf.model._
import scala.collection.JavaConversions._
import java.io._
import scala.io.Source
import java.nio._
import org.apache.commons.logging._

class FileHandler( rootDirString:String ) extends HttpHandler {

  private val log:Log = LogFactory.getLog( this.getClass )

  val responseCodes = Map (
    "200" -> 200, "404" -> 404
  )

  val mimeTypes = Map (
      "html"-> "text/html",
      "png" -> "image/png",
      "css" -> "text/css",
      "js"  -> "text/javascript"
    )

  val Page404 = <html><head><title>Page Not Found</title></head><body><p>Sorry but that resource does not exist</p></body></html>
  val rootDir = new File( rootDirString )

  def handle(exchange: HttpExchange) {

      val writer = new BufferedWriter(new OutputStreamWriter(exchange.getResponseBody ))

      val requestedFile = new File(rootDir, exchange.getRequestURI().toString().substring(1))
      val requestedExtension = exchange.getRequestURI().toString().split("\\.").reverse(0)

      if( requestedFile.canRead ){

          exchange.getResponseHeaders.add("Content-Type", mimeTypes( requestedExtension ) )
          exchange.sendResponseHeaders( responseCodes("200"), 0)

          if(
            requestedExtension.equals("html") ||
            requestedExtension.equals("js")   ||
            requestedExtension.equals("css")
          ) {
            Source.fromFile(requestedFile).getLines.foreach( l => writer.write( l + "\n") )
          } else {
              val input = new DataInputStream(new FileInputStream(requestedFile))
              val output = new DataOutputStream(exchange.getResponseBody())
              val buffer: Array[Byte] = new Array[Byte](1024)

              while((input.read(buffer)) != -1) {
                  output.write(buffer);
              }
          }

      } else {
          exchange.sendResponseHeaders(responseCodes("404"), 0)
          writer.write(Page404.toString());
      }

      writer.flush()

      exchange.close()
  }
}
class OntologyHandler( model:Model, lang:String ) extends HttpHandler {

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
      model.write( writer, lang )
    }catch{
      case e:Exception  => log.error("Error: Could not write model: " + e )
      case _            => log.error("Error: Could not write model" )
    }

    writer.flush

    exchange.close
  }

}

class QueryHandler( queryModule:QueryModule ) extends HttpHandler {

  private val log:Log = LogFactory.getLog( this.getClass )
  private val propVar = "resourcePropertiesOf"
  private val resVar = "resourceName"

  def handle( exchange: HttpExchange ) {

    val writer = new BufferedWriter( new OutputStreamWriter( exchange.getResponseBody ) )

    exchange.getResponseHeaders.put("Content-Type: ", List[String]( "text/plain" ) )
    exchange.sendResponseHeaders(200, 0)

    try{
      val uri = exchange.getRequestURI
			val path = uri.getRawPath
			val params = parseQuery( uri.getRawQuery )
      
      if( params.contains( propVar ) ){
        queryModule.getResourceProperties( params( propVar ) )
          .foreach( r => writer.write( r.toString + "\n" ) )
      }
      if( params.contains( resVar ) ){
        queryModule.searchForResources( params( resVar ) )
          .foreach( r => writer.write( r.toString + "\n" ) )
      }
    }catch{
      case e:Exception  => log.error("Error: Could not execute query: " + e )
      case _            => log.error("Error: Could not execute query" )
    }

    writer.flush

    exchange.close
  }

  def parseQuery( query:String ):Map[String,String] =
    query
      .split("&")
      .map( _.split("=") )
      .filter( _.length == 2 )
      .map( a => a(0) -> URLDecoder.decode( a(1), "UTF-8" ) )
      .toMap
}

class WebServer( model:Model, queryModule:QueryModule ){

  private val log:Log = LogFactory.getLog( this.getClass )

  val server = HttpServer.create( new InetSocketAddress(8000), 10 )

  server.createContext("/", new FileHandler( "../webClient" ) )
  server.createContext("/model/n3", new OntologyHandler( model, "N3" ) )
  server.createContext("/model/rdf", new OntologyHandler( model, "RDF/XML" ) )
  server.createContext("/query", new QueryHandler( queryModule ) )

  server.start
  log.info("WebServer started")

}
