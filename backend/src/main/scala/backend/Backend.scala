package swe.backend

trait Backend {

  def loadFromFileIntoModel( fileName:String )
  def loadFromFileIntoModel( fileName:String, lang:String )

  def addResourceStatement( s:String, p:String, o:String )
  def addLiteralStatement( s:String, p:String, o:String )

  def executeSPARQL( query:String ):String

  def getModel:String
  def getModel( lang:String ):String

}
