package com.brewery.controller;

import com.brewery.model.Style;
import com.brewery.model.User;
import com.brewery.model.Process;
import com.brewery.model.ProfilePassword;
import com.brewery.model.ResetToken;
import com.brewery.model.MeasureType;
import com.brewery.model.Batch;
import com.brewery.model.Info;
import com.brewery.model.Measurement;
import com.brewery.model.Message;
import com.brewery.model.Sensor;
import com.brewery.model.Password;
import com.brewery.dto.ChartAttributes;
import com.brewery.service.BlueToothService;
import com.brewery.service.DataService;
import com.brewery.service.UserService;
import com.brewery.service.WiFiService;
import com.brewery.util.OnCreateUserEvent;
import com.brewery.util.OnPasswordResetEvent;
import com.brewery.core.BluetoothThread;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Controller
@RequestMapping( "")
public class UiController {

    private Logger LOG = LoggerFactory.getLogger( UiController.class );
	
    private DataService dataService;
    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    private BlueToothService blueToothService;
    @Autowired
    public void setBlueToothService(BlueToothService blueToothService) {
        this.blueToothService = blueToothService;
    }

    private WiFiService wifiService;
    @Autowired
    public void setWiFiService(WiFiService wifiService) {
        this.wifiService = wifiService;
    }
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Value("${blueTooth.enabled}")
    private boolean blueToothEnabled;
	
    @Value("${wiFi.enabled}")
    private boolean wiFiEnabled;
    
