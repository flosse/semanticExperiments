package swe
                                  
import scala.collection.JavaConversions._
import com.hp.hpl.jena.rdf.model._
import java.net._
import java.io._
import java.util.Date
import org.apache.http.client._
import org.apache.http.client.methods._
import org.apache.http.impl.client._
import scala.actors._

class SemanticLifter( service:Service, address:String ) extends Actor {

  // namespace for the sensors
  private val ns = "http://github.com/flosse/semanticExperiments/ontologies/simpleOntology#"

  // values that are used by the µC
  private val lsVarName   = "brightness"
  private val hsVarName   = "humidity"

  // values that are used for the semantic model
  private val lsName      = ns + "ExampleLightSensor"
  private val lsValName   = ns + "ExampleLightSensorValues"
  private val hsName      = ns + "ExampleHumiditySensor"
  private val hsValName   = ns + "ExampleHumiditySensorValues"

  def act { 

    addBasicSensorInformations

    loop {
      createTriple( parseData( readData( address ) ) )
      Thread.sleep( 5000 )
    } 
  }
  
  private def addBasicSensorInformations{

    // add light sensor informations
    service.addResourceTriple( lsName,     service.rdfsNS + "Class", ns + "lightSensor"    )
    service.addResourceTriple( lsName,     ns + "unit",              ns + "lux"            )
    service.addResourceTriple( lsName,     ns + "values",            lsValName             )
    service.addResourceTriple( lsValName,  service.rdfNS + "type",   service.rdfNS + "Seq" )

    // add humidity sensor informations
    service.addResourceTriple( hsName,     service.rdfsNS + "Class", ns + "sensor"         )
    service.addResourceTriple( hsName,     ns + "unit",              ns + "percentage"     )
    service.addResourceTriple( hsName,     ns + "values",            hsValName             )
    service.addResourceTriple( hsValName,  service.rdfNS + "type",   service.rdfNS + "Seq" )
  }

  private def readData( address:String ):String =
    (new DefaultHttpClient)
      .execute( new HttpGet( address ), new BasicResponseHandler )

  private def parseData( data:String ):Map[String,String] =
    data
      .split("&")
      .map( _.split("=") )
      .filter( _.length == 2 )
      .map( a => a(0) -> a(1) )
      .toMap

  private def createTriple( dataMap:Map[String,String] ){

    // lightSensor
    service.model.add( ResourceFactory.createStatement(
        service.model.createResource( lsValName ),
        service.model.createProperty( service.rdfNS + "_" + (new Date).getTime ),
        service.model.createLiteral( dataMap( lsVarName ) )
    ))

    // humiditySensor
    service.model.add( ResourceFactory.createStatement(
        service.model.createResource( hsValName ),
        service.model.createProperty( service.rdfNS + "_" + (new Date).getTime ),
        service.model.createLiteral( dataMap( hsVarName ) )
    ))

  }


}


