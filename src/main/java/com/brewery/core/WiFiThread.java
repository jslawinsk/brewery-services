package com.brewery.core;

import java.net.URI;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.brewery.actuator.WiFiStatus;
import com.brewery.model.Measurement;
import com.brewery.model.Message;
import com.brewery.model.Sensor;
import com.brewery.model.SensorData;
import com.brewery.model.SensorType;
import com.brewery.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("prototype")
public class WiFiThread implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger( WiFiThread.class );

	private static ArrayBlockingQueue<Message> wiFiQueue = new ArrayBlockingQueue<Message>( 100 ); 
	
    @Autowired
    private DataService dataService;
	
    @Autowired
    private WiFiStatus wifiStatus;
    
    @Value("${wiFi.scanSeconds}")
    private int scanSeconds;
    
    @Override
    public void run() {
        LOG.info("Running WiFiThread");
        String statusMessage = "";
        while( true ) {
			try {
				Thread.sleep( 1000 * scanSeconds ); 
				wifiStatus.setUp(true);

				List<Sensor> sensors= dataService.getEnabledSensors( SensorType.WIFI );
				Thread.sleep(500);
				
				Message message = null;
				if( !wiFiQueue.isEmpty() ) {
					message = wiFiQueue.remove();
					LOG.info("Message: " + message );						
				}
				
				statusMessage = "";
				for( Sensor sensor : sensors ) {
					LOG.info( "Active Sensor: " + sensor );
					LOG.info("Connecting to " + sensor.getUrl() );
					statusMessage = statusMessage + sensor.getName() + ": ";

					String credentials = sensor.getUserId()+":"+sensor.getPin();
					byte[] credentialBytes = credentials .getBytes();
					byte[] base64CredentialBytes = Base64.getEncoder().encode(credentialBytes);
					String base64Credentials = new String(base64CredentialBytes);
					
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Credentials );				
					
					headers.setContentType(MediaType.TEXT_PLAIN);
					
				    HttpEntity<String> request = new HttpEntity<>( "", headers);
					
				    URI uri = new URI( sensor.getUrl() );
				    
				    ResponseEntity<String> responseEnt = restTemplate.exchange( uri, HttpMethod.GET, request, String.class );
				    String response = responseEnt.getBody();
				    
					LOG.info( "Sensor response: " + response );
					
			        if( response != null ) {
	    		        ObjectMapper objectMapper = new ObjectMapper();    		        
				        SensorData sensorData = objectMapper.readValue(response, SensorData.class);
				        LOG.info( "SensorData: " + sensorData );
				        Measurement measurement = new Measurement();
				        measurement.setId( 0L );
				    	measurement.setMeasurementTime( new Date() );
				    	measurement.setBatch( sensor.getBatch() );
				    	measurement.setProcess( sensor.getProcess() );
				    	measurement.setType( sensor.getMeasureType() );
				    	measurement.setValueNumber( sensorData.getTemperature() );
				    	measurement.setValueText( "{\"target\":" + sensorData.getTarget() + "}");
				    	if( measurement.getValueNumber() != 0 ) {
				    		dataService.saveMeasurement( measurement );
				    	}
		    	        statusMessage = statusMessage + " data retrieved ";
			        }
/*					
					if( message != null ) {
						LOG.info("Message: " + message  );								
						String target = message.getTarget();
						if( target != null ) {
							LOG.info("Sensor: " + sensor  );								
							if( target.equals( sensor.getName() ) ){
								LOG.info("Sending Message: " + message );								
								OutputStream outStream=streamConnection.openOutputStream();
								PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
								pWriter.write( message.getData() + "\n\r");
								pWriter.flush();
				    	        outStream.close();
				    	        statusMessage = statusMessage + " Message Sent ";
							}
						}
					}
			*/

				}    			        
				wifiStatus.setMessage( statusMessage );
				if( scanSeconds == 0 && wiFiQueue.isEmpty() )  break;
			} catch( Exception e ) {
				e.printStackTrace();
		        statusMessage = statusMessage + ": exeception ";
		        wifiStatus.setUp( false );
			}
        }
    }
    
    static public void sendMessage( Message message ) {
    	wiFiQueue.add( message );
    }
}