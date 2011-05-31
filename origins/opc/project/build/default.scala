import sbt._
import assembly._

class default(info: ProjectInfo) extends DefaultProject(info) with AssemblyBuilder
{
  val log4j		= "log4j"		% "log4j"			% "1.2.9"
  val commonsLogging	= "commons-logging"	% "commons-logging"		% "1.1.1"

  override def mainClass:Option[String] = Some("swe.origin.opc.OPCServer")

}
