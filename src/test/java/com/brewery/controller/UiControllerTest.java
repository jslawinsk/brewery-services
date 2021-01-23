package com.brewery.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import com.brewery.model.User;
import com.brewery.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith( SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
		)
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
    	MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
		Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
		
		MeasureType measureType2 = new MeasureType( "PH", "PH", true, 0, 14, GraphTypes.SOLID_GUAGE, DbSync.ADD );
		Measurement measurement2 = new Measurement( 7.0, "{\"target\":7.0}", testBatch, process, measureType2, new Date() );

    	List<Measurement> measurements = new ArrayList<Measurement>();
    	measurements.add( measurement );
    	measurements.add( measurement2 );
		
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
	            .andExpect(content().string(containsString("<h2>Edit Style</h2>")));
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
	public void editStyle() throws Exception
	{
		Style testStyle = new Style( "IPA", "18a", "Hoppy" );
		Mockito.when(dataService.getStyle( 1L )).thenReturn( testStyle );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/style/edit/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Style</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteStyle() throws Exception
	{
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
	            .andExpect(content().string(containsString("<h2>Edit Process</h2>")));
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

		mockMvc.perform( MockMvcRequestBuilders.put("http://localhost:" + port + "/process" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( process ))		
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
		MeasureType measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );

		mockMvc.perform( MockMvcRequestBuilders.put("http://localhost:" + port + "/measureType" )
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString( measureType ))		
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
	            .andExpect(content().string(containsString("<h2>Edit Batch</h2>")));
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
		Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
		Mockito.when(dataService.getBatch( 1L )).thenReturn( testBatch );

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/batch/edit/1")
	            .accept(MediaType.ALL))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("<h2>Edit Batch</h2>")));
	}		
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void deleteBatch() throws Exception
	{
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
	            .andExpect(content().string(containsString("<h2>Edit Measurement</h2>")));
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
				.contentType(MediaType.APPLICATION_JSON)
		        .content( objectMapper.writeValueAsString( measurement ) )		
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
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/measurement/delete/1")
				.with(csrf())
	            .accept(MediaType.ALL))
				.andExpect( MockMvcResultMatchers.redirectedUrl("/"));
	}		
	
	
	
}
