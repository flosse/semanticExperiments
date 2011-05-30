package swe.backend.service

import java.net._
import com.sun.net.httpserver._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.sparql.resultset._
import scala.collection.JavaConversions._
import java.io._
import scala.io.Source
import java.nio._
import org.apache.commons.logging._

import swe.backend._

class FileHandler( rootDirString:String ) extends HttpHandler {

  private val log:Log = LogFactory.getLog( this.getClass )

  private val responseCodes = Map ( "200" -> 200, "404" -> 404 )

  private val mimeTypes = Map (
      "html"-> "text/html",
      "png" -> "image/png",
      "css" -> "text/css",
      "js"  -> "text/javascript"
    )

  private val Page404 = 
    <html>
      <head>
        <title>Page Not Found</title>
      </head>
      <body>
        <p>Sorry but that resource does not exist</p>
      </body>
    </html>

  private val rootDir = new File( rootDirString )

  private def isTextFile( ext:String ) =
            ( ext.equals("html") || ext.equals("js") || ext.equals("css") )

  private def writeTextData( file:File, writer:Writer ) =
    Source.fromFile( file ).getLines.foreach( l => writer.write( l + "\n") )

  private def writeBinaryData( file:File, out:OutputStream ){

      val in = new DataInputStream( new FileInputStream( file ) )
      val buffer = new Array[Byte]( 1024 )

      while( ( in.read( buffer )) != -1 ){
          out.write( buffer )
      }
  }

  private def getFileExtension( name:String ) = name.split("\\.").reverse(0)

  private def getWriterFromHttpExchange( ex:HttpExchange ) = 
    new BufferedWriter( new OutputStreamWriter( ex.getResponseBody ) )

  private def getPathFromHttpExchange( ex:HttpExchange ) =
    ex.getRequestURI.getRawPath match{

        case "/"  => "/index.html" 
        case _    => ex.getRequestURI.getRawPath
      }                          

  private def sendNotFoundMessage( ex:HttpExchange, writer:Writer ){
    ex.sendResponseHeaders( responseCodes("404"), 0 )
    writer.write( Page404.toString );
  }

  private def getFile( ex:HttpExchange )=
    new File( rootDir, getPathFromHttpExchange( ex ).substring(1) )

  private def sendFileContent( ex:HttpExchange, file:File, writer:Writer ){

    val extension = getFileExtension( file.getName )

    ex.getResponseHeaders.add("Content-Type", mimeTypes( extension ) )
    ex.sendResponseHeaders( responseCodes("200"), 0)

    if( isTextFile( extension ) ){
      writeTextData( file, writer )
    } else {
      writeBinaryData( file, new DataOutputStream( ex.getResponseBody ) )
    }
  }

  def handle( ex: HttpExchange ){

      val writer = getWriterFromHttpExchange( ex )
      val file = getFile( ex )

      if( file.canRead ){
        sendFileContent( ex, file, writer )
      } else {
        sendNotFoundMessage( ex, writer )
      }

    writer.flush
    ex.close
  }
}
