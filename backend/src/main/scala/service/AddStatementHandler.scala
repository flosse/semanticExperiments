package swe.backend.service

import java.net._
import com.sun.net.httpserver._
import scala.collection.JavaConversions._
import java.io._
import scala.io.Source
import java.nio._
import org.apache.commons.logging._

import swe.backend._

class AddStatementHandler( backend:Backend, statementType:String ) extends HttpHandler {

  private val log:Log = LogFactory.getLog( this.getClass )

  def handle( exchange: HttpExchange ) {

    val writer = new BufferedWriter( new OutputStreamWriter( exchange.getResponseBody ) )

    exchange.getResponseHeaders.put("Content-Type: ", List[String]( "text/plain" ) )
    exchange.sendResponseHeaders(200, 0)

    try{

      val uri = exchange.getRequestURI
      val path = uri.getRawPath
      val params = parseQuery( uri.getRawQuery )
      
      if( !isValidStatement( params ) ){
        throw new Exception("something went wrong")
      }

      val s = params("subject") 
      val p = params("predicate")
      val o = params("object")

      statementType match {
        case "literal"  =>  backend.addLiteralStatement( s, p, o )
        case "resource" =>  backend.addResourceStatement( s, p, o ) 
        case _  => throw new Exception("something went wrong")
      }
      writer.write("OK")

    }catch{
      case e:Exception  => log.error("Error: Could not add statement: " + e ); writer.write("ERROR")
      case _            => log.error("Error: Could not add statement" ); writer.write("ERROR")
    }

    writer.flush

    exchange.close
  }

  private def isValidStatement( params:Map[String,String] ):Boolean = {
    
    if( containsAllParameters( params ) ){ 

      val s = params("subject").trim
      val p = params("predicate").trim
      val o = params("object").trim

      if ( s != "" && p != "" && o != "" ){
        if( isValidURI( s ) && isValidURI( p ) ){
          return statementType match {
            case "literal"  => true
            case "resource" => isValidURI( o )
            case _  => return false
          }
        }
      }     
    }

    false
  }

  private def containsAllParameters( p:Map[String,String] ):Boolean = 
    p.contains("subject") && p.contains("predicate") && p.contains("object") 

  private def isValidURI( str:String ):Boolean = 

    try{
      new URI(str)
      true
    }catch{
      case e:Exception	=> false
      case _            => false
    }

  private def parseQuery( query:String ):Map[String,String] =
    query
      .split("&")
      .map( _.split("=") )
      .filter( _.length == 2 )
      .map( _.map( s => URLDecoder.decode(s) ) )
      .map( a => Array( a(0), if( a.length > 1 ) a(1) else "" ) )
      .map( a => a(0) -> a(1) )
      .toMap
}
