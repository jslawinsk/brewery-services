package com.brewery.core;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import com.brewery.model.Batch;
import com.brewery.model.DbSync;
import com.brewery.model.GraphTypes;
import com.brewery.model.MeasureType;
import com.brewery.model.Measurement;
import com.brewery.model.Process;
import com.brewery.model.Sensor;
import com.brewery.model.Style;
import com.brewery.model.User;
import com.brewery.model.UserRoles;
import com.brewery.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest( properties = { "blueTooth.enabled=false", 
								"dataSynch.delayMinutes=0",
								"dataSynch.url=http://localhost:8080/api/",
								"dataSynch.apiId=BrewAppTest", 
								"dataSynch.apiPassword=BrewAppTest", 
								"dataSynch.pullConfig=true",
								"wiFi.enabled=false"
				} )
@RunWith( SpringRunner.class)
class DataSynchThreadTest {

	static private Logger LOG = LoggerFactory.getLogger( DataSynchThreadTest.class );
	
	@MockBean
	DataService dataService;

	@MockBean
	JavaMailSender mailSender;
	
	@Autowired
    @Qualifier( "restTemplate" )
	RestTemplate  restTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;	

	@Autowired
	DataSynchThread dbSyncThread;

	private MockRestServiceServer mockServer;	
	
	@BeforeEach
	void setUp() throws Exception {
    	LOG.info( "Initiaizing MockRestServiceServer" );
        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(gateway);
	}
    
