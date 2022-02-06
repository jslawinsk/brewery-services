package com.brewery.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.properties.PropertyMapping;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.brewery.model.Batch;
import com.brewery.model.DbSync;
import com.brewery.model.GraphTypes;
import com.brewery.model.MeasureType;
import com.brewery.model.Measurement;
import com.brewery.model.Password;
import com.brewery.model.Process;
import com.brewery.model.ProfilePassword;
import com.brewery.model.ResetToken;
import com.brewery.model.Sensor;
import com.brewery.model.Style;
import com.brewery.model.User;
import com.brewery.model.VerificationToken;
import com.brewery.service.BlueToothService;
import com.brewery.service.DataService;
import com.brewery.service.WiFiService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith( SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
				properties = { "blueTooth.enabled=false", 
								"wiFi.enabled=false",
								"dataSynch.enabled=true" }
			)
//@TestPropertySource("classpath:resources/appliaction-local.properties")
@AutoConfigureMockMvc
public class UiControllerTest {

    private Logger LOG = LoggerFactory.getLogger( UiControllerTest.class );
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;	
	
	@MockBean
	DataService dataService;
	
    @Autowired
    private PasswordEncoder passwordEncoder;
	
	@MockBean
	BlueToothService blueToothService;

	@MockBean
    WiFiService wifiService;
	
	@MockBean
	JavaMailSender mailSender;

	//
	//	Index Dash board tests 
	//
	@Test
	@WithMockUser(roles = "ADMIN")
	public void getIndex() throws Exception
	{
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
		testBatch.setId( 1L );
    	List<Batch> batches = new ArrayList<Batch>();
    	batches.add( testBatch );
    	
    	Process process = new Process( "FRM", "Fermentation" );
    	MeasureType measureType = new MeasureType( "TMP", "Temperature" );
    	measureType.setGraphType( GraphTypes.GAUGE );
		Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
		
		MeasureType measureType2 = new MeasureType( "PH", "PH", DbSync.ADD );
		measureType2.setGraphType( GraphTypes.SOLID_GUAGE );
		Measurement measurement2 = new Measurement( 7.0, "{\"target\":7.0}", testBatch, process, measureType2, new Date() );

		Measurement measurement3 = new Measurement( 70.3, "{\"target\":}", testBatch, process, measureType, new Date() );
		
    	List<Measurement> measurements = new ArrayList<Measurement>();
    	measurements.add( measurement );
    	measurements.add( measurement2 );
    	measurements.add( measurement3 );
		
        Mockito.when(dataService.getActiveBatches()).thenReturn( batches );
        Mockito.when(dataService.getRecentMeasurement( 1L )).thenReturn( measurements );
		
	    mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Dashboard</h2>")));
	}	
	
