package swe.backend.jena

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.util._
import com.hp.hpl.jena.query._

import scala.collection.JavaConversions._
import java.io._
import org.apache.commons.logging._

import swe.backend._

class JenaBackend extends Backend {

  private val log:Log = LogFactory.getLog( this.getClass )
  private val model = ModelFactory.createDefaultModel

  def loadFromFileIntoModel( fileName:String ) = loadFromFileIntoModel( fileName, "RDF/XML" )

  def loadFromFileIntoModel( fileName:String, lang:String ){

    log.debug("load data from " + fileName )

    FileManager.get.open( fileName ) match {

      case null		        => log.error( "File: " + fileName + " not found" )
      case s:InputStream	=> loadFromInputStreamIntoModel( s, model, lang )
      case _              => log.error( "something went wrong" )

    }
  }

  private def loadFromInputStreamIntoModel( in:InputStream, model:Model, lang:String ){

    try{
      model.read( in, null, lang )
      log.debug("Added data successfully")
    }catch{
      case e:Exception  => log.error( e.getMessage )
      case _            => log.error( "something went wrong" )
    }
  }

  def addResourceStatement( s:String, p:String, o:String ){

    addStatement(
        model.createResource( s ),
        model.createProperty( p ),
        model.createResource( o )
    )
  }

  def addLiteralStatement( s:String, p:String, o:String ){

    addStatement(
        model.createResource( s ),
        model.createProperty( p ),
        model.createLiteral( o )
    )
  }

  private def addStatement( s:Resource, p:Property, o:RDFNode ){
    model.add( ResourceFactory.createStatement( s , p,  o ) )
  }

  def executeSPARQL( query:String ):String = {

    if( query.contains("SELECT") ){
      execSelect( query )
    }else if( query.contains("CONSTRUCT") ){
      execConstruct( query )
    }else{
      log.error("invalid query"); ""
    }

  }

  private def execConstruct( query:String ):String = { 

    var writer = new StringWriter
    QueryExecutionFactory
      .create( QueryFactory.create( query ), model )
      .execConstruct
      .write( writer, "RDF/XML" )

    writer.toString
  }

  private def execSelect( query:String ):String = 
    resultSetToList(
      QueryExecutionFactory
        .create( QueryFactory.create( query ), model )
        .execSelect)

     .map( _.toString )
     .foldLeft("")( _ + "\n" + _ )
     
  private def resultSetToList( res:ResultSet ):List[RDFNode] = {

    var list = List[RDFNode]()

    while( res.hasNext ){
      var sol = res.nextSolution
      for( v <- res.getResultVars ){
        list = sol.get( v ) :: list
      }
    }
    list
  }         

  def getModel:String = getModel( "RDF/XML" ) 

  def getModel( lang:String ):String = {
    val writer = new StringWriter
    model.write( writer, lang )
    writer.toString
  }
}

