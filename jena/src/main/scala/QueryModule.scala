package swe

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query._
import org.apache.commons.logging._
import scala.collection.JavaConversions._

class QueryModule( model:Model ){

  private val log:Log = LogFactory.getLog( this.getClass )
  private val rdfsNS = "http://www.w3.org/2000/01/rdf-schema#"
  private val rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

  def searchForResources( searchText: String, classes:List[String], properties:List[String] ):ResultSet =
    execSelect( resourceLookUpQuery( searchText, classes, properties ) )

  def searchForClasses:ResultSet =
    execSelect( classLookUpQuery )

  def searchForProperties:ResultSet =
    execSelect( propertyLookUpQuery )

  def getResourceGraphOf( resourceName:String ):Model =
    execConstruct( resourceGraphQuery( resourceName ) )

  def execConstruct( query:String ):Model = QueryExecutionFactory
    .create( QueryFactory.create( query ), model ).execConstruct

  def execSelect( query:String ):ResultSet = QueryExecutionFactory
    .create( QueryFactory.create( query ), model ).execSelect

  private def resourceLookUpQuery( searchText:String, classes:List[String], properties:List[String] ) = {

    val classFilter = classes
      .filter( _!="")
      .map( c => "?s <" + rdfsNS + "subClassOf> <" + c + "> .")

    var filterString = ""

    if( classFilter.length > 0 ){
      filterString = " " + classFilter.reduceLeft( _ + "" + _) + " "
    }

    """ SELECT DISTINCT ?s
    WHERE { ?s ?p ?o .""" + filterString +
    """FILTER ( regex( str(?s) , "(?i)""" + searchText + """" )) }
    ORDER BY ?s"""
  }

  private val classLookUpQuery =
    """ SELECT DISTINCT ?s
    WHERE { ?s <""" + rdfNS + "type> <" + rdfsNS + """Class> . }
    ORDER BY ?s"""

  private val propertyLookUpQuery =
    """ SELECT DISTINCT ?s
    WHERE { ?s <""" + rdfNS + "type> <" + rdfNS + """Property> . }
    ORDER BY ?s"""

  private def resourceGraphQuery( resourceName:String ) =
    " CONSTRUCT { " + resourceName + """ ?p ?o }
    WHERE { """ + resourceName + " ?p ?o . }"
}
