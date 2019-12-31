package com.brewery.controller;

import com.brewery.model.Style;
import com.brewery.model.Process;
import com.brewery.model.MeasureType;
import com.brewery.model.Batch;
import com.brewery.model.Measurement;
import com.brewery.model.Sensor;
import com.brewery.service.BlueToothService;
import com.brewery.service.DataService;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping( "")
public class UiController {

    private Logger LOG = LoggerFactory.getLogger( UiController.class );
	
    private DataService dataService;
    @Autowired
    public void setProductsService(DataService dataService) {
        this.dataService = dataService;
    }

    private BlueToothService blueToothService;
    @Autowired
    public void setProductsService(BlueToothService blueToothService) {
        this.blueToothService = blueToothService;
    }
    
    @Value("${blueTooth.enabled}")
    private boolean blueToothEnabled;
	
    @RequestMapping(path = "/")
    public String index( Model model ) {
        return "index";
    }

    //
    //	Style table UI routines
    //
    //
    @RequestMapping(path = "/style/add", method = RequestMethod.GET)
    public String createStyle(Model model) {
        model.addAttribute("style", new Style());
        return "styleEdit";
    }

    @RequestMapping(path = "/style", method = RequestMethod.POST)
    public String saveStyle(Style style) {
    	dataService.saveStyle(style);
        return "redirect:/style";
    }
    
    @RequestMapping(path = "/style", method = RequestMethod.GET)
    public String getAllStyles(Model model) {
        model.addAttribute("styles",  dataService.getAllStyles() );
        return "styles";
    }

    @RequestMapping(path = "/style/edit/{id}", method = RequestMethod.GET)
    public String editStyle(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("style", dataService.getStyle(id) );
        return "styleEdit";
    }

    @RequestMapping(path = "/style/delete/{id}", method = RequestMethod.GET)
    public String deleteStyle(@PathVariable(name = "id") Long id) {
    	dataService.deleteStyle(id);
        return "redirect:/style";
    }
    
    //
    //	Process table UI routines
    //
    //
    @RequestMapping(path = "/process/add", method = RequestMethod.GET)
    public String createProcess(Model model) {
        model.addAttribute("process", new Process());
        return "processAdd";
    }

    @RequestMapping(path = "/process", method = RequestMethod.POST)
    public String saveProcess(Process process) {
        LOG.info("UiController: saveProcess Process: " + process );   	
    	dataService.saveProcess(process);
        return "redirect:/process";
    }

    @RequestMapping(path = "/process", method = RequestMethod.PUT)
    public String updateProcess(Process process) {
        LOG.info("UiController: updateProcess Process: " + process );   	
    	dataService.updateProcess( process );
        return "redirect:/process";
    }
    
    
    @RequestMapping(path = "/process", method = RequestMethod.GET)
    public String getAllProcesses(Model model) {
        model.addAttribute("processes",  dataService.getAllProcesses() );
        return "processes";
    }

    @RequestMapping(path = "/process/edit/{code}", method = RequestMethod.GET)
    public String editProcess(Model model, @PathVariable(value = "code") String code ) {
        model.addAttribute("process", dataService.getProcess( code ) );
        return "processEdit";
    }

    @RequestMapping(path = "/process/delete/{code}", method = RequestMethod.GET)
    public String deleteProcess(@PathVariable(name = "code") String code) {
    	dataService.deleteProcess(code);
        return "redirect:/process";
    }

    //
    //	Measurement Type table UI routines
    //
    //
    @RequestMapping(path = "/measureType/add", method = RequestMethod.GET)
    public String createMeasureType(Model model) {
        model.addAttribute("measureType", new MeasureType() );
        return "measureTypeAdd";
    }

    @RequestMapping(path = "/measureType", method = RequestMethod.POST)
    public String saveMeasureType( MeasureType measureType ) {
        LOG.info("UiController: saveMeasureType MeasureType: " + measureType );   	
    	dataService.saveMeasureType(measureType);
        return "redirect:/measureType";
    }