	@Test
	void run() throws Exception
	{
		User user = new User( "BrewAppTest", "test", DbSync.ADD, UserRoles.API.toString() );
		user.setToken( "test token" );
		Mockito.when(dataService.getUserByName( "BrewAppTest" )).thenReturn( user );
		
		mockServer.expect( requestTo("http://localhost:8080/api/authorize") )
	 		.andExpect(method(HttpMethod.POST))
			.andRespond(withStatus(HttpStatus.OK  )
			.contentType(MediaType.APPLICATION_JSON )
			.body(objectMapper.writeValueAsString(user))
			); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/heartBeat") )
		 	.andExpect(method(HttpMethod.GET))
	        .andRespond(withStatus(HttpStatus.OK  )
	        .contentType( MediaType.TEXT_PLAIN )
	        .body( "ACK" )
        	); 		

		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
    	List<Style> styles = new ArrayList<Style>();
    	styles.add( testStyle );
		testStyle = new Style( "IPA", "18a", "Hoppy", DbSync.UPDATE, "TestToken" );
    	styles.add( testStyle );
		testStyle = new Style( "IPA", "18a", "Hoppy", DbSync.UPDATE, "" );
    	styles.add( testStyle );
		testStyle = new Style( "IPA", "18a", "Hoppy", DbSync.DELETE, "TestToken" );
    	styles.add( testStyle );
		testStyle = new Style( "IPA", "18a", "Hoppy", DbSync.DELETE, "" );
    	styles.add( testStyle );
    	Style testStyle2 = new Style( "IPA", "18a", "Hoppy", DbSync.SYNCHED, "TestToken2" );
    	styles.add( testStyle2 );
		Mockito.when(dataService.getStylesToSynchronize( )).thenReturn( styles );
        Mockito.when(dataService.getStyle( "TestToken2" )).thenReturn( testStyle2 );
		
		mockServer.expect( requestTo("http://localhost:8080/api/style") )
 		.andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(testStyle))
		); 		

		Process process = new Process( "FRM", "Fermentation", false, DbSync.ADD );
    	List<Process> processes = new ArrayList<Process>();
    	processes.add( process );
		process = new Process( "FRM", "Fermentation", false, DbSync.UPDATE );
    	processes.add( process );
		process = new Process( "FRM", "Fermentation", false, DbSync.DELETE );
    	processes.add( process );
		Process process2 = new Process( "TEST", "Fermentation", false, DbSync.SYNCHED );
    	processes.add( process2 );
		Mockito.when(dataService.getProcessesToSynchronize()).thenReturn( processes );
        Mockito.when(dataService.getProcess( "FRM" )).thenReturn( process );
		
		mockServer.expect( requestTo("http://localhost:8080/api/process") )
 		.andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(process))
		); 		
	
		MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
    	List<MeasureType> measureTypes = new ArrayList<MeasureType>();
    	measureTypes.add( measureType );
		measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.UPDATE  );
    	measureTypes.add( measureType );
		measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.DELETE  );
    	measureTypes.add( measureType );
    	MeasureType measureType2 = new MeasureType( "TEST", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.SYNCHED  );
    	measureTypes.add( measureType2 );
		Mockito.when( dataService.getMeasureTypesToSynchronize()).thenReturn( measureTypes );
        Mockito.when(dataService.getMeasureType( "TMP" )).thenReturn( measureType );
		
		mockServer.expect( requestTo("http://localhost:8080/api/measureType") )
 		.andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(measureType))
		); 		

		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
    	List<Batch> batches = new ArrayList<Batch>();
    	batches.add( testBatch );
		testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date(), DbSync.UPDATE, "TestToken" );
    	batches.add( testBatch );
		testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date(), DbSync.UPDATE, "" );
    	batches.add( testBatch );
		testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date(), DbSync.DELETE, "TestToken" );
    	batches.add( testBatch );
		testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date(), DbSync.DELETE, "" );
    	batches.add( testBatch );
		Mockito.when(dataService.getBatchesToSynchronize()).thenReturn( batches );

		mockServer.expect( requestTo("http://localhost:8080/api/batch") )
 		.andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(testBatch))
		); 		

		Sensor sensor = new Sensor();
    	List<Sensor> sensors = new ArrayList<Sensor>();
    	sensors.add( sensor );
        sensor = new Sensor( 2L, false, "test2", "", "", "1234", "BLUETOOTH", "", null, process, measureType, new Date(), DbSync.UPDATE, "TestToken" );
    	sensors.add( sensor );
        sensor = new Sensor( 2L, false, "test2", "", "", "1234", "BLUETOOTH", "", null, process, measureType, new Date(), DbSync.UPDATE, "" );
    	sensors.add( sensor );
        sensor = new Sensor( 2L, false, "test2", "", "", "1234", "BLUETOOTH", "", null, process, measureType, new Date(), DbSync.DELETE, "TestToken" );
    	sensors.add( sensor );
        sensor = new Sensor( 2L, false, "test2", "", "", "1234", "BLUETOOTH", "", null, process, measureType, new Date(), DbSync.DELETE, "" );
    	sensors.add( sensor );
		Mockito.when( dataService.getSensorsToSynchronize() ).thenReturn( sensors );
		
		mockServer.expect( requestTo("http://localhost:8080/api/sensor") )
 		.andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString( sensor ))
		); 		
		
		Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
    	List<Measurement> measurements = new ArrayList<Measurement>();
    	measurements.add( measurement );
    	measurements.add( new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() ) );
    	measurement = new Measurement( 70.3, "{\"target\":77.0}", testBatch, process, measureType, new Date(), DbSync.UPDATE, "TestToken" );
    	measurements.add( measurement );
    	measurement = new Measurement( 70.3, "{\"target\":77.0}", testBatch, process, measureType, new Date(), DbSync.UPDATE, "" );
    	measurements.add( measurement );
    	measurement = new Measurement( 70.3, "{\"target\":77.0}", testBatch, process, measureType, new Date(), DbSync.DELETE, "TestToken" );
    	measurements.add( measurement );
    	measurement = new Measurement( 70.3, "{\"target\":77.0}", testBatch, process, measureType, new Date(), DbSync.DELETE, "" );
    	measurements.add( measurement );
		Mockito.when( dataService.getMeasurementsToSynchronize() ).thenReturn( measurements );
        
		mockServer.expect( requestTo("http://localhost:8080/api/measurement") )
 		.andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString( measurement ))
		); 		
		
		//
		//	Update API Tests
		//
		mockServer.expect( requestTo("http://localhost:8080/api/style") )
	 		.andExpect(method(HttpMethod.PUT))
			.andRespond(withStatus(HttpStatus.OK  )
			.contentType(MediaType.APPLICATION_JSON )
			.body(objectMapper.writeValueAsString(testStyle))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/process") )
 		.andExpect(method(HttpMethod.PUT))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(process))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/measureType") )
 		.andExpect(method(HttpMethod.PUT))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(measureType))
		); 		

		mockServer.expect( requestTo("http://localhost:8080/api/batch") )
 		.andExpect(method(HttpMethod.PUT))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(testBatch))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/measurement") )
 		.andExpect(method(HttpMethod.PUT))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString( measurement ))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/sensor") )
 		.andExpect(method(HttpMethod.PUT))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString( sensor ))
		); 		
		
		//
		//	Delete API Test
		//
		mockServer.expect( requestTo("http://localhost:8080/api/sensor/synchToken/TestToken") )
 		.andExpect(method(HttpMethod.DELETE))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString( sensor ))
		); 		

		mockServer.expect( requestTo("http://localhost:8080/api/measurement/synchToken/TestToken") )
 		.andExpect(method(HttpMethod.DELETE))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString( measurement ))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/batch/synchToken/TestToken") )
 		.andExpect(method(HttpMethod.DELETE))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(testBatch))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/measureType/TMP") )
 		.andExpect(method(HttpMethod.DELETE))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(measureType))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/process/FRM") )
 		.andExpect(method(HttpMethod.DELETE))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(process))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/style/synchToken/TestToken") )
 		.andExpect(method(HttpMethod.DELETE))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(testStyle))
		); 		

		//
		//	Test for pulling configuration data
		//
		mockServer.expect( requestTo("http://localhost:8080/api/measureType") )
 		.andExpect(method(HttpMethod.GET))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(measureTypes))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/process") )
 		.andExpect(method(HttpMethod.GET))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(processes))
		); 		
		
		mockServer.expect( requestTo("http://localhost:8080/api/style") )
 		.andExpect(method(HttpMethod.GET))
		.andRespond(withStatus(HttpStatus.OK  )
		.contentType(MediaType.APPLICATION_JSON )
		.body(objectMapper.writeValueAsString(styles))
		); 		
		
		//
		//	Execute Tests
		//
		dbSyncThread.run();
		
		mockServer.verify();
	}
	
}
