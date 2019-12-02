package com.brewery.api.controller;

import com.brewery.api.model.Style;
import com.brewery.api.model.Process;
import com.brewery.api.model.MeasureType;
import com.brewery.api.model.Batch;
import com.brewery.api.model.Measurement;
import com.brewery.api.service.BrewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/")
@Api(value = "BreweryControllerAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class BreweryController {

    private BrewService brewService;

    private Logger LOG = LoggerFactory.getLogger(BreweryController.class);

    @Autowired
    public void setProductsService(BrewService brewService) {
        this.brewService = brewService;
    }

    //
    // Style table API service methods
    //
    //    
    @RequestMapping(path = "style/{id}", method = RequestMethod.GET)
    @ApiOperation("Gets the style with specific id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Style.class)})
    public Style getStyle(@PathVariable(name = "id") Long id) {
        return brewService.getStyle(id);
    }

    @RequestMapping( path = "style", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Style saveStyle( @RequestBody Style styleToSave ) {
        return brewService.saveStyle( styleToSave );
    }

    @RequestMapping(path = "style", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Style updateStyle( @RequestBody Style styleToUpdate ) {
        return brewService.updateStyle(styleToUpdate);
    }

    @RequestMapping(path = "style/{id}", method = RequestMethod.DELETE)
    public void deleteStyle(@PathVariable(name = "id") Long id) {
    	brewService.deleteStyle(id);
    }
    
    //
    // Process table API service methods
    //
    //    
    @RequestMapping(path = "process/{code}", method = RequestMethod.GET)
    @ApiOperation("Gets the process with specific code")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Process.class)})
    public Process getProcess(@PathVariable(name = "code") String code) {
        return brewService.getProcess( code );
    }

    @RequestMapping( path = "process", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Process saveStyle( @RequestBody Process processToSave ) {
        return brewService.saveProcess( processToSave );
    }

    @RequestMapping(path = "process", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Process updateProcess( @RequestBody Process processToUpdate ) {
        return brewService.updateProcess(processToUpdate);
    }

    @RequestMapping(path = "process/{code}", method = RequestMethod.DELETE)
    public void deleteProcess(@PathVariable(name = "code") String code ) {
        LOG.info("BreweryController: Delete Process: " + code );    	
    	brewService.deleteProcess( code );
    }
    
    //
    // MeasureType table API service methods
    //
    //    
    @RequestMapping(path = "measureType/{code}", method = RequestMethod.GET)
    @ApiOperation("Gets the MeasureType with specific code")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = MeasureType.class)})
    public MeasureType getMeasureType(@PathVariable(name = "code") String code) {
        return brewService.getMeasureType( code );
    }

    @RequestMapping( path = "measureType", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public MeasureType saveMeasureType( @RequestBody MeasureType measureTypeToSave ) {
        return brewService.saveMeasureType( measureTypeToSave );
    }

    @RequestMapping(path = "measureType", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public MeasureType updateMeasureType( @RequestBody MeasureType measureTypeToUpdate ) {
        return brewService.updateMeasureType( measureTypeToUpdate );
    }

    @RequestMapping(path = "measureType/{code}", method = RequestMethod.DELETE)
    public void deleteMeasureType(@PathVariable(name = "code") String code ) {
        LOG.info("BreweryController: Delete MeasureType: " + code );    	
    	brewService.deleteMeasureType( code );
    }
    
    //
    // Batch table API service methods
    //
    //    
    @RequestMapping(path = "batch/{id}", method = RequestMethod.GET)
    @ApiOperation("Gets the batch with specific id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Batch.class)})
    public Batch getBatch(@PathVariable(name = "id") Long id) {
        return brewService.getBatch(id);
    }

    @RequestMapping( path = "batch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Batch saveBatch( @RequestBody Batch batchToSave ) {
        return brewService.saveBatch( batchToSave );
    }

    @RequestMapping(path = "batch", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Batch updateBatch( @RequestBody Batch batchToUpdate ) {
        return brewService.updateBatch( batchToUpdate );
    }

    @RequestMapping(path = "batch/{id}", method = RequestMethod.DELETE)
    public void deleteBatch(@PathVariable(name = "id") Long id) {
    	brewService.deleteBatch(id);
    }
    
    //
    // Measurement table API service methods
    //
    //    
    @RequestMapping(path = "measurement/{id}", method = RequestMethod.GET)
    @ApiOperation("Gets the measurement with specific id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Measurement.class)})
    public Measurement getMeasurement(@PathVariable(name = "id") Long id) {
        return brewService.getMeasurement(id);
    }

    @RequestMapping( path = "measurement", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Measurement saveMeasurement( @RequestBody Measurement measurementToSave ) {
        return brewService.saveMeasurement( measurementToSave );
    }

    @RequestMapping(path = "measurement", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Measurement updateMeasurement( @RequestBody Measurement measurementToUpdate ) {
        return brewService.updateMeasurement( measurementToUpdate );
    }

    @RequestMapping(path = "measurement/{id}", method = RequestMethod.DELETE)
    public void deleteMeasurement(@PathVariable(name = "id") Long id) {
    	brewService.deleteMeasurement( id );
    }
    
}
