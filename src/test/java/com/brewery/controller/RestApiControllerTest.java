package com.brewery.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

@RunWith( SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
		)
@AutoConfigureMockMvc
public class RestApiControllerTest {

    static private Logger LOG = LoggerFactory.getLogger( RestApiControllerTest.class );
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;	
	
	@MockBean
	DataService dataService;
	
	private Style testStyle = new Style( "IPA", "18a", "Hoppy" );
	private Process process = new Process( "FRM", "Fermentation" );
	private MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
	private Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
	private Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
	private Sensor sensor = new Sensor();
	
    @BeforeClass
    public static void beforeAllTestMethods() {
    }
 
    @Before
    public void beforeEachTestMethod() {
    }
 
    @After
    public void afterEachTestMethod() {
    }
 	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void heartBeat() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "api/heartBeat")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("ACK")));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void getMeasurementSummary() throws Exception
	{
		testBatch.setId( 1L );
    	List<Batch> batches = new ArrayList<Batch>();
    	batches.add( testBatch );
		
		MeasureType measureType2 = new MeasureType( "PH", "PH", true, 0, 14, GraphTypes.SOLID_GUAGE, DbSync.ADD );
		Measurement measurement2 = new Measurement( 7.0, "{\"target\":7.0}", testBatch, process, measureType2, new Date() );

    	List<Measurement> measurements = new ArrayList<Measurement>();
    	measurements.add( measurement );
    	measurements.add( measurement2 );
		
        Mockito.when(dataService.getActiveBatches()).thenReturn( batches );
        Mockito.when(dataService.getRecentMeasurement( 1L )).thenReturn( measurements );
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "api/summary")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString( "\"{\\\"target\\\":70.0}\"," )))
	            ;
	}	

	//
	//	Style Rest API Test Methods
	//
	@Test
	@WithMockUser( authorities = "API" )
	public void getStyle() throws Exception
	{
		testStyle.setId( 1L );
		Mockito.when(dataService.getStyle( 1L )).thenReturn( testStyle );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "api/style/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void saveStyle() throws Exception
	{
		testStyle.setId( 1L );
		Mockito.when(dataService.saveStyle( Mockito.any(Style.class) )).thenReturn( testStyle );

        mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "api/style" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( testStyle ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void updateStyle() throws Exception
	{
		testStyle.setId( 1L );
		Mockito.when(dataService.updateStyle( Mockito.any(Style.class) )).thenReturn( testStyle );

        mockMvc.perform( MockMvcRequestBuilders.put("http://localhost:" + port + "api/style" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( testStyle ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void deleteStyle() throws Exception
	{
        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:" + port + "api/style/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk());
	}	

	//
	//	Process Rest API Test Methods
	//
	@Test
	@WithMockUser( authorities = "API" )
	public void getProcess() throws Exception
	{
        Mockito.when(dataService.getProcess( "FRM" )).thenReturn( process );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "api/process/FRM")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString( "{\"code\":\"FRM\"," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void saveProcess() throws Exception
	{
		Mockito.when(dataService.saveProcess( Mockito.any(Process.class) )).thenReturn( process );

        mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "api/process" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( process ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"code\":\"FRM\"," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void updateProcess() throws Exception
	{
		Mockito.when(dataService.updateProcess( Mockito.any(Process.class) )).thenReturn( process );

        mockMvc.perform( MockMvcRequestBuilders.put("http://localhost:" + port + "api/process" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( process ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"code\":\"FRM\"," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void deleteProcess() throws Exception
	{
        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:" + port + "api/process/FRM")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk());
	}	

	//
	//	MeasureType Rest API Test Methods
	//
	@Test
	@WithMockUser( authorities = "API" )
	public void getMeasureType() throws Exception
	{
        Mockito.when(dataService.getMeasureType( "TMP" )).thenReturn( measureType );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "api/measureType/TMP")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString( "{\"code\":\"TMP\"," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void saveMeasureType() throws Exception
	{
		Mockito.when(dataService.saveMeasureType( Mockito.any(MeasureType.class) )).thenReturn( measureType );

        mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "api/measureType" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( measureType ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"code\":\"TMP\"," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void updateMeasureType() throws Exception
	{
		Mockito.when(dataService.updateMeasureType( Mockito.any(MeasureType.class) )).thenReturn( measureType );

        mockMvc.perform( MockMvcRequestBuilders.put("http://localhost:" + port + "api/measureType" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( measureType ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"code\":\"TMP\"," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void deleteMeasureType() throws Exception
	{
        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:" + port + "api/measureType/TMP")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk());
	}	

	//
	//	Batch Rest API Test Methods
	//
	@Test
	@WithMockUser( authorities = "API" )
	public void getBatch() throws Exception
	{
		testStyle.setId( 1L );
		Mockito.when(dataService.getBatch( 1L )).thenReturn( testBatch );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "api/batch/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void saveBatch() throws Exception
	{
		testStyle.setId( 1L );
		Mockito.when(dataService.saveBatch( Mockito.any( Batch.class) )).thenReturn( testBatch );

        mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "api/batch" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( testBatch ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void updateBatch() throws Exception
	{
		testStyle.setId( 1L );
		Mockito.when(dataService.updateBatch( Mockito.any( Batch.class) )).thenReturn( testBatch );

        mockMvc.perform( MockMvcRequestBuilders.put("http://localhost:" + port + "api/batch" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( testBatch ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void deleteBatch() throws Exception
	{
        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:" + port + "api/batch/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk());
	}	

	//
	//	Measurement Rest API Test Methods
	//
	@Test
	@WithMockUser( authorities = "API" )
	public void getMeasurement() throws Exception
	{
		measurement.setId( 1L );
        Mockito.when(dataService.getMeasurement( 1L )).thenReturn( measurement );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "api/measurement/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void saveMeasurement() throws Exception
	{
		measurement.setId( 1L );
		Mockito.when(dataService.saveMeasurement( Mockito.any( Measurement.class ) )).thenReturn( measurement );

        mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "api/measurement" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( measurement ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void updateMeasurement() throws Exception
	{
		measurement.setId( 1L );
		Mockito.when(dataService.updateMeasurement( Mockito.any(Measurement.class) )).thenReturn( measurement );

        mockMvc.perform( MockMvcRequestBuilders.put("http://localhost:" + port + "api/measurement" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( measurement ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void deleteMeasurement() throws Exception
	{
        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:" + port + "api/measurement/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk());
	}	


	//
	//	Sensor Rest API Test Methods
	//
	@Test
	@WithMockUser( authorities = "API" )
	public void getSensor() throws Exception
	{
		sensor.setId( 1L );
		Mockito.when(dataService.getSensor( 1L )).thenReturn( sensor );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "api/sensor/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void saveSensor() throws Exception
	{
		sensor.setId( 1L );
		Mockito.when(dataService.saveSensor( Mockito.any(Sensor.class) )).thenReturn( sensor );

        mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "api/sensor" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( sensor ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void updateSensor() throws Exception
	{
		sensor.setId( 1L );
		Mockito.when(dataService.updateSensor( Mockito.any(Sensor.class) )).thenReturn( sensor );

        mockMvc.perform( MockMvcRequestBuilders.put("http://localhost:" + port + "api/sensor" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( sensor ))		
	            .accept(MediaType.ALL))
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString( "{\"id\":1," )));
	}	
	
	@Test
	@WithMockUser( authorities = "API" )
	public void deleteSensor() throws Exception
	{
        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:" + port + "api/sensor/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk());
	}	

	//
	// Rest API Authentication Test Methods
	//
	@Test
	@WithMockUser( authorities = "API" )
	public void login() throws Exception
	{
		User user = new User( "TEST", "test", DbSync.ADD, UserRoles.ADMIN.toString() );
        Mockito.when(dataService.getUserByName( "TEST" )).thenReturn( user );

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "api/authorize?user=TEST&password=test")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString( "\"username\":\"TEST\"," )));
        
        //
        //	Test for invalid login
        //
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "api/authorize?user=TEST2&password=test2")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect( content().string(  CoreMatchers.not(containsString( "\"username\":\"TEST2\"," ) ) ))
	            ;
	            
	}	

	@Test
	@WithMockUser( authorities = "API" )
	public void notAuthenticated() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "api/notauthenticated")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString( "{error: Not Autheticated}" )))
	            ;

	}	
	
	
}
