package com.brewery.controller;

import com.brewery.model.Style;
import com.brewery.model.Process;
import com.brewery.model.MeasureType;
import com.brewery.model.Batch;
import com.brewery.service.DataService;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	
    @RequestMapping(path = "/")
    public String index() {
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
    
}
