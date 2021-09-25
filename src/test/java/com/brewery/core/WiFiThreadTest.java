package com.brewery.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import com.brewery.model.Sensor;
import com.brewery.model.SensorData;
import com.brewery.model.SensorType;
import com.brewery.service.DataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest( properties = { "blueTooth.enabled=false", 
		"wiFi.scanSeconds=0", "wiFi.enabled=false"
} )
@RunWith( SpringRunner.class)
public class WiFiThreadTest {

	static private Logger LOG = LoggerFactory.getLogger( WiFiThreadTest.class );
	
	@MockBean
	DataService dataService;
	
	@MockBean
	JavaMailSender mailSender;
	
	@Autowired
	WiFiThread wiFiThread;

	@Autowired
	RestTemplate  restTemplate;

	@Autowired
	private ObjectMapper objectMapper;	

	private MockRestServiceServer mockServer;	
	
    @Before
    public void init() {
    	LOG.info( "Initiaizing MockRestServiceServer" );
        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(gateway);
    }
	
	
	@Test
	public void testRun() throws JsonProcessingException {
		Sensor sensor = new Sensor();
		sensor.setName( "TEST" );
		sensor.setUrl( "http://10.0.0.22/testdata?responseFormat=JSON" );
    	List<Sensor> sensors = new ArrayList<Sensor>();
    	sensors.add( sensor );
		Mockito.when( dataService.getEnabledSensors( SensorType.WIFI ) ).thenReturn( sensors );
		
		SensorData sensorData = new SensorData();
		sensorData.setTemperature( 70.0 );
		mockServer.expect( requestTo("http://10.0.0.22/testdata?responseFormat=JSON") )
 			.andExpect(method(HttpMethod.GET))
 			.andRespond(withStatus(HttpStatus.OK  )
			.contentType(MediaType.APPLICATION_JSON )
			.body(objectMapper.writeValueAsString( sensorData ))
		); 		
		
		wiFiThread.run();
		
		verify( dataService, atLeast(1)).getEnabledSensors( SensorType.WIFI );		
	}

}