	//
	//	Style Methods tests 
	//
	@Test
	@WithMockUser(roles = "ADMIN")
	public void getAllStyles() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/style")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Styles</h2>")));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void getStyleCreate() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/style/add")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Add Style</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void saveStyle() throws Exception
	{
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/style" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( testStyle ))		
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/style"));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void updateStyle() throws Exception
	{
		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/style/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	                "DbSynch", "SYNCHED"
	                )
	            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/style"));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void editStyle() throws Exception
	{
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Mockito.when(dataService.getStyle( 1L )).thenReturn( testStyle );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/style/edit/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Style:")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteStyle() throws Exception
	{
		Style testStyle = new Style( "IPA", "18a", "Hoppy", DbSync.SYNCHED, "TestToken" );
		Mockito.when(dataService.getStyle( 1L )).thenReturn( testStyle );
		Mockito.when(dataService.getStyleBatchCount( 1L )).thenReturn( 0L );

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/style/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/style"));

        testStyle.setDbSynchToken( "" );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/style/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/style"));

		Mockito.when(dataService.getStyleBatchCount( 1L )).thenReturn( 1L );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/style/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/style"));

		Mockito.when(dataService.getStyleBatchCount( 1L )).thenReturn( 10L );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/style/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/style"));
	}		
	
	//
	//	Process Methods tests 
	//
	@Test
	@WithMockUser(roles = "ADMIN")
	public void createProcess() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/process/add")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Add Process</h2>")));
	}			
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void saveProcess() throws Exception
	{
		Process process = new Process( "FRM", "Fermentation" );

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/process" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( process ))		
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/process"));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void updateProcess() throws Exception
	{
		Process process = new Process( "FRM", "Fermentation" );

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/process/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	                "DbSynch", "SYNCHED"
	                )
	            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/process"));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void getAllProcesses() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/process")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Processes</h2>")));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void editProcess() throws Exception
	{
		Process process = new Process( "FRM", "Fermentation" );
		Mockito.when(dataService.getProcess( "FRM" )).thenReturn( process );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/process/edit/FRM")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Process</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteProcess() throws Exception
	{
		Process process = new Process( "FRM", "Fermentation" );
		Mockito.when(dataService.getProcess( "FRM" )).thenReturn( process );
		Mockito.when(dataService.getProcessSensorCount( "FRM" )).thenReturn( 0L );
		Mockito.when(dataService.getProcessMeasurementCount( "FRM" )).thenReturn( 0L );
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/process/delete/FRM")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/process"));

		Mockito.when(dataService.getProcessSensorCount( "FRM" )).thenReturn( 1L );
		Mockito.when(dataService.getProcessMeasurementCount( "FRM" )).thenReturn( 1L );
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/process/delete/FRM")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/process"));

		Mockito.when(dataService.getProcessSensorCount( "FRM" )).thenReturn( 10L );
		Mockito.when(dataService.getProcessMeasurementCount( "FRM" )).thenReturn( 10L );
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/process/delete/FRM")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/process"));
	}		
	
	
	//
	//	Measurement Methods tests 
	//
	@Test
	@WithMockUser(roles = "ADMIN")
	public void createMeasureType() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measureType/add")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Measure Type</h2>")));
	}			
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void saveMeasureType() throws Exception
	{
		MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/measureType" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( measureType ))		
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measureType"));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void updateMeasureType() throws Exception
	{
		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/measureType/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED )
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "ADD"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measureType"));

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/measureType/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED )
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "SYNCHED"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measureType"));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void getAllMeasureTypes() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measureType")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Measure Types</h2>")));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void editMeasureType() throws Exception
	{
		MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
		Mockito.when(dataService.getMeasureType( "TMP" )).thenReturn( measureType );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measureType/edit/TMP")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Measure Type</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteMeasureType() throws Exception
	{
		MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
		Mockito.when(dataService.getMeasureType( "TMP" )).thenReturn( measureType );
		Mockito.when(dataService.getMeasureTypeSensorCount( "TMP" )).thenReturn( 0L );
		Mockito.when(dataService.getMeasureTypeMeasurementCount( "TMP" )).thenReturn( 0L );
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measureType/delete/TMP")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measureType"));

		Mockito.when(dataService.getMeasureTypeSensorCount( "TMP" )).thenReturn( 1L );
		Mockito.when(dataService.getMeasureTypeMeasurementCount( "TMP" )).thenReturn( 1L );
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measureType/delete/TMP")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measureType"));

		Mockito.when(dataService.getMeasureTypeSensorCount( "TMP" )).thenReturn( 10L );
		Mockito.when(dataService.getMeasureTypeMeasurementCount( "TMP" )).thenReturn( 10L );
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measureType/delete/TMP")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measureType"));
	}		
	
	//
	//	Batch Methods tests 
	//
	@Test
	@WithMockUser(roles = "ADMIN")
	public void createBatch() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch/add")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Add Batch</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void saveBatch() throws Exception
	{
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/batch" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( testBatch ))		
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/batch"));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void updateBatch() throws Exception
	{
		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/batch/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED )
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "SYNCHED"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/batch"));
		
		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/batch/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "ADD"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/batch"));
	}	
	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void getAllBatches() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Batches</h2>")));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void getBatchChart() throws Exception
	{
    	MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
    	List<MeasureType> measureTypes = new ArrayList<MeasureType>();
    	measureTypes.add( measureType );
	
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
		
    	Process process = new Process( "FRM", "Fermentation" );
		Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
    	List<Measurement> measurements = new ArrayList<Measurement>();
    	measurements.add( measurement );

		Mockito.when(dataService.getMeasureTypesToGraph( )).thenReturn( measureTypes );
        Mockito.when(dataService.getMeasurementsByBatchType( 1L, "TMP" )).thenReturn( measurements );
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch/chart/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("Highcharts.chart")));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void editBatch() throws Exception
	{
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date(), DbSync.SYNCHED, "testToken" );
		Mockito.when(dataService.getBatch( 1L )).thenReturn( testBatch );
		Mockito.when(dataService.getBatchSensorCount( 1L )).thenReturn( 0L );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch/edit/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Batch</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteBatch() throws Exception
	{
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date(), DbSync.SYNCHED, "TestToken" );
		Mockito.when(dataService.getBatch( 1L )).thenReturn( testBatch );
		Mockito.when(dataService.getBatchSensorCount( 1L )).thenReturn( 0L );

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/batch"));
		
		testBatch.setDbSynchToken( "" );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/batch"));

		testBatch.setDbSynchToken( null );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/batch"));

		Mockito.when(dataService.getBatchSensorCount( 1L )).thenReturn( 1L );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/batch"));

		Mockito.when(dataService.getBatchSensorCount( 1L )).thenReturn( 10L );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/batch"));
	}		
	
	//
	//	Measurement Methods tests 
	//
	@Test
	@WithMockUser(roles = "ADMIN")
	public void createMeasurement() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measurement/add/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Add Measurement</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void saveMeasurement() throws Exception
	{
    	MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 20, 200, GraphTypes.GAUGE, DbSync.ADD  );
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
		testBatch.setId( 1L );
    	Process process = new Process( "FRM", "Fermentation" );
		Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
		measurement.setId( 1L );
		LOG.info( "Test saveMeasurement Measurement: " + measurement );
		Mockito.when(dataService.saveMeasurement( Mockito.any(Measurement.class) )).thenReturn( measurement );

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/measurement" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED )
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "SYNCHED",
		                "batch.id", "1"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measurement/batch/1"))
				;
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void updateMeasurement() throws Exception
	{
    	MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 20, 200, GraphTypes.GAUGE, DbSync.ADD  );
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
		testBatch.setId( 1L );
    	Process process = new Process( "FRM", "Fermentation" );
		Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
		measurement.setId( 1L );
		LOG.info( "Test updateMeasurement Measurement: " + measurement );
		Mockito.when(dataService.updateMeasurement( Mockito.any(Measurement.class) )).thenReturn( measurement );

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/measurement/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED )
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "SYNCHED",
		                "batch.id", "1"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measurement/batch/1"))
				;

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/measurement/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED )
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "ADD",
		                "batch.id", "1"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measurement/batch/1"))
				;
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void getMeasurementForBatch() throws Exception
	{
		
    	MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
    	Process process = new Process( "FRM", "Fermentation" );
		Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
    	List<Measurement> measurements = new ArrayList<Measurement>();
    	measurements.add( measurement );
		
    	List<Measurement> measurementData = new ArrayList<>();
    	measurementData.add(measurement);		
		Page<Measurement> page = new PageImpl<>(measurementData);		
		Mockito.when(dataService.getMeasurementsPageByBatch( 0, 1L )).thenReturn( page );
		
		Mockito.when(dataService.getBatch( 1L )).thenReturn( testBatch );
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measurement/batch/1" )
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<title>Measurements</title>")))
		;
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void editMeasurement() throws Exception
	{
    	MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
		testBatch.setId( 1L );
    	Process process = new Process( "FRM", "Fermentation" );
		Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
		measurement.setId( 1L );
		
        Mockito.when(dataService.getMeasurement( 1L )).thenReturn( measurement );
		
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measurement/edit/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Measurement</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteMeasurement() throws Exception
	{
    	MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
		testBatch.setId( 1L );
    	Process process = new Process( "FRM", "Fermentation" );
		Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
		measurement.setId( 1L );
		measurement.setDbSynchToken( "TestToken" );
        Mockito.when(dataService.getMeasurement( 1L )).thenReturn( measurement );
		
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measurement/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
        		.andExpect( MockMvcResultMatchers.redirectedUrl("/measurement/batch/1"));

		measurement.setDbSynchToken( "" );
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measurement/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
        		.andExpect( MockMvcResultMatchers.redirectedUrl("/measurement/batch/1"));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteDuplicateMeasurements() throws Exception
	{
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measurement/duplicatedelete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/measurement/batch/1"));
	}		
	
	
	//
	//	Sensor Method tests 
	//
	@Test
	@WithMockUser(roles = "ADMIN")
	public void createSensor() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/add")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Add Sensor</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void saveSensor() throws Exception
	{
		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/sensor" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED )
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "ADD"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/sensor/"));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void updateSensor() throws Exception
	{
		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/sensor/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED )
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "ADD"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/sensor/"));

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/sensor/update" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED )
	            .content(buildUrlEncodedFormEntity(
		                "DbSynch", "SYNCHED"
		                )
		            )		        
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/sensor/"));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void getAllSensors() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Sensors</h2>")));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void discoverSensors() throws Exception
	{
		Sensor sensor = new Sensor();
    	List<Sensor> sensors = new ArrayList<>();
    	sensors.add( sensor );			
		Mockito.when( blueToothService.discoverSensors( )).thenReturn( sensors );
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/scan")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Sensor</h2>")));

		Mockito.when( blueToothService.discoverSensors( )).thenThrow( new IOException( "test") );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/scan")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Sensor</h2>")));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void discoverWifiSensors() throws Exception
	{
		
		Sensor sensor = new Sensor();
    	List<Sensor> sensors = new ArrayList<>();
    	sensors.add( sensor );			
		Mockito.when( wifiService.discoverSensors( "" )).thenReturn( sensors );
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/scanwifi")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Sensor</h2>")));

		Mockito.when( wifiService.discoverSensors( "" )).thenThrow( new SocketException( "test") );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/scanwifi")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Sensor</h2>")));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void pairSensor() throws Exception
	{
		Sensor sensor = new Sensor();
		sensor.setName( "test" );
		sensor.setPin( "1234" );
		sensor.setId( 1L );
    	List<Sensor> sensors = new ArrayList<>();
    	sensors.add( sensor );			
		Mockito.when( blueToothService.pairSensor( sensor.getName(), sensor.getPin() )).thenReturn( true );
		Mockito.when(dataService.getSensor( 1L )).thenReturn( sensor );
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/pair/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<title>Error</title>")));

		Mockito.when( blueToothService.pairSensor( sensor.getName(), sensor.getPin() )).thenThrow( new IOException( "test") );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/pair/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<title>Error</title>")));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void editSensor() throws Exception
	{
		Sensor sensor = new Sensor();
		Mockito.when(dataService.getSensor( 1L )).thenReturn( sensor );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/edit/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Sensor</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteSensor() throws Exception
	{
		Sensor sensor = new Sensor();
		sensor.setDbSynchToken( "TestToken" );
		Mockito.when(dataService.getSensor( 1L )).thenReturn( sensor );

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/sensor/"));

		sensor.setDbSynchToken( "" );
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/sensor/"));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void sensorControlAuto() throws Exception
	{
		Sensor sensor = new Sensor();
		sensor.setName( "test" );
		sensor.setPin( "1234" );
		sensor.setId( 1L );
		Mockito.when(dataService.getSensor( 1L )).thenReturn( sensor );
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/controlauto/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<title>Error</title>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void sensorControlHeat() throws Exception
	{
		Sensor sensor = new Sensor();
		sensor.setName( "test" );
		sensor.setPin( "1234" );
		sensor.setId( 1L );
		Mockito.when(dataService.getSensor( 1L )).thenReturn( sensor );
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/controlheat/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<title>Error</title>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void sensorControlCool() throws Exception
	{
		Sensor sensor = new Sensor();
		sensor.setName( "test" );
		sensor.setPin( "1234" );
		sensor.setId( 1L );
		Mockito.when(dataService.getSensor( 1L )).thenReturn( sensor );
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/sensor/controlcool/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<title>Error</title>")));
	}	

	//
	//	User Validation and Password Reset Methods tests 
	//	
	@Test
	public void validateUser() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		LOG.info( "Test validateUser: " + user );

		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken( "test123" );
        verificationToken.setUsername( user.getUsername() );
        verificationToken.setExpiryDate(  verificationToken.calculateExpiryDate( 20 ) );

		Mockito.when(dataService.getUserByName( Mockito.any(String.class)) ).thenReturn( user );
		Mockito.when( dataService.getVerificationToken( "test123" ) ).thenReturn( verificationToken );
		Mockito.when( dataService.getVerificationToken( "test456" ) ).thenReturn( null );

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/validate?token=test123")
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( user ))		
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>User Validation</h2>")));

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/validate?token=test456")
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( user ))		
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>User Validation</h2>")));
	}		

	@Test
	public void getPasswordReset() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/password")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Reset Password</h2>")));
	}		
	
	@Test
	public void sendEmailToReset() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		user.setEmail( "test@gmail.com");
		LOG.info( "Test sendEmailToReset: " + user );

		Mockito.when(dataService.getUserByName( Mockito.any(String.class)) ).thenReturn( user );

		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/password")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	                "username", "test",
	                "email", "test@gmail.com"
	                )
	            )		        
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Password Reset</h2>")));

		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/password")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	                "username", "test",
	                "email", "invalid@gmail.com"
	                )
	            )		        
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Reset Password</h2>")));
	}		

	@Test
	public void getNewPassword() throws Exception
	{
        Password password = new Password();
        password.setToken( "test123" );

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/passwordReset?token=test123")
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( password ))		
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Reset Password</h2>")));
	}		

	@Test
	public void saveNewPassword() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		user.setEmail( "test@gmail.com");
		LOG.info( "Test sendEmailToReset: " + user );
		Mockito.when(dataService.getUserByName( "test" ) ).thenReturn( user );

		ResetToken resetToken = new ResetToken();
		resetToken.setUsername( "test" );
		resetToken.setToken( "test123" );
        resetToken.setExpiryDate( resetToken.calculateExpiryDate( 20 ) );
		Mockito.when(dataService.getResetToken( "test123" ) ).thenReturn( resetToken );
		
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/passwordReset")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	            	"token", "test123",
	                "username", "test",
	                "password", "test"
	                )
	            )		        
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("Password reset successfully.")));

		resetToken.setUsername( "invalid" );
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/passwordReset")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	            	"token", "test123",
	                "username", "test",
	                "password", "test"
	                )
	            )		        
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("Password reset failed.")));

        resetToken.setExpiryDate( resetToken.calculateExpiryDate( -20 ) );
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/passwordReset")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	            	"token", "test123",
	                "username", "test",
	                "password", "test"
	                )
	            )		        
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("Password reset token expired.")));
	}		
	
	//
	//	User Profile Methods tests 
	//	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void editProfile() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		LOG.info( "Test saveNewUser: " + user );

		Mockito.when(dataService.getUserByName( Mockito.any(String.class)) ).thenReturn( user );

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/profile/edit")
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( user ))		
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit User</h2>")));
	}		

	@Test
	@WithMockUser(roles = "ADMIN", username = "test")
	public void saveProfile() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/profile")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	                "username", "test" 
	                )
	            )		        
	            .accept(MediaType.ALL) )
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("<h2>Profile Update</h2>")));
//				.andExpect( status().isTemporaryRedirect() );
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN", username = "test")
	public void profilePassword() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		user.setPassword( passwordEncoder.encode( "test" ));
		LOG.info( "Test saveNewUser: " + user );

		Mockito.when(dataService.getUserByName( Mockito.any(String.class)) ).thenReturn( user );

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/profile/password")
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( user ))		
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Change Password for: test</h2>")));
	}		

	@Test
	@WithMockUser(roles = "ADMIN", username = "test")
	public void profileUpdatePw() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		user.setPassword( passwordEncoder.encode( "test" ));
		Mockito.when(dataService.getUserByName( "test" )).thenReturn( user );
		
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/profile/password")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	                "password", "test",
	                "newPassword", "testNew"
	                )
	            )		        
	            .accept(MediaType.ALL) )
				.andExpect( MockMvcResultMatchers.redirectedUrl("/")
			);

		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/profile/password")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	                "password", "invalidPassword",
	                "newPassword", "testNew"
	                )
	            )		        
	            .accept(MediaType.ALL) )
		        .andExpect(status().isOk())
		        .andExpect(content().string(containsString("<h2>Change Password for: test</h2>"))
			);
	}		
	

	
	//
	//	User Methods tests 
	//
	@Test
	public void login() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/login")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Login</h2>")));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void createUser() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/user/add")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit User</h2>")));
	}		

	@Test
	@WithMockUser(roles = "ADMIN")
	public void saveNewUser() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		LOG.info( "Test saveNewUser: " + user );
		
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/user/add")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	                "username", user.getUsername(), 
	                "password", user.getPassword()
	                )
	            )
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/user"))
				;
		
	}		
	
	private String buildUrlEncodedFormEntity(String... params) {
	    if( (params.length % 2) > 0 ) {
	       throw new IllegalArgumentException("Need to give an even number of parameters");
	    }
	    StringBuilder result = new StringBuilder();
	    for (int i=0; i<params.length; i+=2) {
	        if( i > 0 ) {
	            result.append('&');
	        }
	        try {
	            result.
	            append(URLEncoder.encode(params[i], StandardCharsets.UTF_8.name())).
	            append('=').
	            append(URLEncoder.encode(params[i+1], StandardCharsets.UTF_8.name()));
	        }
	        catch (UnsupportedEncodingException e) {
	            throw new RuntimeException(e);
	        }
	    }
	    return result.toString();
	 }	
	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void saveUser() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		LOG.info( "Test saveUser: " + user );

		mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/user" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( user ))		
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/user"));
	}	
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void getAllUsers() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/user")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Users</h2>")));
	}	

	@Test
	@WithMockUser(roles = "ADMIN")
	public void editUser() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		Mockito.when(dataService.getUser( 1L )).thenReturn( user );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/user/edit/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit User</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void editUserPassword() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		Mockito.when(dataService.getUser( 1L )).thenReturn( user );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/user/password/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Change Password for:")));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void updateUserPw() throws Exception
	{
		User user = new User( "test", "test", DbSync.ADD, "TEST" );
		user.setId( 1L );
		LOG.info( "Test saveNewUser: " + user );
		
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/user/password")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(buildUrlEncodedFormEntity(
	                "username", user.getUsername(), 
	                "password", user.getPassword()
	                )
	            )
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/user"))
				;
		
	}			
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteUser() throws Exception
	{
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/user/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/user"));
	}		
	
	
	
}
