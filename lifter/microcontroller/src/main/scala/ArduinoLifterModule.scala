package swe.lifter.microcontroller
                                  
import scala.collection.JavaConversions._
import java.net._
import java.io._
import java.util.Date
import org.apache.http.client._
import org.apache.http.client.methods._
import org.apache.http.impl.client._
import scala.actors._
import org.apache.commons.logging._

import scala.actors._

class ArduinoLifterModule {

  private val log:Log = LogFactory.getLog( this.getClass )

  // namespace for the sensors
  private val ns = "http://github.com/flosse/semanticExperiments/ontologies/simpleOntology#"
  private val rdfsNS = "http://www.w3.org/2000/01/rdf-schema#"
  private val rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

  // values that are used by the µC
  private val lsVarName   = "brightness"
  private val hsVarName   = "humidity"

  // values that are used for the semantic model
  private val lsName      = ns + "ExampleLightSensor"
  private val lsValName   = ns + "ExampleLightSensorValues"
  private val hsName      = ns + "ExampleHumiditySensor"
  private val hsValName   = ns + "ExampleHumiditySensorValues"
  private val sysName     = ns + "ExampleAutomationSysten"

  private val rdfAddress        = "http://localhost:8000/"
  private val controllerAddress = "http://192.168.10.2"

  addBasicSensorInformations

  private val reader =  new Actor{
  
    def act{ loop {
      createTriple( parseData( readData( controllerAddress ) ) )
      Thread.sleep( 5000 )
    }}
  } 

  reader.start
  
  private def addBasicSensorInformations{

    // add sytem ino
    addResourceTriple( sysName,    rdfsNS + "subClassOf", ns + "automationSystem"    )
    addResourceTriple( sysName,    ns + "hasComponent", lsName    )
    addResourceTriple( sysName,    ns + "hasComponent", hsName    )
    addResourceTriple( sysName,    ns + "hasComponent", ns + "ExampleMotor"    )
    addResourceTriple( ns + "ExampleMotor", rdfsNS + "subClassOf", ns + "motor"    )

    // add light sensor informations
    addResourceTriple( lsName,     rdfsNS + "subClassOf", ns + "lightSensor"    )
    addResourceTriple( lsName,     ns + "unit",              ns + "lux"            )
    addResourceTriple( lsName,     ns + "values",            lsValName             )
    addResourceTriple( lsValName,  rdfNS + "type",   rdfNS + "Seq" )

    // add humidity sensor informations
    addResourceTriple( hsName,     rdfsNS + "subClassOf", ns + "sensor"         )
    addResourceTriple( hsName,     ns + "unit",              ns + "percentage"     )
    addResourceTriple( hsName,     ns + "values",            hsValName             )
    addResourceTriple( hsValName,  rdfNS + "type",   rdfNS + "Seq" )
  }

  private def readData( address:String ):String =
    try{
      new DefaultHttpClient execute( new HttpGet( controllerAddress ), new BasicResponseHandler )
    }catch{
      case e:Exception  => log.warn( e.getMessage ); "ERROR"
      case _            => log.error( "something went wrong" ); "ERROR"
    }

  private def parseData( data:String ):Map[String,String] =
    data
      .split("&")
      .map( _.split("=") )
      .filter( _.length == 2 )
      .map( a => a(0) -> a(1).toString.trim )
      .toMap

  private def createTriple( dataMap:Map[String,String] ){

    // lightSensor
    if( dataMap.contains( lsVarName ) ){
      addLiteralTriple( lsValName, rdfNS + "_" + (new Date).getTime , dataMap( lsVarName ) )
    }

    // humiditySensor
    if( dataMap.contains( hsVarName ) ){
      addLiteralTriple( hsValName, rdfNS + "_" + (new Date).getTime , dataMap( hsVarName ) )
    }

  }                               

 private def addResourceTriple( s:String, p:String, o:String ){
   execQuery( rdfAddress + "add/ResourceStatement?" + getParams( s, p, o ) )

 }

 private def addLiteralTriple( s:String, p:String, o:String ){
   execQuery( rdfAddress + "add/LiteralStatement?" + getParams( s, p, o ) )
 }

 private def execQuery( address:String ){
    log.debug( address )
    try{
      new DefaultHttpClient execute( new HttpGet( address ), new BasicResponseHandler )
    }catch{
      case e:Exception  => log.warn( e.getMessage )
      case _            => log.error( "something went wrong" )
    }
 }

 private def getParams( s:String, p:String, o:String ):String =
  "subject=" + URLEncoder.encode( s ) +
  "&predicate=" + URLEncoder.encode( p ) +
  "&object=" + URLEncoder.encode( o ) 

}
