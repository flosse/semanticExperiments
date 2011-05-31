import sbt._
import assembly._

class default(info: ProjectInfo) extends DefaultProject(info) with AssemblyBuilder
{
  val log4j		= "log4j"			% "log4j"		% "1.2.9"
  val commonsLogging	= "commons-logging"		% "commons-logging"	% "1.1.1"
  val httpClient	= "org.apache.httpcomponents"	% "httpclient"		% "4.1.1"
  val commonsCodec	= "commons-codec"		% "commons-codec"	% "1.3"

  override def mainClass:Option[String] = Some("swe.lifter.microcontroller.ArduinoLifterStandalone")

}
