package com.brewery.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.bluetooth.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.brewery.actuator.DataSynchStatus;
import com.brewery.model.Batch;
import com.brewery.model.DbSync;
import com.brewery.model.MeasureType;
import com.brewery.model.Measurement;
import com.brewery.model.Message;
import com.brewery.model.Sensor;
import com.brewery.model.SensorData;
import com.brewery.model.Style;
import com.brewery.model.User;
import com.brewery.model.Process;
import com.brewery.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("prototype")
public class DataSynchThread implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger( DataSynchThread.class );

	@Autowired
    private DataService dataService;
    
    @Autowired
    private DataSynchStatus dataSynchStatus;

    @Value("${dataSynch.url}")
    private String dataSynchUrl;    
    
    @Value("${dataSynch.delayMinutes}")
    private int delayMinutes;
    
    @Value("${dataSynch.apiId}")
    private String apiUserId;    

    @Value("${dataSynch.apiPassword}")
    private String apiUserPassword;    
    
    @Value("${dataSynch.deleteDuplicates}")
    private boolean deleteDuplicates;
    
    @Value("${dataSynch.deleteSynched}")
    private boolean deleteSynched;
    
    @Autowired
    @Qualifier( "restTemplate" )
    private RestTemplate restTemplate;
    
    @Override
    public void run() {
        LOG.info("Running DataSynchThread");
        String statusMessage = "";
        while( true ) {
			try {
				Thread.sleep( 1000 * delayMinutes * 60 ); 
				dataSynchStatus.setUp(true);
				statusMessage = "Syncronizing data: ";
				User apiUser = dataService.getUserByName(apiUserId);
				if( apiUser != null ) {
					String token = "";
		        	int attempt = 0;
					User autheticatedUser = null;
		        	while( autheticatedUser == null && attempt < 10 ) {
						try {
							
							HttpHeaders headers = new HttpHeaders();
							headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
							MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
							map.add("user", apiUserId );
							map.add("password", apiUserPassword );
							HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

							ResponseEntity<User> respUser = restTemplate.postForEntity( dataSynchUrl + "authorize", request , User.class );							
							
							autheticatedUser = respUser.getBody();
							LOG.info( "API Auth response: " + autheticatedUser );
							if( autheticatedUser != null ) {
								token = autheticatedUser.getToken();
							}
						} catch( Exception e ) {
							LOG.error( "User Authentication Exception: " + e.getMessage() );
						}	
						attempt++;
		        	}
			
		        	if( token != null && token.length() > 0 ) {
			        	String response = "";
			        	attempt = 0;
			        	while( !"ACK".equals( response ) && attempt < 10 ) {
							try {
								
								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.TEXT_PLAIN);
								headers.setBearerAuth(token);
								
							    HttpEntity<String> request = new HttpEntity<>( "", headers);
								
							    URI uri = new URI( dataSynchUrl + "heartBeat");
							    // ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
							    
							    ResponseEntity<String> responseEnt = restTemplate.exchange( uri, HttpMethod.GET, request, String.class );
							    response = responseEnt.getBody();
							    
								LOG.info( "DB Synch health response: " + response );
							} catch( Exception e ) {
								LOG.error( "heartBeat: " + e.getMessage() );
							}	
							attempt++;
			        	}
						
		        	//
		        	// 1st Phase Process Adding New Data
		        	//
			        	//
			        	//	Synchronize Style Table for adding new data
			        	//
						List<Style> styles= dataService.getStylesToSynchronize();
						for( Style style: styles ) {
							LOG.info( "Style to Synchronize: " + style );
							if( style.getDbSynch() == DbSync.ADD ) {
								LOG.info( "Synchronize Add: " + style );
								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.APPLICATION_JSON);
								headers.setBearerAuth(token);
							    HttpEntity<Style> request = new HttpEntity<>(style, headers);
								
							    URI uri = new URI( dataSynchUrl + "style");
							    ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize Style local update" );
									style.setDbSynch( DbSync.SYNCHED );
							    	dataService.updateStyle( style );
							    }
							}
						}
		
			        	//
			        	//	Synchronize Process Table for adding new data
			        	//
						List<Process> processes = dataService.getProcessesToSynchronize();
						for( Process process: processes ) {
							if( process.getDbSynch() == DbSync.ADD ) {
								LOG.info( "Synchronize Add Process: " + process );
								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.APPLICATION_JSON);		
								headers.setBearerAuth(token);
							    HttpEntity<Process> request = new HttpEntity<>(process, headers);
								
							    URI uri = new URI( dataSynchUrl + "process");
							    ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize Process local update" );
									process.setDbSynch( DbSync.SYNCHED );
							    	dataService.updateProcess( process );
							    }
							}
						}
		
			        	//
			        	//	Synchronize Measure Types Table for adding new data
			        	//
						List<MeasureType> measureTypes = dataService.getMeasureTypesToSynchronize();
						for( MeasureType measureType: measureTypes ) {
							if( measureType.getDbSynch() == DbSync.ADD ) {
								LOG.info( "Synchronize Add MeasureType: " + measureType );
								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.APPLICATION_JSON);		
								headers.setBearerAuth(token);
							    HttpEntity<MeasureType> request = new HttpEntity<>(measureType, headers);
								
							    URI uri = new URI( dataSynchUrl + "measureType");
							    ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize MeasureType local update" );
									measureType.setDbSynch( DbSync.SYNCHED );
							    	dataService.updateMeasureType( measureType );
							    }
							}
						}
		
			        	//
			        	//	Synchronize Batch Table for adding new data
			        	//
						List<Batch> batches = dataService.getBatchesToSynchronize();
						for( Batch batch: batches ) {
							if( batch.getDbSynch() == DbSync.ADD ) {
								LOG.info( "Synchronize Add: " + batch.getName() );
								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.APPLICATION_JSON);		
								headers.setBearerAuth(token);
								
							    HttpEntity<Batch> request = new HttpEntity<>(batch, headers);
								
							    URI uri = new URI( dataSynchUrl + "batch");
							     
							    ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize Batch local update" );
									batch.setDbSynch( DbSync.SYNCHED );
							    	dataService.updateBatch( batch );
							    }
							}
						}
						
			        	//
			        	//	Synchronize Sensor Table
			        	//
						//Thread.sleep(500);
						List<Sensor> sensors = dataService.getSensorsToSynchronize();
						for( Sensor sensor: sensors ) {
							LOG.info( "Sensor to Synchronize: " + sensor );
							if( sensor.getDbSynch() == DbSync.ADD ) {
								LOG.info( "Synchronize Add: " + sensor.getName() );
								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.APPLICATION_JSON);		
								headers.setBearerAuth(token);
								
								boolean enabled = sensor.isEnabled();
								sensor.setEnabled( false );
								
							    HttpEntity<Sensor> request = new HttpEntity<>(sensor, headers);
								
							    URI uri = new URI( dataSynchUrl + "sensor");
							     
							    ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize Sensor local update" );
									sensor.setEnabled( enabled );
									sensor.setDbSynch( DbSync.SYNCHED );
							    	dataService.updateSensor( sensor );
							    }
							}
						}
		
			        	//
			        	//	Synchronize Measurement Table
			        	//
						//Thread.sleep(500);
						Long batchId = -1L;
						Double value = -1D;
						String measureTypeStr = "";
						String processStr = "";
						
						List<Measurement> measurements = dataService.getMeasurementsToSynchronize();
						for( Measurement measurement: measurements ) {
							LOG.info( "Measurement to Synchronize: " + measurement );
							if( measurement.getDbSynch() == DbSync.ADD ) {
								boolean publish = false;
								if( batchId != measurement.getBatch().getId() 
										|| value + .5 < measurement.getValueNumber()
										|| value -.5 > measurement.getValueNumber()
										|| !measureTypeStr.equals( measurement.getType().getCode() )
										|| !processStr.equals( measurement.getProcess().getCode() )
									) {
									publish = true;
									batchId = measurement.getBatch().getId();
									value = measurement.getValueNumber();
									measureTypeStr = measurement.getType().getCode();
									processStr = measurement.getProcess().getCode();
								}
								if( publish ) {
									LOG.info( "Synchronize Add: " + measurement.getId() );
									HttpHeaders headers = new HttpHeaders();
									headers.setContentType(MediaType.APPLICATION_JSON);		
									headers.setBearerAuth(token);
									
								    HttpEntity<Measurement> request = new HttpEntity<>(measurement, headers);
									
								    URI uri = new URI( dataSynchUrl + "measurement");
								     
								    ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
									LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
								    if( result.getStatusCode() == HttpStatus.OK ) {
								    	if( deleteSynched ) {
									    	LOG.info( "Synchronize Measurement local delete" );
									    	dataService.deleteMeasurement( measurement.getId() );
								    	}
								    	else {
									    	LOG.info( "Synchronize Measurement local update" );
											measurement.setDbSynch( DbSync.SYNCHED );
									    	dataService.updateMeasurement( measurement );
								    	}
								    }
								}
								else {
									if( deleteDuplicates ) {
								    	LOG.info( "Delete Duplicate" );
								    	dataService.deleteMeasurement( measurement.getId() );
									}
									else {
								    	LOG.info( "Synchronize ignore" );
										measurement.setDbSynch( DbSync.IGNORE );
								    	dataService.updateMeasurement( measurement );
									}
								}
							}
						}
						
		        	//
		        	// 2nd Phase Process Updating Existing Data
		        	//
			        	//
			        	//	Synchronize Style Table for updating data
			        	//
						for( Style style: styles ) {
							if( style.getDbSynch() == DbSync.UPDATE ) {
								LOG.info( "Synchronize Update Style: " + style );
								if( style.getDbSynchToken() != null && style.getDbSynchToken().length() > 0 ) {
									HttpHeaders headers = new HttpHeaders();
									headers.setContentType(MediaType.APPLICATION_JSON);
									headers.setBearerAuth(token);
								    HttpEntity<Style> request = new HttpEntity<>(style, headers);
									
								    URI uri = new URI( dataSynchUrl + "style");
								    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class );
									LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
								    if( result.getStatusCode() == HttpStatus.OK ) {
								    	LOG.info( "Synchronize Style local update" );
										style.setDbSynch( DbSync.SYNCHED );
								    	dataService.updateStyle( style );
								    }
								}
								else{
									LOG.error( "ERROR: Synchronize Update Style: Invalid DbSynchToken: " + style );
									dataSynchStatus.setUp( false ); 
									statusMessage = statusMessage +  "Synchronize Update Style: Invalid DbSynchToken: " + style;
								}
							}
						}
						
			        	//
			        	//	Synchronize Process Table for updating data
			        	//
						for( Process process: processes ) {
							if( process.getDbSynch() == DbSync.UPDATE ) {
								LOG.info( "Synchronize Update Process: " + process );
								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.APPLICATION_JSON);		
								headers.setBearerAuth(token);
							    HttpEntity<Process> request = new HttpEntity<>(process, headers);
								
							    URI uri = new URI( dataSynchUrl + "process");
							    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class );
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize Process local update" );
									process.setDbSynch( DbSync.SYNCHED );
							    	dataService.updateProcess( process );
							    }
							}
						}
						
			        	//
			        	//	Synchronize Measure Types Table for updating data
			        	//
						for( MeasureType measureType: measureTypes ) {
							if( measureType.getDbSynch() == DbSync.UPDATE ) {
								LOG.info( "Synchronize Update MeasureType: " + measureType );
								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.APPLICATION_JSON);		
								headers.setBearerAuth(token);
							    HttpEntity<MeasureType> request = new HttpEntity<>(measureType, headers);
								
							    URI uri = new URI( dataSynchUrl + "measureType");
							    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class );
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize MeasureType local update" );
									measureType.setDbSynch( DbSync.SYNCHED );
							    	dataService.updateMeasureType( measureType );
							    }
							}
						}

			        	//
			        	//	Synchronize Batch Table for updating data
			        	//
						for( Batch batch: batches ) {
							if( batch.getDbSynch() == DbSync.UPDATE ) {
								LOG.info( "Synchronize Update Batch: " + batch.getName() );
								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.APPLICATION_JSON);		
								headers.setBearerAuth(token);
							    HttpEntity<Batch> request = new HttpEntity<>(batch, headers);
								
							    URI uri = new URI( dataSynchUrl + "batch");
							    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class );
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize Batch local update" );
									batch.setDbSynch( DbSync.SYNCHED );
							    	dataService.updateBatch( batch );
							    }
							}
						}
						
	        	//
	        	// 3rd Phase Process Data Deletion
	        	//
			        	//
			        	//	Synchronize Batch Table for deleting data
			        	//
						for( Batch batch: batches ) {
							if( batch.getDbSynch() == DbSync.DELETE ) {
								LOG.info( "Synchronize Delete Batch: " + batch.getName() );
								if( batch.getDbSynchToken() != null && batch.getDbSynchToken().length() > 0 ) {
									HttpHeaders headers = new HttpHeaders();
									headers.setBearerAuth(token);
								    HttpEntity<Batch> request = new HttpEntity<>( headers );
									
								    URI uri = new URI( dataSynchUrl + "batch/synchToken/" + batch.getDbSynchToken() );
								    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class );
									LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
								    if( result.getStatusCode() == HttpStatus.OK ) {
								    	LOG.info( "Synchronize Batch local update" );
										batch.setDbSynch( DbSync.SYNCHED );
								    	dataService.updateBatch( batch );
								    }
								}
								else{
									LOG.error( "ERROR: Synchronize Delete Batch: Invalid DbSynchToken: " + batch );
									dataSynchStatus.setUp( false ); 
									statusMessage = statusMessage +  " Synchronize Delete Batch: Invalid DbSynchToken: " + batch;
								}
							}
						}

			        	//
			        	//	Synchronize Measure Types Table for deleting data
			        	//
						for( MeasureType measureType: measureTypes ) {
							if( measureType.getDbSynch() == DbSync.DELETE ) {
								LOG.info( "Synchronize Delete MeasureType: " + measureType.getName() );
								HttpHeaders headers = new HttpHeaders();
								headers.setBearerAuth(token);
							    HttpEntity<MeasureType> request = new HttpEntity<>( headers );
								
							    URI uri = new URI( dataSynchUrl + "measureType/" + measureType.getCode() );
							    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class );
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize MeasureType local delete" );
							    	dataService.deleteMeasureType( measureType.getCode() );
							    }
							}
						}
						
						//
			        	//	Synchronize Process Table for deleting data
			        	//
						for( Process process: processes ) {
							if( process.getDbSynch() == DbSync.DELETE ) {
								LOG.info( "Synchronize Delete Process: " + process.getName() );
								HttpHeaders headers = new HttpHeaders();
								headers.setBearerAuth(token);
							    HttpEntity<Process> request = new HttpEntity<>( headers );
								
							    URI uri = new URI( dataSynchUrl + "process/" + process.getCode() );
							    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class );
								LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
							    if( result.getStatusCode() == HttpStatus.OK ) {
							    	LOG.info( "Synchronize Process local delete" );
							    	dataService.deleteProcess( process.getCode() );
							    }
							}
						}
			        	
						//
			        	//	Synchronize Style Table for deleting data
			        	//
						for( Style style: styles ) {
							if( style.getDbSynch() == DbSync.DELETE ) {
								LOG.info( "Synchronize Delete Style: " + style );
								if( style.getDbSynchToken() != null && style.getDbSynchToken().length() > 0 ) {
									HttpHeaders headers = new HttpHeaders();
									headers.setBearerAuth(token);
								    HttpEntity<Style> request = new HttpEntity<>( headers);
									
								    URI uri = new URI( dataSynchUrl + "style/synchToken/" + style.getDbSynchToken() );
								    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class );
									LOG.info( "Synchronize result: " + result.getStatusCodeValue() + " : "  + result.toString() );
								    if( result.getStatusCode() == HttpStatus.OK ) {
								    	LOG.info( "Synchronize Style local update" );
								    	dataService.deleteStyle( style.getId() );
								    }
								}
								else{
									LOG.error( "ERROR: Synchronize Delete Style: Invalid DbSynchToken: " + style );
									dataSynchStatus.setUp( false ); 
									statusMessage = statusMessage +  " Synchronize Delete Style: Invalid DbSynchToken: " + style;
								}
							}
						}
						
		        	}
		        	else {
				    	LOG.info( "API User not autheticated" );
				    	statusMessage = statusMessage + "API User not autheticated ";
				    	dataSynchStatus.setUp( false );
		        	}
				}
				else {
			    	LOG.info( "API User not found" );
			    	statusMessage = statusMessage +  "API User not found ";
			    	dataSynchStatus.setUp( false );
				}
			} catch( Exception e ) {
				LOG.error( e.getMessage() );
				statusMessage = statusMessage + "Exception";
				dataSynchStatus.setUp( false );
			}	
	        dataSynchStatus.setMessage( statusMessage );
	        if( delayMinutes == 0 ) break;
        }
    }
    
}