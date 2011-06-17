package swe.lifter.opc

import java.io._
import java.net._
import java.text._
import java.util._

import org.apache.commons.logging._
import org.apache.log4j._

import org.apache.http.client._
import org.apache.http.client.methods._
import org.apache.http.impl.client._

import org.opcfoundation.ua.builtintypes._
import org.opcfoundation.ua.common._
import org.opcfoundation.ua.core._
import org.opcfoundation.ua.transport.security._
import org.opcfoundation.ua.utils._

import com.prosysopc.ua._
import com.prosysopc.ua.PkiFileBasedCertificateValidator._
import com.prosysopc.ua.UaApplication._
import com.prosysopc.ua.client._
import com.prosysopc.ua.nodes._  

import scala.collection.JavaConversions._
import scala.actors._

class OPCLifterModule{

  private val log:Log = LogFactory.getLog( this.getClass )

  private val serverUri = "opc.tcp://localhost:52520/OPCServer"
  private val clientName = "OPCLifter"
  private val url = "github.com/flosse/semanticExperiments"
  private val addressSpaceName = "SampleAddressSpace"
	private val client = new UaClient( serverUri )
	private val validator = new PkiFileBasedCertificateValidator
  private val appDescription = createAppDescription
	private val identity = createAppIdentity( appDescription )

  private val ns = "http://github.com/flosse/semanticExperiments/ontologies/simpleOntology#"
  private val rdfsNS = "http://www.w3.org/2000/01/rdf-schema#"
  private val rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

  private val sysName     = ns + "ExampleOPCAutomationSystem"
  private val switchName  = ns + "ExampleSwitch"
  private val switchValName  = ns + "ExampleSwitchValues"
  private val sliderName   = ns + "ExampleSlider"
  private val sliderValName   = ns + "ExampleSliderValues"

  private val rdfAddress        = "http://localhost:8000/"

	log.debug("Setup OPC client ..." )
  setupClient

	log.debug("Connecting to " + serverUri + " ..." )
  connectToOPCServer
	log.debug("... connected" )

  log.debug( "queryAddressSpace" )
  queryAddressSpace

  private def createAppDescription = {

    val appDescription = new ApplicationDescription
    appDescription.setApplicationName( new LocalizedText( clientName, Locale.ENGLISH ) )
    appDescription.setApplicationUri( "urn:localhost:UA:" + clientName )
    appDescription.setProductUri( "urn:" + url + ":UA:" + clientName )
    appDescription.setApplicationType( ApplicationType.Client )

    appDescription
  }
  
  private def createAppIdentity( appDescription:ApplicationDescription ) = 
	  ApplicationIdentity
      .loadOrCreateCertificate(
        appDescription,
        "Semantic Experiments",
        null,
        new File( validator.getBaseDir, "private" ),
        true 
      )

  def setupClient {

    client.setCertificateValidator( validator )
    client.setApplicationIdentity( identity )
    client.setLocale( Locale.ENGLISH )
    client.setSecurityMode( SecurityMode.NONE )
    client.setUserIdentity( new UserIdentity )
  }

  def queryAddressSpace {

    client.getAddressSpace.setMaxReferencesPerNode( 500 ) 
    client.getAddressSpace.setReferenceTypeId( Identifiers.HierarchicalReferences )

    var nt = client.getNamespaceTable

    printNameSpaces( nt )

    var ns = nt.getIndex("http://" + url + "/" + addressSpaceName )

    val mySwitchNodeId = new NodeId( ns, "MySwitch")
    val mySliderNodeId = new NodeId( ns, "MyNumber")

    var mySliderData:Int = 0
    var mySwitchData:Boolean = false 
    
    addBasicInformations

    val reader =  new Actor{
  
      def act{ loop {

      // switch
      mySwitchData = client.readValue( mySwitchNodeId ).getValue.booleanValue
      log.debug("Status of the switch: " + mySwitchData )
      addLiteralTriple( switchValName, rdfNS + "_" + (new Date).getTime , mySwitchData.toString )

      // slider
      mySliderData = client.readValue( mySliderNodeId ).getValue.intValue
      log.debug("Status of the slider: " + mySliderData )
      addLiteralTriple( sliderValName, rdfNS + "_" + (new Date).getTime , mySliderData.toString )

        Thread.sleep( 5000 )

      }}
    }          
    
    reader.start
  }

  private def printNameSpaces( nt: NamespaceTable ) {
    log.debug( "Namespaces:" )
    for( i <- 0 to 5 )
      log.debug( "Index= " + i + " Uri= " + nt.getUri( i ) )
  }

  private def connectToOPCServer{

		if( !client.isConnected ){
			try {
				client.connect
			} catch{
        case e:InvalidServerEndpointException => println( e.getMessage )
        case e:ServerConnectionException      => println( e.getMessage )
        case e:SessionActivationException     => println( e.getMessage )
        case e:ServiceException               => println( e.getMessage )
        case _                                =>
			}
    }
	}

  private def addBasicInformations{

    // add sytem info
    addResourceTriple( sysName,       rdfsNS + "subClassOf",  ns + "automationSystem"    )
    addResourceTriple( sysName,       ns + "hasComponent",    switchName     )
    addResourceTriple( sysName,       ns + "hasComponent",    sliderName     )

    // add slider sensor informations
    addResourceTriple( sliderName,     rdfsNS + "subClassOf", ns + "sensor"  )
    addResourceTriple( sliderName,     ns + "values",         sliderValName  )
    addResourceTriple( sliderValName,  rdfNS + "type",        rdfNS + "Seq"  )

    // add switch sensor informations
    addResourceTriple( switchName,     rdfsNS + "subClassOf", ns + "sensor"  )
    addResourceTriple( switchName,     ns + "values",         switchValName  )
    addResourceTriple( switchValName,  rdfNS + "type",        rdfNS + "Seq"  )
  }                   

 private def addResourceTriple( s:String, p:String, o:String ){
   execQuery( rdfAddress + "add/ResourceStatement?" + getParams( s, p, o ) )
 }

 private def getParams( s:String, p:String, o:String ):String =
  "subject=" + URLEncoder.encode( s ) +
  "&predicate=" + URLEncoder.encode( p ) +
  "&object=" + URLEncoder.encode( o ) 

 private def execQuery( address:String ){
    try{
      new DefaultHttpClient execute( new HttpGet( address ), new BasicResponseHandler )
    }catch{
      case e:Exception  => log.warn( e.getMessage )
      case _            => log.error( "something went wrong" )
    }
 }

 private def addLiteralTriple( s:String, p:String, o:String ){
   execQuery( rdfAddress + "add/LiteralStatement?" + getParams( s, p, o ) )
 }

}


