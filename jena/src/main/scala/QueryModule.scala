package swe

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query._
import org.apache.commons.logging._

class QueryModule( model:Model ){

  private val log:Log = LogFactory.getLog( this.getClass )

  def searchForResources( prefix:String, searchText: String ):List[RDFNode] = 
        resultSetToList( 
          QueryExecutionFactory.create( 
            createQuery( searchText, prefix ), model 
          ).execSelect 
        )

  private def resultSetToList( res:ResultSet ):List[RDFNode] = {
    var list = List[RDFNode]()

    while( res.hasNext ){
      list = res.nextSolution.get( "s" ) :: list 
    }
    list
  }

  private def createQuery( searchText:String, prefix:String ) = {

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
