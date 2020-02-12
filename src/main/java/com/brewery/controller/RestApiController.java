package com.brewery.controller;

import com.brewery.model.Style;
import com.brewery.model.Process;
import com.brewery.model.MeasureType;
import com.brewery.model.Batch;
import com.brewery.model.Measurement;
import com.brewery.model.Sensor;
import com.brewery.service.DataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    //
    // Heartbeat - Used to verify service is running
    //
    @RequestMapping(value = "/heartBeat", method = RequestMethod.GET)
    public String heartBeat(){
        return "ACK";
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
        return dataService.updateStyle(styleToUpdate);
    }

    @RequestMapping(path = "style/{id}", method = RequestMethod.DELETE)
    public void deleteStyle(@PathVariable(name = "id") Long id) {
    	dataService.deleteStyle(id);
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
    public Process saveStyle( @RequestBody Process processToSave ) {
        return dataService.saveProcess( processToSave );
    }

    @RequestMapping(path = "process", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Process updateProcess( @RequestBody Process processToUpdate ) {
        return dataService.updateProcess(processToUpdate);
    }

    @RequestMapping(path = "process/{code}", method = RequestMethod.DELETE)
    public void deleteProcess(@PathVariable(name = "code") String code ) {
        LOG.info("BreweryController: Delete Process: " + code );    	
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
        LOG.info("BreweryController: Delete MeasureType: " + code );    	
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
        return dataService.updateBatch( batchToUpdate );
    }

    @RequestMapping(path = "batch/{id}", method = RequestMethod.DELETE)
    public void deleteBatch(@PathVariable(name = "id") Long id) {
    	dataService.deleteBatch(id);
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
        return dataService.saveMeasurement( measurementToSave );
    }

    @RequestMapping(path = "measurement", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Measurement updateMeasurement( @RequestBody Measurement measurementToUpdate ) {
        return dataService.updateMeasurement( measurementToUpdate );
    }

    @RequestMapping(path = "measurement/{id}", method = RequestMethod.DELETE)
    public void deleteMeasurement(@PathVariable(name = "id") Long id) {
    	dataService.deleteMeasurement( id );
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
        return dataService.updateSensor( sensorToUpdate );
    }

    @RequestMapping(path = "sensor/{id}", method = RequestMethod.DELETE)
    public void deleteSensor(@PathVariable( name = "id" ) Long id ) {
    	dataService.deleteSensor( id );
    }
    
    
}
