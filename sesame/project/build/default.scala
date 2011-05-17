import sbt._
import assembly._

class default(info: ProjectInfo) extends DefaultProject(info) with AssemblyBuilder
{
  val log4j		= "log4j"		% "log4j"		% "1.2.9"
  val commonsLogging	= "commons-logging"	% "commons-logging"	% "1.1.1"
  val slf4jApi		= "org.slf4j"		% "slf4j-api"		% "1.5.6"
  val slf4jLog4j	= "org.slf4j"		% "slf4j-log4j12"	% "1.5.6"

  override def mainClass:Option[String] = Some("swe.helloSemanticWorld")

}