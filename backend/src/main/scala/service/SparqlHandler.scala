package swe.backend.service

import java.net._
import com.sun.net.httpserver._
import scala.collection.JavaConversions._
import java.io._
import scala.io.Source
import java.nio._
import org.apache.commons.logging._

import swe.backend._

class SparqlHandler( backend:Backend ) extends HttpHandler {

  private val log:Log = LogFactory.getLog( this.getClass )

  def handle( ex: HttpExchange ) {

    val writer = new BufferedWriter( new OutputStreamWriter( ex.getResponseBody ) )

    ex.getResponseHeaders.put( "Content-Type: ", List[String]( "text/plain" ) )
    ex.sendResponseHeaders( 200, 0 )

    try{

      val query = parseQuery( ex.getRequestURI.getRawQuery )("query")

      log.debug("execute " + query );

      writer.write( backend.executeSPARQL( query ) )

    }catch{
      case e:Exception  => log.error("Error: Could not execute query: " + e )
      case _            => log.error("Error: Could not execute query" )
    }

    writer.flush

    ex.close
  }

  private def parseQuery( query:String ):Map[String,String] =
    query
      .split("&")
      .map( _.split("=") )
      //.filter( _.length == 2 )
      .map( _.map( s => URLDecoder.decode(s) ) )
      .map( a => Array( a(0), if( a.length > 1 ) a(1) else "" ) )
      .map( a => a(0) -> a(1) )
      .toMap

}