    @RequestMapping(path = "/")
    public String index( Model model ) {

    	List<Measurement> measurements = new ArrayList<Measurement>();
    	
    	List<Batch> batches = dataService.getActiveBatches();
    	for( Batch batch:batches) {
    		List<Measurement> batchMeasurements = dataService.getRecentMeasurement(  batch.getId() );
    		if( !batchMeasurements.isEmpty() ) {
    			measurements.addAll( batchMeasurements );
    		}
    	}    	
        
        List<ChartAttributes> gauges = new ArrayList<ChartAttributes>();
        ObjectMapper objectMapper = new ObjectMapper();
        for( Measurement measurement:measurements) {
        	if( !measurement.getType().getGraphType().toString().equals( "NONE" ) ){
	        	double target = 60;
	        	ChartAttributes gauge = new ChartAttributes();
	
	        	boolean btarget = false;
	        	String temp = measurement.getValueText();
	        	if( temp.indexOf("target") >= 0 ) {
		        	Map<String, Double> map;
					try {
						map = objectMapper.readValue(temp, Map.class);
						target = (double)map.get( "target" );
						btarget = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
	        	
	        	gauge.setTitle( measurement.getBatch().getName() + " " + measurement.getProcess().getName() );
	        	gauge.setValueType( measurement.getType().getName() );
	        	gauge.setValueNumber( measurement.getValueNumber() );
            	gauge.setGaugeType( measurement.getType().getGraphType().toString() );
            	gauge.setMaxValue( measurement.getType().getMaxValue() );
            	gauge.setMinValue( measurement.getType().getMinValue() );
	            LOG.info("UiController: Gauge: Measurement:" + measurement.getType().getGraphType() );   	
	        	if( measurement.getType().getGraphType().toString().equals("SOLID_GUAGE") ) {
	                LOG.info("UiController: Adding Solid Gauge: " );   	
	            	gauge.setStartAngle( -90 );
	            	gauge.setEndAngle( 90 );
	            	gauge.addStop( 0,     "#FC1B2B" );	// Red
	            	gauge.addStop( 0.067, "#FD542B" );	// Pink
	            	gauge.addStop( 0.133, "#FDA529" );	// Orange
	            	gauge.addStop( 0.2,   "#FECE2F" );	// Beige
	            	gauge.addStop( 0.267, "#DBE030" );	// Yellow
	            	gauge.addStop( 0.333, "#72D628" );	// Lime Green
	            	gauge.addStop( 0.4,   "#1CB321" );	// Green
	            	gauge.addStop( 0.467, "#159A19" );	// Dark Green
	            	gauge.addStop( 0.533, "#17A45B" );	// Turquoise
	            	gauge.addStop( 0.6,   "#20BEB5" );	// Pale Blue
	            	gauge.addStop( 0.667, "#1888CE" );	// Blue
	            	gauge.addStop( 0.733, "#0F4FC5" );	// Dark Blue
	            	gauge.addStop( 0.8,   "#342BB7" );	// Violet
	            	gauge.addStop( 0.867, "#342BA5" );	// Purple
	            	gauge.addStop( 0.933, "#4A1590" );	// Deep Purple
	        	}
	        	else if( measurement.getType().getGraphType().toString().equals("GAUGE") ){
	                LOG.info("UiController: Adding Gauge: " );   	
	                if( btarget ) {
		            	gauge.addPlotBand( 0, (long)target-12, "#DF5353" );					// Red
		            	gauge.addPlotBand( (long)target-12, (long)target-6, "#e0790b" );	// Orange
		            	gauge.addPlotBand( (long)target-6, (long)target-3, "#f2f20c" );		// Yellow
		            	gauge.addPlotBand( (long)target-3, (long)target-1, "#92f20c" );		// Light Green
		            	gauge.addPlotBand( (long)target-1, (long)target+2, "#55BF3B" );		// Green
		            	gauge.addPlotBand( (long)target+2, (long)target+4, "#92f20c" );		// Light Green
		            	gauge.addPlotBand( (long)target+4, (long)target+7, "#f2f20c" );		// Yellow
		            	gauge.addPlotBand( (long)target+7, (long)target+13, "#e0790b" );	// Orange
		            	gauge.addPlotBand( (long)target+13, 200, "#DF5353" );				// Red
	                }
	        	}
            	gauges.add( gauge );
        	}
        }
        model.addAttribute("gaugeAttrs", gauges );
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
        LOG.info("UiController: Style Post: " );   	
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
    	batch.setActive( false );
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

    @RequestMapping(path = "/batch/chart/{id}", method = RequestMethod.GET)
    public String getBatchChart(Model model, @PathVariable(value = "id") long id,
    		@RequestParam("page") Optional<Integer> page     		
    		) {
    	
    	ChartAttributes chartAttributes = new ChartAttributes();
    	
    	List<MeasureType> measureTypes = dataService.getMeasureTypesToGraph();
        for( MeasureType measureType:measureTypes) {
        	ChartAttributes.SeriesInfo seriesInfo = chartAttributes.new SeriesInfo( measureType.getName() );
	    	List<Measurement> measurements = dataService.getMeasurementsByBatchType( id, measureType.getCode() );
	        for( Measurement measurement:measurements) {
	        	chartAttributes.addSeriesData( measurement.getMeasurementTime().getTime(), measurement.getValueNumber() );
	        	seriesInfo.addData( measurement.getMeasurementTime().getTime(), measurement.getValueNumber()  );
	        }    	
	        chartAttributes.addSeriesInfo( seriesInfo );
        }
        LOG.info("UiController: getBatchChart Guage: " + chartAttributes );        
        model.addAttribute("chartAttributes", chartAttributes );    	
        model.addAttribute("batch", dataService.getBatch(id) );
        return "batchChart";
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
        Measurement measurementS = dataService.saveMeasurement( measurement );
        return "redirect:/measurement/batch/" + measurementS.getBatch().getId();
    }
    
    @RequestMapping(path = "/measurement/batch/{id}", method = RequestMethod.GET)
    public String getMeasurementForBatch(Model model, @PathVariable(value = "id") long id,
    		@RequestParam("page") Optional<Integer> page     		
    		) {
    	int currentPage = page.orElse( 0 );
    	
    	List<Measurement> measurements = new ArrayList<Measurement>();
    	Page<Measurement> pagedResult = dataService.getMeasurementsPageByBatch( currentPage, id );
	    if(pagedResult.hasContent()) {
	    	measurements = (List<Measurement>) pagedResult.getContent();
	    } 
        model.addAttribute("measurements", measurements );
        model.addAttribute("batch", dataService.getBatch(id) );
        
        int totalPages = pagedResult.getTotalPages();
        List<Integer> pageNumbers = new ArrayList<Integer>();
        if (totalPages > 0) {
            pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
        }        
        model.addAttribute("totalPages", totalPages );
        model.addAttribute("pageNumbers", pageNumbers );
        model.addAttribute("currentPage", currentPage );
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

    @RequestMapping(path = "/measurement/duplicatedelete/{id}", method = RequestMethod.GET)
    public String deleteDuplicateMeasurements(@PathVariable(name = "id") Long id) {
    	dataService.deleteDuplicateMeasurements( id );
        return "redirect:/measurement/batch/" + id;
    }

    //
    //	Sensor table UI routines
    //
    //
    @RequestMapping(path = "/sensor/add", method = RequestMethod.GET)
    public String createSensor( Model model ) {
    	Sensor sensor = new Sensor();
    	sensor.setId( 0L );
    	sensor.setEnabled( false );
    	sensor.setCommunicationType("Bluetooth");
    	sensor.setTrigger( "Auto" );
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
        model.addAttribute("wiFiEnabled", blueToothEnabled );
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
        model.addAttribute("wiFiEnabled", blueToothEnabled );

        Sensor sensor = new Sensor();
    	sensor.setId( 0L );
    	sensor.setEnabled( false );
    	sensor.setCommunicationType("Bluetooth");
    	sensor.setTrigger( "Auto" );
    	sensor.setUpdateTime( new Date() );
        model.addAttribute("sensor", sensor );
        model.addAttribute("batches",  dataService.getAllBatches() );
        model.addAttribute("processes",  dataService.getAllProcesses() );
        model.addAttribute("measureTypes",  dataService.getAllMeasureTypes() );
        return "sensorSelect";
    }

    @RequestMapping(path = "/sensor/scanwifi", method = RequestMethod.GET)
    public String discoverWifiSensors( Model model )  {
        try {
			model.addAttribute("sensors", wifiService.discoverSensors() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        model.addAttribute("blueToothEnabled", blueToothEnabled );
        model.addAttribute("wiFiEnabled", blueToothEnabled );

        Sensor sensor = new Sensor();
    	sensor.setId( 0L );
    	sensor.setEnabled( false );
    	sensor.setCommunicationType("WiFi");
    	sensor.setTrigger( "Auto" );
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
        model.addAttribute("wiFiEnabled", blueToothEnabled );
        return "sensorEdit";
    }

    @RequestMapping(path = "/sensor/delete/{id}", method = RequestMethod.GET)
    public String deleteSensor(@PathVariable(name = "id") Long id) {
    	dataService.deleteSensor( id );
        return "redirect:/sensor/";
    }

    @RequestMapping(path = "/sensor/controlauto/{id}", method = RequestMethod.GET)
    public String sensorControlAuto(Model model, @PathVariable(value = "id") Long id) {
        Sensor sensor = dataService.getSensor( id );
        model.addAttribute("title", sensor.getName()  );
        Message message = new Message();
        message.setTarget( sensor.getName() );
        message.setData( "COMMAND:CONTROL:AUTO" );
        BluetoothThread.sendMessage( message );
        model.addAttribute("message",  "Command Auto Control sent "  );
        return "results";
    }

    
    @RequestMapping(path = "/sensor/controlheat/{id}", method = RequestMethod.GET)
    public String sensorControlHeat(Model model, @PathVariable(value = "id") Long id) {
        Sensor sensor = dataService.getSensor( id );
        model.addAttribute("title", sensor.getName()  );
        Message message = new Message();
        message.setTarget( sensor.getName() );
        message.setData( "COMMAND:CONTROL:HEAT_ON" );
        BluetoothThread.sendMessage( message );
        model.addAttribute("message",  "Command override Heat On sent "  );
        return "results";
    }

    @RequestMapping(path = "/sensor/controlcool/{id}", method = RequestMethod.GET)
    public String sensorControlCool(Model model, @PathVariable(value = "id") Long id) {
        Sensor sensor = dataService.getSensor( id );
        model.addAttribute("title", sensor.getName()  );
        Message message = new Message();
        message.setTarget( sensor.getName() );
        message.setData( "COMMAND:CONTROL:COOL_ON" );
        BluetoothThread.sendMessage( message );
        model.addAttribute("message",  "Command override Cool On sent "  );
        return "results";
    }
    
    //
    //	Login
    //
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        return "login";
    }
    
    @GetMapping("/validate")
    public String validateUser( Model model, @RequestParam("token") String token, HttpServletRequest request) {
        LOG.info("UiController: validateUser: " + token );   	
        Info info = new Info();
        info.setHeader( "User Validation");
        if( userService.confirmUser( token ) ) {
        	info.setMessage( "User validation successful." );
        	info.setHrefLink( "/" );
        	info.setHrefText( "Login" );
        }
        else {
        	info.setMessage( "User validation failed. Contact administator to generate a new verification token." );
        }
        model.addAttribute("info", info );
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }        
    	return "info";
    }
    
    @GetMapping("password")
    public String getPasswordReset(@ModelAttribute("password") Password password) {
        return "password";
    }
    
    @PostMapping("password")
    public String sendEmailToReset( Model model, @Valid @ModelAttribute("password") Password password, BindingResult result) {
    	User user = dataService.getUserByName( password.getUsername() );
    	if( user != null && user.getEmail().length() > 0 && user.getEmail().equals( password.getEmail() ) ){
		    String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();    
	        eventPublisher.publishEvent(new OnPasswordResetEvent(password, serverUrl, "password"));
            Info info = new Info();
            info.setHeader( "Password Reset");
        	info.setMessage( "Password reset token sent to registered email." );
            model.addAttribute("info", info );
        	return "info";
    	}
        Password passwordNew = new Password();
        model.addAttribute("password", passwordNew );
        model.addAttribute("error", true );
        return "password";
    }
    

    @GetMapping("passwordReset")
    public String getNewPassword( Model model, @RequestParam("token") String token) {
        //verify token
        Password password = new Password();
        password.setToken(token);
        model.addAttribute("password", password );

        return "passwordReset";
    }

    @PostMapping("passwordReset")
    public String saveNewPassword( Model model, @ModelAttribute("password") Password password) {
    	// verify user name
        //verify token
        Info info = new Info( "Password Reset", "Password Reset Error" );
    	info.setHrefLink( "/" );
    	info.setHrefText( "Login" );

        ResetToken resetToken = dataService.getResetToken( password.getToken() );
        dataService.deleteResetToken(  password.getToken() );
        if (resetToken.getExpiryDate().after(new Date())) {
        	if( password.getUsername().equals( resetToken.getUsername() )) {
	        	User user = dataService.getUserByName( password.getUsername() );
	    		user.setPassword( passwordEncoder.encode( password.getPassword() ) );
	        	dataService.saveUser(user);
	        	info.setMessage( "Password reset successfully." );
        	} else {
	        	info.setMessage( "Password reset failed." );        		
        	}
        } else {
        	info.setMessage( "Password reset token expired." );
        }
        model.addAttribute("info", info );
        return "info";
    }    
    
    //
    //	User table Profile UI routines
    //
    //
    @RequestMapping(path = "/profile/edit", method = RequestMethod.GET)
    public String editProfile( Model model ) {
    	
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();    	
    	
    	User user = dataService.getUserByName( userDetails.getUsername() );
        model.addAttribute("user", user );
        return "profileEdit";
    }
    
    @RequestMapping(path = "/profile", method = RequestMethod.POST)
    public String saveProfile( Model model, User user, HttpServletRequest request ) {

    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
        Info info = new Info( "Profile Update", "Profile updated." );
        
    	if( user.getUsername().equals( userDetails.getUsername() ) ) {
    		dataService.saveUser(user);
        	if( !user.isValidated() ) {
        	    String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();    
        		eventPublisher.publishEvent( new OnCreateUserEvent( user, serverUrl, "validate" ) );
                SecurityContextHolder.clearContext();
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                info.setMessage( "Profile updated. An email has been sent to validate your profile.");
        	}
    	}
        model.addAttribute("info", info );
        return "info";
    }
    
    @RequestMapping(path = "/profile/password", method = RequestMethod.GET)
    public String profilePassword( Model model ) {
    	
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();    	
    	
    	User user = dataService.getUserByName( userDetails.getUsername() );
    	ProfilePassword profilePassword = new ProfilePassword( user.getUsername() );
        model.addAttribute("profilePassword", profilePassword );
        
        Info info = new Info();
        model.addAttribute("info", info );
        
        return "profilePassword";
    }
    
    @RequestMapping(path = "/profile/password", method = RequestMethod.POST)
    public String profileUpdatePw( Model model, ProfilePassword profilePassword ) {
    	
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();    	
        Info info = new Info();

    	User user = dataService.getUserByName( userDetails.getUsername() );

    	if( passwordEncoder.matches( profilePassword.getPassword(), user.getPassword() ) ) {
			LOG.info( "Passwords Match");
    		user.setPassword( passwordEncoder.encode( profilePassword.getNewPassword() ) );
        	dataService.saveUser(user);
		}
		else {
			LOG.info( "Invalid current password");
	    	profilePassword = new ProfilePassword( user.getUsername() );
	        model.addAttribute("profilePassword", profilePassword );
	        info.setMessage( "Password change failed, please enter current password." );
	        model.addAttribute("info", info );
	        return "profilePassword";
		}
        model.addAttribute("info", info );
        return "redirect:/";
    }
    
    //
    //	User table UI routines
    //
    //
    @RequestMapping(path = "/user/add", method = RequestMethod.GET)
    public String createUser(Model model) {
        model.addAttribute("user", new User());
        return "userAdd";
    }

    @RequestMapping(path = "/user/add", method = RequestMethod.POST)
    public String saveNewUser(User user) {
    	String pw = user.getPassword();
    	if( pw != null && pw.length() > 0 ) {
    		user.setPassword( passwordEncoder.encode( user.getPassword() ) );
        	dataService.saveUser(user);
        	if( !user.isValidated() ) {
        	    String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();    
        		eventPublisher.publishEvent( new OnCreateUserEvent( user, serverUrl, "validate" ) );
        	}
    	}
        return "redirect:/user";
    }
    
    @RequestMapping(path = "/user", method = RequestMethod.POST)
    public String saveUser(User user) {
        dataService.saveUser(user);
    	if( !user.isValidated() ) {
    	    String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();    
    		eventPublisher.publishEvent( new OnCreateUserEvent( user, serverUrl, "validate" ) );
    	}
        return "redirect:/user";
    }
    
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public String getAllUsers(Model model) {
        model.addAttribute("users",  dataService.getAllUsers() );
        return "users";
    }

    @RequestMapping(path = "/user/edit/{id}", method = RequestMethod.GET)
    public String editUser(Model model, @PathVariable(value = "id") Long id) {
    	User user = dataService.getUser(id);
        model.addAttribute("user", user );
        return "userEdit";
    }

    @RequestMapping(path = "/user/password/{id}", method = RequestMethod.GET)
    public String editUserPassword(Model model, @PathVariable(value = "id") Long id) {
    	User user = dataService.getUser(id);
    	user.setPassword( "" );
        model.addAttribute("user", user );
        return "userPasswordEdit";
    }

    @RequestMapping(path = "/user/password", method = RequestMethod.POST)
    public String updateUserPw(User user) {
    	String pw = user.getPassword();
    	if( pw != null && pw.length() > 0 ) {
    		user.setPassword( passwordEncoder.encode( user.getPassword() ) );
        	dataService.saveUser(user);
    	}
        return "redirect:/user";
    }
    
    @RequestMapping(path = "/user/delete/{id}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable(name = "id") Long id) {
    	dataService.deleteUser(id);
        return "redirect:/user";
    }
    
}