    @RequestMapping(path = "/measureType", method = RequestMethod.PUT)
    public String updateMeasureType( MeasureType measureType ) {
        LOG.info("UiController: updateMeasureType MeasureType: " + measureType );   	
    	dataService.updateMeasureType( measureType );
        return "redirect:/measureType";
    }
    
    @RequestMapping(path = "/measureType", method = RequestMethod.GET)
    public String getAllMeasureTypes(Model model) {
        model.addAttribute("measureTypes",  dataService.getAllMeasureTypes() );
        return "measureTypes";
    }

    @RequestMapping(path = "/measureType/edit/{code}", method = RequestMethod.GET)
    public String editMeasureType(Model model, @PathVariable(value = "code") String code ) {
        model.addAttribute("measureType", dataService.getMeasureType( code ) );
        return "measureTypeEdit";
    }

    @RequestMapping(path = "/measureType/delete/{code}", method = RequestMethod.GET)
    public String deleteMeasureType(@PathVariable(name = "code") String code) {
    	dataService.deleteMeasureType(code);
        return "redirect:/measureType";
    }

    //
    //	Batch table UI routines
    //
    //
    @RequestMapping(path = "/batch/add", method = RequestMethod.GET)
    public String createBatch(Model model) {
    	Batch batch = new Batch();
    	batch.setId( 0L );
    	batch.setStartTime( new Date() );
        model.addAttribute("batch", batch );
        model.addAttribute("styles",  dataService.getAllStyles() );
        return "batchEdit";
    }

    @RequestMapping(path = "/batch", method = RequestMethod.POST)
    public String saveBatch( Batch batch ) {
        LOG.info("UiController: saveBatch Batch: " + batch );   
    	dataService.saveBatch(batch);
        return "redirect:/batch";
    }
    
    @RequestMapping(path = "/batch", method = RequestMethod.GET)
    public String getAllBatches(Model model) {
        model.addAttribute("batches",  dataService.getAllBatches() );
        return "batches";
    }

    @RequestMapping(path = "/batch/edit/{id}", method = RequestMethod.GET)
    public String editBatch(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("batch", dataService.getBatch(id) );
        model.addAttribute("styles",  dataService.getAllStyles() );
        return "batchEdit";
    }

    @RequestMapping(path = "/batch/delete/{id}", method = RequestMethod.GET)
    public String deleteBatch(@PathVariable(name = "id") Long id) {
    	dataService.deleteBatch( id );
        return "redirect:/batch";
    }

    //
    //	Measurement table UI routines
    //
    //
    @RequestMapping(path = "/measurement/add/{id}", method = RequestMethod.GET)
    public String createMeasurement( Model model, @PathVariable(name = "id") Long id) {
    	Batch batch = dataService.getBatch( id );
    	Measurement measurement = new Measurement();
    	measurement.setId( 0L );
    	measurement.setMeasurementTime( new Date() );
    	measurement.setBatch( batch );
        model.addAttribute("measurement", measurement );
        model.addAttribute("batches",  dataService.getAllBatches() );
        model.addAttribute("processes",  dataService.getAllProcesses() );
        model.addAttribute("measureTypes",  dataService.getAllMeasureTypes() );
        return "measurementEdit";
    }
    
    @RequestMapping(path = "/measurement", method = RequestMethod.POST)
    public String saveMeasurement( Measurement measurement ) {
        LOG.info("UiController: saveMeasurement Measurement: " + measurement );   
    	dataService.saveMeasurement( measurement );
        return "redirect:/measurement/batch/" + measurement.getBatch().getId();
    }
    
    @RequestMapping(path = "/measurement/batch/{id}", method = RequestMethod.GET)
    public String getMeasurementForBatch(Model model, @PathVariable(value = "id") long id ) {
        model.addAttribute("measurements", dataService.getMeasurementsByBatch( id ) );
        model.addAttribute("batch", dataService.getBatch(id) );
        return "measurements";
    }

