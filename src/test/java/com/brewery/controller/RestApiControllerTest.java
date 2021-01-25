package com.brewery.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.brewery.model.Batch;
import com.brewery.model.DbSync;
import com.brewery.model.GraphTypes;
import com.brewery.model.MeasureType;
import com.brewery.model.Measurement;
import com.brewery.model.Process;
import com.brewery.model.Style;
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
	
	static private Style testStyle = new Style( "IPA", "18a", "Hoppy" );
	static private Process process = new Process( "FRM", "Fermentation" );
	static private MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
	static private Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
	static private Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
	
    @BeforeClass
    public static void beforeAllTestMethods() {
		LOG.info( "beforeAllTestMethods: " );
		testStyle.setId( 1L );
		measurement.setId( 1L );
    }
 
    @Before
    public void beforeEachTestMethod() {
		LOG.info( "beforeEachTestMethod: " );
    }
 
    @After
    public void afterEachTestMethod() {
		LOG.info( "afterEachTestMethod: " );
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

	
	
	
}
