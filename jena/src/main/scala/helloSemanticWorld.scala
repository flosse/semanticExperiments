package swe

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.util._
import com.hp.hpl.jena.query._

import java.io.InputStream

import org.apache.commons.logging._

object helloSemanticWorld{

  private val log:Log = LogFactory.getLog( this.getClass )

  def main( args:Array[String] ){

    val prefix = "PREFIX so: <http://github.com/flosse/semanticExperiments/ontologies/simpleOntology#>"
    val searchText = "sensor"

    val model = loadRDFData( "../ontologies/simpleOntology.n3", createEmptyModel )
    query( searchText, prefix, model )

  }

  def createEmptyModel = ModelFactory.createDefaultModel

  def loadRDFData( fileName:String, model:Model ):Model = {

    log.debug("adding file content...")

    FileManager.get().open( fileName ) match {

      case null		        => log.error( "File: " + fileName + " not found" ); model
      case s:InputStream	=> loadFileIntoModel( s, model )
      case _              => log.error( "something went wrong" ); model

    }
  }

  def loadFileIntoModel( in:InputStream, model:Model ):Model = {
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

  def query( searchText:String, prefix:String, model:Model ) {

    val qexec = QueryExecutionFactory.create( createQuery( searchText, prefix ), model )

    try {
      log.info("Result:")
      printResults( qexec.execSelect, "s" )
    }catch{
      case e:Exception	=> println("error: "+ e )
      case _           	=> println("something went wrong" )
    } finally { qexec.close }
  }

  def printResults( results:ResultSet, varName:String ){

    while( results.hasNext ){
      log.info( results.nextSolution.get( varName ) )
    }
  }

  def createQuery( searchText:String, prefix:String ) = {

    log.debug("create query")

    val select = " SELECT DISTINCT ?s "
    val regex = "regex( str(?s) , \"(?i)" + searchText + "\" )"
    val where = "WHERE { ?s ?p ?o . FILTER ( " + regex + " ) } "
    val order = "ORDER BY ?s"
    val queryString = prefix + select + where + order

    log.debug("Search for \"" + searchText + "\"" )

    QueryFactory.create( queryString )
  }

}
