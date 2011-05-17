package swe

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query._
import org.apache.commons.logging._

class QueryModule( model:Model, prefix:String ){

  private val log:Log = LogFactory.getLog( this.getClass )

  def searchForResources( searchText: String ):List[RDFNode] = 
        resultSetToList( 
          QueryExecutionFactory.create( 
            createResourceLookUpQuery( searchText ), model 
          ).execSelect 
        )

  def getResourceProperties( resourceName:String ):List[RDFNode] = 
        resultSetToList( 
          QueryExecutionFactory.create( 
            createResourcePropertiesQuery( resourceName ), model 
          ).execSelect 
        )
  private def resultSetToList( res:ResultSet ):List[RDFNode] = {
    var list = List[RDFNode]()

    while( res.hasNext ){
      list = res.nextSolution.get( "x" ) :: list 
    }
    list
  }

  private def createResourceLookUpQuery( searchText:String ) = {

    log.debug("create query")

    val select = " SELECT DISTINCT ?x "
    val regex = "regex( str(?s) , \"(?i)" + searchText + "\" )"
    val where = "WHERE { ?x ?p ?o . FILTER ( " + regex + " ) } "
    val order = "ORDER BY ?x"
    val queryString = prefix + select + where + order

    log.debug("Search for \"" + searchText + "\"" )

    QueryFactory.create( queryString )
  }

  private def createResourcePropertiesQuery( resourceName:String ) = {

    log.debug("create query")

    val select = " SELECT DISTINCT ?x "
    val where = "WHERE { " + resourceName + " ?x ?o } "
    val order = "ORDER BY ?x"
    val queryString = prefix + select + where + order

    log.debug("Search for properties of " + resourceName )

    QueryFactory.create( queryString )
  }
}
