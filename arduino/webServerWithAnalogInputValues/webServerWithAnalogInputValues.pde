/**
 * This arduino webserver serves the measured values of 
 * a photocell and of a humidity sensor.
 */
#include <SPI.h>
#include <Ethernet.h>

/**
 * Define global Variables.
 */
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
byte ip[] = { 192, 168, 10, 2 };

Server server( 80 );

int BRIGHTNESS_PIN = 0;
int HUMIDITY_PIN = 1;

/**
 * Start the web server.
 */
void setup(){    
  Serial.begin( 9600 ); // only used for debugging
  Ethernet.begin( mac, ip );
  server.begin(); 
  Serial.println( "Server started");
}

/** 
 * Calculates the relative humidity.
 * Humidity RH =  ( ( V_OUT / V_CC ) - 0.16 )  / ( 0.0062 * ( 1.0546 - 0.00216 T ) ) 
 */
double calcRelHumidity( int val ){ 
  return ( 0.00476 * val * 33.0278 ) - 25.73497; 
}

/**
 * Calculates the brightness.
 */
double calcBrightness( int val ){
  return map( val, 0, 1024, 0 , 100 );
}

/**
 * Reads the analog input value of the photocell and converts it.
 */
double getBrightness(){
  int val = analogRead( BRIGHTNESS_PIN );
  return calcBrightness( val );
}

/**
 * Reads the analog input value of the humidity sensor and converts it.
 */
double getRelHumidity(){
  int val = analogRead( HUMIDITY_PIN );
  return calcRelHumidity( val );
}

/**
 * Sends the standard http response header.
 */
void sendStandardResponseHeader( Client client ){
  client.println("HTTP/1.1 200 OK");
  client.println("Content-Type: text/html");
  client.println();
}

/**
 * Sends default response if no path was specified.
 */
void sendStandardContent( Client client ){
  
  client.println("Available data:");
  client.println("<br />");
  client.println("<ul>");
         
  String brightness = String( (int) getBrightness() );
  String humidity = String( (int) getRelHumidity() );                 
            
  client.println("<li><a href='/humidity'>Brightness</a> = " + brightness + "%</li>");              
  client.println("<li><a href='/brightness'>Humidity</a> = " + humidity + "%</li>");                                         
  client.println("</ul>");  
}

/**
 * Reads the http request.
 */
String readRequest( Client client ){

  boolean currentLineIsBlank = true; 
  String request = "";

  while( client.connected() ){
    
    if( client.available() ){
       
      char c = client.read();      

      if( c == '\n' && currentLineIsBlank ){
        break;
      }  
      if( c == '\n' ){  
        currentLineIsBlank = true; 
      } 
      else if( c != '\r' ){
        currentLineIsBlank = false; 
      }
      request += c;
    }
  }
  return request;
}

/**
 * Processes the http request.
 */
void processRequest( Client client, String request ){

  Serial.println( "The following request was received:" );
  Serial.println( request );
  // at the moment, just answer with default content
  sendStandardResponseHeader( client );
  sendStandardContent( client );        
  delay(1);      // give the browser time to receive the data
  client.stop(); // close connection:
}

/**
 * Start main loop.
 */
void loop()
{
  // listen for incoming clients
  Client client = server.available();
  
  if( client ){  
    processRequest( client, readRequest( client ) );
  }  
}
