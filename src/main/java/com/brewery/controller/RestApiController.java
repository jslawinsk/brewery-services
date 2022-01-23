package com.brewery.controller;

import com.brewery.model.Style;
import com.brewery.model.User;
import com.brewery.model.Process;
import com.brewery.model.MeasureType;
import com.brewery.dto.ChartAttributes;
import com.brewery.model.Batch;
import com.brewery.model.Measurement;
import com.brewery.model.Sensor;
import com.brewery.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/")
@Api(value = "BreweryControllerAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestApiController {

    private DataService dataService;

    private Logger LOG = LoggerFactory.getLogger(RestApiController.class);

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    //
    // Heartbeat - Used to verify service is running
    //
    @RequestMapping(value = "/heartBeat", method = RequestMethod.GET)
    public String heartBeat(){
        return "ACK";
    }    

    //
    // API service methods to get current summary of measurements
    //
    //    
    @RequestMapping(path = "summary", method = RequestMethod.GET)
    @ApiOperation(value="Gets brewery summary",
    		notes="Return summarey data for all active batches. Most recent measurement is included.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = List.class )})
    public List<Measurement> getMeasurementSummary( ) {
    	List<Measurement> measurements = new ArrayList<Measurement>();
    	List<Batch> batches = dataService.getActiveBatches();
    	for( Batch batch:batches) {
    		List<Measurement> batchMeasurements = dataService.getRecentMeasurement( batch.getId() );
    		if( !batchMeasurements.isEmpty() ) {
    			measurements.addAll( batchMeasurements );
    		}
    	}    	
        LOG.info("RestApiController: getMeasurementSummary: " + measurements );    	

        return measurements;
    }

    
    //
    // Style table API service methods
    //
    //    
    @RequestMapping(path = "style/{id}", method = RequestMethod.GET)
    @ApiOperation("Gets the style with specific id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Style.class)})
    public Style getStyle(@PathVariable(name = "id") Long id) {
        return dataService.getStyle(id);
    }

    @RequestMapping( path = "style", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Style saveStyle( @RequestBody Style styleToSave ) {
        return dataService.saveStyle( styleToSave );
    }

    @RequestMapping(path = "style", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Style updateStyle( @RequestBody Style styleToUpdate ) {
    	Style style = new Style();
    	if( styleToUpdate.getDbSynchToken() != null && styleToUpdate.getDbSynchToken().length() > 0 ) {
    		Style foundStyle = dataService.getStyle( styleToUpdate.getDbSynchToken() );
    		if( foundStyle != null ) {
	           	foundStyle.setName( styleToUpdate.getName() );
	        	foundStyle.setBjcpCategory( styleToUpdate.getBjcpCategory() );
	        	foundStyle.setDescription( styleToUpdate.getDescription() );
	        	foundStyle.setDbSynch( styleToUpdate.getDbSynch() );
	    		style = dataService.updateStyle( foundStyle );
    		}
    	}
    	else {
    		style = dataService.updateStyle( styleToUpdate );
    	}
        return style;
    }

    @RequestMapping(path = "style/{id}", method = RequestMethod.DELETE)
    public void deleteStyle(@PathVariable(name = "id") Long id) {
    	dataService.deleteStyle(id);
    }

    @RequestMapping(path = "style/synchToken/{token}", method = RequestMethod.DELETE)
    public void deleteStyleRemote(@PathVariable(name = "token") String token) {
		Style foundStyle = dataService.getStyle( token );
        LOG.info("RestApiController: deleteStyleRemote: " + foundStyle );    	
        if( foundStyle != null ) {
        	dataService.deleteStyle( foundStyle.getId() );
        }
    }
    
    //
    // Process table API service methods
    //
    //    
    @RequestMapping(path = "process/{code}", method = RequestMethod.GET)
    @ApiOperation("Gets the process with specific code")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Process.class)})
    public Process getProcess(@PathVariable(name = "code") String code) {
        return dataService.getProcess( code );
    }

    @RequestMapping( path = "process", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Process saveProcess( @RequestBody Process processToSave ) {
        return dataService.saveProcess( processToSave );
    }

    @RequestMapping(path = "process", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Process updateProcess( @RequestBody Process processToUpdate ) {
        return dataService.updateProcess(processToUpdate);
    }

    @RequestMapping(path = "process/{code}", method = RequestMethod.DELETE)
    public void deleteProcess(@PathVariable(name = "code") String code ) {
        LOG.info("RestApiController: Delete Process: " + code );    	
        dataService.deleteProcess( code );
    }
    
    //
    // MeasureType table API service methods
    //
    //    
    @RequestMapping(path = "measureType/{code}", method = RequestMethod.GET)
    @ApiOperation("Gets the MeasureType with specific code")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = MeasureType.class)})
    public MeasureType getMeasureType(@PathVariable(name = "code") String code) {
        return dataService.getMeasureType( code );
    }

    @RequestMapping( path = "measureType", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public MeasureType saveMeasureType( @RequestBody MeasureType measureTypeToSave ) {
        return dataService.saveMeasureType( measureTypeToSave );
    }

    @RequestMapping(path = "measureType", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public MeasureType updateMeasureType( @RequestBody MeasureType measureTypeToUpdate ) {
        return dataService.updateMeasureType( measureTypeToUpdate );
    }

    @RequestMapping(path = "measureType/{code}", method = RequestMethod.DELETE)
    public void deleteMeasureType(@PathVariable(name = "code") String code ) {
        LOG.info("RestApiController: Delete MeasureType: " + code );    	
        dataService.deleteMeasureType( code );
    }
    
    //
    // Batch table API service methods
    //
    //    
    @RequestMapping(path = "batch/{id}", method = RequestMethod.GET)
    @ApiOperation("Gets the batch with specific id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Batch.class)})
    public Batch getBatch(@PathVariable(name = "id") Long id) {
        return dataService.getBatch(id);
    }

    @RequestMapping( path = "batch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Batch saveBatch( @RequestBody Batch batchToSave ) {
        return dataService.saveBatch( batchToSave );
    }

    @RequestMapping(path = "batch", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Batch updateBatch( @RequestBody Batch batchToUpdate ) {
    	Batch batch = new Batch();
    	
    	Style foundStyle = null;
    	if( batchToUpdate.getStyle().getDbSynchToken() != null && batchToUpdate.getStyle().getDbSynchToken().length() > 0 ) {
    		foundStyle = dataService.getStyle( batchToUpdate.getStyle().getDbSynchToken() );
    	}
    	else {
    		foundStyle = batchToUpdate.getStyle();
    	}
    	
    	if( batchToUpdate.getDbSynchToken() != null && batchToUpdate.getDbSynchToken().length() > 0 ) {
    		Batch foundBatch = dataService.getBatch( batchToUpdate.getDbSynchToken() );
    		if( foundBatch != null ) {
    			LOG.info( "updateBatch: found by token: " + foundBatch );
            	foundBatch.setActive( batchToUpdate.isActive() );
            	foundBatch.setName( batchToUpdate.getName() );
            	foundBatch.setDescription( batchToUpdate.getDescription() );
            	foundBatch.setStartTime( batchToUpdate.getStartTime() );
            	foundBatch.setDbSynch( batchToUpdate.getDbSynch() );
            	foundBatch.setStyle( foundStyle );        		
	        	batch = dataService.updateBatch( foundBatch );
    		}
    	}
    	else {
    		batchToUpdate.setStyle( foundStyle );        		
    		batch = dataService.updateBatch( batchToUpdate );
    	}
        return batch;
    }

    @RequestMapping(path = "batch/{id}", method = RequestMethod.DELETE)
    public void deleteBatch(@PathVariable(name = "id") Long id) {
    	dataService.deleteBatch(id);
    }

    @RequestMapping(path = "batch/synchToken/{token}", method = RequestMethod.DELETE)
    public void deleteBatchRemote( @PathVariable(name = "token") String token ) {
    	Batch foundBatch = dataService.getBatch( token );
        LOG.info("RestApiController: deleteBatchRemote: " + foundBatch );    	
        if( foundBatch != null ) {
        	dataService.deleteBatch( foundBatch.getId() );
        }
    }
    
    //
    // Measurement table API service methods
    //
    //    
    @RequestMapping(path = "measurement/{id}", method = RequestMethod.GET)
    @ApiOperation("Gets the measurement with specific id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Measurement.class)})
    public Measurement getMeasurement(@PathVariable(name = "id") Long id,
    			@RequestParam(value="pageNo", defaultValue="0") Integer pageNo 
    		) {
        return dataService.getMeasurement(id);
    }

    @RequestMapping( path = "measurement", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Measurement saveMeasurement( @RequestBody Measurement measurementToSave ) {
    	LOG.info("RestApiController:saveMeasurement: " + measurementToSave );
        return dataService.saveMeasurement( measurementToSave );
    }

    @RequestMapping(path = "measurement", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Measurement updateMeasurement( @RequestBody Measurement measurementToUpdate ) {
    	Measurement measurement = new Measurement();
    	
    	Batch foundBatch = null;
    	if( measurementToUpdate.getBatch().getDbSynchToken() != null && measurementToUpdate.getBatch().getDbSynchToken().length() > 0 ) {
    		foundBatch = dataService.getBatch( measurementToUpdate.getBatch().getDbSynchToken() );
    	}
    	else {
    		foundBatch = measurementToUpdate.getBatch();
    	}

    	if( measurementToUpdate.getDbSynchToken() != null && measurementToUpdate.getDbSynchToken().length() > 0 ) {
    		Measurement foundMeasurement = dataService.getMeasurement( measurementToUpdate.getDbSynchToken() );
    		if( foundMeasurement != null ) {
    			LOG.info( "updateMeasurement: found by token: " + foundMeasurement );
    			foundMeasurement.setBatch( foundBatch );
    			foundMeasurement.setDbSynch( measurementToUpdate.getDbSynch() );
    			foundMeasurement.setMeasurementTime( measurementToUpdate.getMeasurementTime() );
    			foundMeasurement.setProcess( measurementToUpdate.getProcess() );
    			foundMeasurement.setType( measurementToUpdate.getType() );
    			foundMeasurement.setValueNumber( measurementToUpdate.getValueNumber() );
    			foundMeasurement.setValueText( measurementToUpdate.getValueText() );
	        	measurement = dataService.updateMeasurement( foundMeasurement );
    		}
    	}
    	else {
    		measurementToUpdate.setBatch( foundBatch );        		
    		measurement = dataService.updateMeasurement( measurementToUpdate );
    	}
        return measurement;
    }

    @RequestMapping(path = "measurement/{id}", method = RequestMethod.DELETE)
    public void deleteMeasurement(@PathVariable(name = "id") Long id) {
    	dataService.deleteMeasurement( id );
    }

    @RequestMapping(path = "measurement/synchToken/{token}", method = RequestMethod.DELETE)
    public void deleteMeasurementRemote( @PathVariable(name = "token") String token ) {
    	Measurement foundMeasurement= dataService.getMeasurement( token );
        LOG.info("RestApiController: deleteMeasurementRemote: " + foundMeasurement );    	
        if( foundMeasurement != null ) {
        	dataService.deleteMeasurement( foundMeasurement.getId() );
        }
    }
    
    //
    // Sensor table API service methods
    //
    //    
    @RequestMapping(path = "sensor/{id}", method = RequestMethod.GET)
    @ApiOperation("Gets the sensor with specific id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Sensor.class)})
    public Sensor getSensor(@PathVariable(name = "id") Long id ) {
        return dataService.getSensor( id );
    }

    @RequestMapping( path = "sensor", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Sensor saveSensor( @RequestBody Sensor sensorToSave ) {
        return dataService.saveSensor( sensorToSave );
    }

    @RequestMapping(path = "sensor", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Sensor updateSensor( @RequestBody Sensor sensorToUpdate ) {    	
    	
    	Sensor sensor = null;
    	Batch foundBatch = null;
    	if( sensorToUpdate.getBatch().getDbSynchToken() != null && sensorToUpdate.getBatch().getDbSynchToken().length() > 0 ) {
			foundBatch = dataService.getBatch( sensorToUpdate.getBatch().getDbSynchToken() );
		}
		else {
			foundBatch = sensorToUpdate.getBatch();
		}
    	
    	if( sensorToUpdate.getDbSynchToken() != null && sensorToUpdate.getDbSynchToken().length() > 0 ) {
    		Sensor foundSensor = dataService.getSensor( sensorToUpdate.getDbSynchToken() );
    		if( foundSensor != null ) {
    			LOG.info( "updateSensor: found by token: " + foundSensor );
    			foundSensor.setBatch( foundBatch );
    			foundSensor.setCommunicationType( sensorToUpdate.getCommunicationType() );
    			foundSensor.setDbSynch( sensorToUpdate.getDbSynch() );
    			foundSensor.setEnabled( sensorToUpdate.isEnabled() );
    			foundSensor.setMeasureType( sensorToUpdate.getMeasureType() );
    			foundSensor.setName( sensorToUpdate.getName() );
    			foundSensor.setPin( sensorToUpdate.getPin() );
    			foundSensor.setProcess( sensorToUpdate.getProcess() );
    			foundSensor.setTrigger( sensorToUpdate.getTrigger() );
    			foundSensor.setUpdateTime( sensorToUpdate.getUpdateTime() );
    			foundSensor.setUrl( sensorToUpdate.getUrl() );
    			foundSensor.setUserId( sensorToUpdate.getUserId() );
        		sensor = dataService.updateSensor( foundSensor );
    		}
    	}
    	else {
    		sensorToUpdate.setBatch( foundBatch );        		
    		sensor = dataService.updateSensor( sensorToUpdate );
    	}
        return sensor;
    }

    @RequestMapping(path = "sensor/{id}", method = RequestMethod.DELETE)
    public void deleteSensor(@PathVariable( name = "id" ) Long id ) {
    	dataService.deleteSensor( id );
    }
    
    @RequestMapping(path = "sensor/synchToken/{token}", method = RequestMethod.DELETE)
    public void deleteSensorRemote(@PathVariable( name = "token" ) String token ) {
		Sensor foundSensor = dataService.getSensor( token );
        LOG.info("RestApiController: deleteSensorRemote: " + foundSensor );    	
        if( foundSensor != null ) {
        	dataService.deleteSensor( foundSensor.getId() );
        }
    }
    
    //
    // API Authentication methods
    //
    //    
	@PostMapping("authorize")
	public User login(@RequestParam("user") String username, @RequestParam("password") String pwd) {
		User foundUser = dataService.getUserByName( username );
		if( foundUser != null ) {
			if( passwordEncoder.matches( pwd, foundUser.getPassword() ) ) {
				LOG.info( "Passwords Match");
				String token = getJWTToken( foundUser );
				foundUser.setToken(token);		
				return foundUser;
			}
		}			
		return null;
	}

    @RequestMapping( path = "notauthenticated", method = RequestMethod.GET)
    public String notAuthenticated( ) {
    	return "{error: Not Autheticated}";
    }
    
	
	private String getJWTToken( User user ) {
		String secretKey = "mySecretKey";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList( user.getRoles() );
		
		String token = Jwts
				.builder()
				.setId("softtekJWT")
				.setSubject( user.getUsername() )
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SignatureAlgorithm.HS512,
						secretKey.getBytes()).compact();

		return "Bearer " + token;
	}
    
}
