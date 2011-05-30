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

class WebServer( backend:Backend, port:Int ){

  private val log:Log = LogFactory.getLog( this.getClass )
  private val server = HttpServer.create( new InetSocketAddress( port ), 10 )

  server.createContext("/", new FileHandler( "../webClient" ) )
  server.createContext("/model/n3", new OntologyHandler( backend, "N3" ) )
  server.createContext("/model/rdf", new OntologyHandler( backend, "RDF/XML" ) ) 
  server.createContext("/sparql", new SparqlHandler( backend ) )
  server.createContext("/add/ResourceStatement", new AddStatementHandler( backend, "resource" ) )
  server.createContext("/add/LiteralStatement", new AddStatementHandler( backend, "literal" ) )

  def start = server.start

}
