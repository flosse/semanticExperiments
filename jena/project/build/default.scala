import sbt._
import assembly._

class default(info: ProjectInfo) extends DefaultProject(info) with AssemblyBuilder
{
  val log4j		= "log4j"		% "log4j"		% "1.2.9"
  val commonsLogging	= "commons-logging"	% "commons-logging"	% "1.1.1"
  val jena		= "com.hp.hpl.jena"	% "jena"		% "2.6.4"
  val arq		= "com.hp.hpl.jena"	% "arq"			% "2.8.8"

  override def mainClass:Option[String] = Some("swe.helloSemanticWorld")

}