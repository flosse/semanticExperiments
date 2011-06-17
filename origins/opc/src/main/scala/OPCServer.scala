package swe.origin.opc

import java.io._
import java.net._
import java.security.cert._
import java.util._
import java.util.concurrent._

import scala.actors._
import scala.util.Random

import org.apache.commons.logging._
import org.apache.log4j._

import org.opcfoundation.ua.builtintypes._
import org.opcfoundation.ua.common._
import org.opcfoundation.ua.core._
import org.opcfoundation.ua.transport.security._
import org.opcfoundation.ua.utils._

import com.prosysopc.ua._
import com.prosysopc.ua.PkiFileBasedCertificateValidator
import com.prosysopc.ua.PkiFileBasedCertificateValidator._
import com.prosysopc.ua.UaApplication._
import com.prosysopc.ua.server._
import com.prosysopc.ua.server.nodes._
import com.prosysopc.ua.server.nodes.opcua._

object OPCServer extends App{

  private val log:Log = LogFactory.getLog( this.getClass )

  private val server = new UaServer
	private val validator = new PkiFileBasedCertificateValidator
  private val appDescription = createAppDescription
  private val identity = createAppIdentity( appDescription )
  private val port = 52520
  private val serverName = "OPCServer"
  private val url = "github.com/flosse/semanticExperiments"
  private val addressSpaceName = "SampleAddressSpace"

  log.debug( "setup server ..." )
  setupServer

  log.debug( "init server ..." )
  initServer

  log.debug( "setup address space server ..." )
  setupAddressSpace

  log.debug( "start server ..." )
  startServer
  log.debug( "... server started" )

  private def createAppDescription = {

    val appDescription = new ApplicationDescription

    appDescription.setApplicationName( new LocalizedText( serverName, Locale.ENGLISH ) )
    appDescription.setApplicationUri( "urn:localhost:UA:" + serverName )
    appDescription.setProductUri( "urn:" + url + ":UA:" + serverName )
    appDescription.setApplicationType( ApplicationType.Server )

    appDescription
  }

  private def createAppIdentity( appDescription:ApplicationDescription ) =
    ApplicationIdentity
      .loadOrCreateCertificate(
        appDescription,
        "Semantic Experiments",
        null,
        new File( validator.getBaseDir, "private" )
        ,true
      )

  private def setupServer {

    server.setApplicationIdentity( identity )
    server.setPort( port )
    server.setUseLocalhost( true )
    server.setServerName( serverName )
    server.setUseAllIpAddresses( true )
    server.setSecurityModes( SecurityMode.ALL )
    server.setCertificateValidator( validator )

    addUserTokenPolicies
    addValidationListener
    addUserValidator
  }

  def addUserTokenPolicies{
    server.addUserTokenPolicy( UserTokenPolicy.ANONYMOUS )
    server.addUserTokenPolicy( UserTokenPolicy.SECURE_USERNAME_PASSWORD )
    server.addUserTokenPolicy( UserTokenPolicy.SECURE_CERTIFICATE )
  }

  def addValidationListener{

    validator.setValidationListener( new CertificateValidationListener() {
      def onValidate(
          certificate:Cert,
          applicationDescription:ApplicationDescription,
          passedChecks:EnumSet[CertificateCheck]
        ) = ValidationResult.AcceptPermanently
      })
  }

  def addUserValidator {
    server.setUserValidator( new UserValidator() {
      def onValidate( session:Session, userIdentity:UserIdentity ) = true
    })
  }

  private def initServer = server.init

  private def setupAddressSpace {

    val myNodeManager = new NodeManagerUaNode(
      server,"http://" + url + "/" + addressSpaceName )

    val ns = myNodeManager.getNamespaceIndex

    var mySwitch = new PlainVariable[Boolean](myNodeManager, new NodeId( ns, "MySwitch") , "MySwitch", Locale.ENGLISH )
    mySwitch.setCurrentValue( false )
    myNodeManager.addNode( mySwitch )

    var number = new PlainVariable[Int]( myNodeManager, new NodeId( ns, "MyNumber"), "MyNumber", Locale.ENGLISH )
    number.setCurrentValue( 13 )
    myNodeManager.addNode( number )

    var simulator = new Actor{
      def act{ loop{
        mySwitch.setCurrentValue( Random.nextBoolean  )
        number.setCurrentValue( Random.nextInt )

        Thread.sleep( 6000 )
      } }
    }
    simulator.start
  }

  private def addObjectFolder{

  }

  private def startServer = server.start

}