    @RequestMapping(path = "/measurement/edit/{id}", method = RequestMethod.GET)
    public String editMeasurement(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("measurement", dataService.getMeasurement(id) );
        model.addAttribute("batches",  dataService.getAllBatches() );
        model.addAttribute("processes",  dataService.getAllProcesses() );
        model.addAttribute("measureTypes",  dataService.getAllMeasureTypes() );
        return "measurementEdit";
    }

    @RequestMapping(path = "/measurement/delete/{id}", method = RequestMethod.GET)
    public String deleteMeasurement(@PathVariable(name = "id") Long id) {
    	dataService.deleteMeasurement( id );
        return "redirect:/";
    }

    //
    //	Sensor table UI routines
    //
    //
    @RequestMapping(path = "/sensor/add", method = RequestMethod.GET)
    public String createSensor( Model model ) {
    	Sensor sensor = new Sensor();
    	sensor.setId( 0L );
    	sensor.setUpdateTime( new Date() );
        model.addAttribute("sensor", sensor );
        model.addAttribute("batches",  dataService.getAllBatches() );
        model.addAttribute("processes",  dataService.getAllProcesses() );
        model.addAttribute("measureTypes",  dataService.getAllMeasureTypes() );
        return "sensorEdit";
    }
    
    @RequestMapping(path = "/sensor", method = RequestMethod.POST)
    public String saveSensor( Sensor sensor ) {
        LOG.info("UiController: saveSensor: " + sensor );   
    	dataService.saveSensor( sensor );
        return "redirect:/sensor/";
    }
    
    @RequestMapping(path = "/sensor", method = RequestMethod.GET)
    public String getAllSensors( Model model ) {
        model.addAttribute("sensors", dataService.getAllSensors() );
        model.addAttribute("blueToothEnabled", blueToothEnabled );
        return "sensors";
    }

    @RequestMapping(path = "/sensor/scan", method = RequestMethod.GET)
    public String discoverSensors( Model model )  {
        try {
			model.addAttribute("sensors", blueToothService.discoverSensors() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        model.addAttribute("blueToothEnabled", blueToothEnabled );

        Sensor sensor = new Sensor();
    	sensor.setId( 0L );
    	sensor.setCommunicationType("Bluetooth");
    	sensor.setUpdateTime( new Date() );
        model.addAttribute("sensor", sensor );
        model.addAttribute("batches",  dataService.getAllBatches() );
        model.addAttribute("processes",  dataService.getAllProcesses() );
        model.addAttribute("measureTypes",  dataService.getAllMeasureTypes() );
        return "sensorSelect";
    }

    @RequestMapping(path = "/sensor/pair/{id}", method = RequestMethod.GET)
    public String pairSensor(Model model, @PathVariable(value = "id") Long id) {
        Sensor sensor = dataService.getSensor( id );
        model.addAttribute("title", sensor.getName() + " Pairing" );
        boolean result = false;
		try {
			result = blueToothService.pairSensor( sensor.getName(), sensor.getPin() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        model.addAttribute("message",  "Pair " +  (result ? "successful" : "failed") );
        return "results";
    }

    
    @RequestMapping(path = "/sensor/edit/{id}", method = RequestMethod.GET)
    public String editSensor(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("sensor", dataService.getSensor( id ) );
        model.addAttribute("batches",  dataService.getAllBatches() );
        model.addAttribute("processes",  dataService.getAllProcesses() );
        model.addAttribute("measureTypes",  dataService.getAllMeasureTypes() );
        model.addAttribute("blueToothEnabled", blueToothEnabled );
        return "sensorEdit";
    }

    @RequestMapping(path = "/sensor/delete/{id}", method = RequestMethod.GET)
    public String deleteSensor(@PathVariable(name = "id") Long id) {
    	dataService.deleteSensor( id );
        return "redirect:/sensor/";
    }
    
}
