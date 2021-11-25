package com.brewery.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.commons.net.util.*;
//import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;
import org.hamcrest.Matchers;

import com.brewery.model.Sensor;

@SpringBootTest()
public class WiFiServiceTest {

	static private Logger LOG = LoggerFactory.getLogger( WiFiServiceTest.class ); 
	
	@Autowired
    @Qualifier( "restTemplateWifi" )
	RestTemplate  restTemplateWifi;
	
	@Autowired
	private WiFiService wiFiService;

	private static MockRestServiceServer mockServer;	
	
	@MockBean
	JavaMailSender mailSender;
	
	@MockBean
	SubnetInfo subnetInfo;
    	
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
 	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	public void setUp() throws Exception {
    	LOG.info( "Initiaizing MockRestServiceServer" );
        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate( restTemplateWifi );
        mockServer = MockRestServiceServer.createServer(gateway);
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void testDiscoverSensors() throws Exception {

		
		mockServer.expect( 
			requestTo("http://190.0.0.1/tempdata?responseFormat=JSON") )
 				.andExpect(method(HttpMethod.GET))
 				.andRespond(withStatus(HttpStatus.UNAUTHORIZED  ) );

		mockServer.expect( 
			requestTo("http://190.0.0.2/tempdata?responseFormat=JSON") )
 				.andExpect(method(HttpMethod.GET))
 				.andRespond( withServerError() );

		mockServer.expect( 
			requestTo("http://190.0.0.3/tempdata?responseFormat=JSON") )
 				.andExpect(method(HttpMethod.GET))
 				.andRespond(withStatus(HttpStatus.BAD_REQUEST  ) );
		
		mockServer.expect( 
			ExpectedCount.times( 3 ),
			requestTo( org.hamcrest.Matchers.matchesPattern( "http://190.0.0.*" ) ) )
 				.andExpect(method(HttpMethod.GET))
 				.andRespond(withStatus(HttpStatus.OK  )
 						.contentType(MediaType.TEXT_PLAIN ));
		
		List<Sensor> sensors = wiFiService.discoverSensors( "190.0.0.0/29" );
		mockServer.verify();
		assertNotNull( sensors );
		
		sensors = wiFiService.discoverSensors( "invalid" );
		mockServer.verify();
		assertNotNull( sensors );
	}

}
