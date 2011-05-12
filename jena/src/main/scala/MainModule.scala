package swe

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.util._
import com.hp.hpl.jena.query._

import java.io.InputStream
import org.apache.commons.logging._

object MainModule {

  private val log:Log = LogFactory.getLog( this.getClass )
  private var queryModule : QueryModule = _
  private val prefix = "PREFIX so: <http://github.com/flosse/semanticExperiments/ontologies/simpleOntology#>"
  
  def main( args:Array[String] ){

    val model = loadRDFData( "../ontologies/simpleOntology.n3", createEmptyModel )

    queryModule = new QueryModule( model )
    
    search( "sensor" ).foreach( r => log.info( r ) )

  }
  
  def search( text:String ):List[RDFNode] = {
 
    try {
      log.debug("execute query")
      queryModule.searchForResources( prefix, text )
    }catch{
      case e:Exception	=> log.error("error: "+ e ); List() 
      case _           	=> log.error("something went wrong" ); List() 
    }
  }
  
  private def createEmptyModel = ModelFactory.createDefaultModel

  private def loadRDFData( fileName:String, model:Model ):Model = {

    log.debug("adding file content...")

    FileManager.get().open( fileName ) match {

      case null		        => log.error( "File: " + fileName + " not found" ); model
      case s:InputStream	=> loadFileIntoModel( s, model )
      case _              => log.error( "something went wrong" ); model

    }
  }

  private def loadFileIntoModel( in:InputStream, model:Model ):Model = {
    try{
      model.read( in, null, "N3" )
      log.debug("content added")
      model
    }catch{
      case e:Exception  => log.error( "error: " + e ); model
      case _            => log.error( "something went wrong" ); model
    } finally {
      model
    }
  }

}
