package swe

import org.openrdf.repository.Repository._
import org.openrdf.repository.sail._
import org.openrdf.sail.memory._
import org.openrdf.OpenRDFException
import org.openrdf.rio.RDFFormat
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.openrdf.query.QueryLanguage
import org.openrdf.query.BindingSet

import java.io.File

object helloSemanticWorld{

  def main( args:Array[String] ){

    val myRepository = new SailRepository( new MemoryStore )
    myRepository.initialize
    println("repository initialized")

    val file = new File("../ontologies/simpleOntology.n3")
    val baseURI = "http://example.org/example/local"

    try {
      val con = myRepository.getConnection()
      println("adding file content...")
      con.add( file, baseURI, RDFFormat.N3 )
      println("content added")

      println("sample quering...")
      val queryString = 
      "PREFIX so: <http://github.com/flosse/semanticExperiments/ontologies/simpleOntology#> " +
      "SELECT ?sensor WHERE { ?sensor so:hastUnit so:celsius . }"

      println("SPARQL: " + queryString )

      val tupleQuery = con.prepareTupleQuery( QueryLanguage.SPARQL, queryString )
      val result = tupleQuery.evaluate

      println("Result:")

      while( result.hasNext ){
	val bindingSet = result.next
	val value = bindingSet.getValue("sensor")
	println(value)
      }
    }
    catch{
      case i: java.io.IOException => println("could not open file")
      case r: OpenRDFException => println("could not open rdf")
      case e: Exception => println(e)
      case _ => println("unknown error :(")
    }
  }
}