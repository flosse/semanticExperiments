package swe

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query._
import org.apache.commons.logging._
import scala.collection.JavaConversions._

class QueryModule( model:Model, prefix:String ){

  private val log:Log = LogFactory.getLog( this.getClass )

  def searchForResources( searchText: String ):ResultSet = 
    QueryExecutionFactory.create( 
      createResourceLookUpQuery( searchText ), model 
    ).execSelect 

  def getResourceGraph( resourceName:String ):Model = 
    QueryExecutionFactory.create( 
      createResourceGraphQuery( resourceName ), model 
    ).execConstruct 

  private def createResourceLookUpQuery( searchText:String ) = {

    log.debug("create query")

    val select = " SELECT DISTINCT ?s "
    val regex = "regex( str(?s) , \"(?i)" + searchText + "\" )"
    val where = "WHERE { ?s ?p ?o . FILTER ( " + regex + " ) } "
    val order = "ORDER BY ?s"
    val queryString = prefix + select + where + order

    log.debug("Search for \"" + searchText + "\"" )

    QueryFactory.create( queryString )
  }

  private def createResourceGraphQuery( resourceName:String ) = {

    log.debug("create graph query")

    val construct = " CONSTRUCT { " + resourceName + " ?p ?o } "
    val where = "WHERE { " + resourceName + " ?p ?o . } "
    val queryString = prefix + construct + where

    log.debug("Search for properties of " + resourceName )

    QueryFactory.create( queryString )
  }
}
