package com.brewery;

import com.brewery.model.Style;
import com.brewery.model.User;
import com.brewery.model.UserRoles;
import com.brewery.actuator.BluetoothStatus;
import com.brewery.actuator.DataSynchStatus;
import com.brewery.actuator.WiFiStatus;
import com.brewery.core.BluetoothThread;
import com.brewery.core.DataSynchThread;
import com.brewery.core.WiFiThread;
import com.brewery.model.Batch;
import com.brewery.model.DbSync;
import com.brewery.model.GraphTypes;
import com.brewery.model.Process;
import com.brewery.model.Measurement;
import com.brewery.model.MeasureType;
import com.brewery.repository.StyleRepository;
import com.brewery.repository.UserRepository;
import com.brewery.repository.BatchRepository;
import com.brewery.repository.ProcessRepository;
import com.brewery.repository.MeasurementRepository;
import com.brewery.repository.MeasureTypeRepository;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BreweryApplication implements CommandLineRunner {

    static private Logger LOG = LoggerFactory.getLogger( BreweryApplication.class );

    private StyleRepository styleRepository;
	@Autowired
	public void styleRepository( StyleRepository styleRepository ) {
		this.styleRepository = styleRepository;
	}

	
	private BatchRepository batchRepository;
	@Autowired
	public void batchRepository( BatchRepository batchRepository ) {
		this.batchRepository = batchRepository;
	}

	private ProcessRepository processRepository;
	@Autowired
	public void  processRepository( ProcessRepository processRepository ) {
		this.processRepository = processRepository;
	}
	
	private MeasurementRepository measurementRepository;
	@Autowired
	public void  measurementRepository( MeasurementRepository measurementRepository ) {
		this.measurementRepository = measurementRepository;
	}
	
	private MeasureTypeRepository measureTypeRepository;
	@Autowired
	public void  measureTypeRepository( MeasureTypeRepository measureTypeRepository ) {
		this.measureTypeRepository = measureTypeRepository;
	}

	private UserRepository userRepository;
    @Autowired
	public void userRepository( UserRepository userRepository ) {
		this.userRepository = userRepository;
	}
	    	
    @Value("${blueTooth.enabled}")
    private boolean blueToothEnabled;

    @Value("${wiFi.enabled}")
    private boolean wiFiEnabled;
    
    @Value("${dataSynch.enabled}")
    private boolean dataSynchEnabled;
    
    @Value("${testdata.create}")
    private boolean createTestData;

    @Value("${testdata.createAdmin}")
    private boolean createTestAdmin;
    
    @Autowired
    private TaskExecutor taskExecutor;
    
    @Autowired
    private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(BreweryApplication.class, args);
	}

    @Autowired
    private BluetoothStatus bluetoothStatus;

    @Autowired
    private WiFiStatus wiFiStatus;

    @Autowired
    private DataSynchStatus dataSynchStatus;
    
    @Autowired
    private JavaMailSender mailSender;

    @Override
	public void run(String... strings) throws Exception {
		
		// Populate test database
		//
		if( createTestData ) {
			Style testStyle = new Style( "IPA", "18a", "Hoppy" );
			styleRepository.save( testStyle );
	
			Style testStyle2 = new Style( "Stout", "21", "Malty" );
			styleRepository.save( testStyle2 );
			
			Process process = new Process( "FRM", "Fermentation" );
			processRepository.save( process );
			
			process = new Process( "MSH", "Mash" );
			processRepository.save( process );		
			
			MeasureType measureType = new MeasureType( "PH", "PH", true, 0, 14, GraphTypes.SOLID_GUAGE, DbSync.ADD );
			measureTypeRepository.save(measureType);
			
			measureType = new MeasureType( "TA", "Total Alcalinity" );
			measureTypeRepository.save(measureType);
			
			measureType = new MeasureType( "TMP", "Temperature", true, 0, 200, GraphTypes.GAUGE, DbSync.ADD  );
			measureTypeRepository.save(measureType);
	
			Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
			batchRepository.save( testBatch );
			
			Measurement measurement = new Measurement( 70.3, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
			measurementRepository.save( measurement );
			
			measurement = new Measurement( 70.5, "{\"target\":70.0}", testBatch, process, measureType, new Date() );
			measurementRepository.save( measurement );
	
			Batch testBatch2 = new Batch( false, "Joe's Stout", "Old School Stout", testStyle2, new Date() );
			batchRepository.save( testBatch2 );
			
			measurement = new Measurement( 60.5, "{\"target\":70.0}", testBatch2, process, measureType, new Date() );
			measurementRepository.save( measurement );
			
			User user = new User( "ADMIN", "admin@breweryservices.com", new BCryptPasswordEncoder().encode( "admin" ), DbSync.ADD, UserRoles.ADMIN.toString(), true );
			userRepository.save( user );
		}
		if ( createTestAdmin ) {
			User user = new User( "ADMIN", "admin@breweryservices.com", new BCryptPasswordEncoder().encode( "admin" ), DbSync.ADD, UserRoles.ADMIN.toString(), true );
			userRepository.save( user );
		}

		bluetoothStatus.setUp(true);	
		if( blueToothEnabled ) {
			LOG.info("Bluetooth Initializing" );
			BluetoothThread btThread = applicationContext.getBean( BluetoothThread.class );
			bluetoothStatus.setMessage( "Initializing" );
			taskExecutor.execute( btThread );
		}
		else {
			LOG.info("Bluetooth Not Enabled" );
			bluetoothStatus.setMessage( "Not Enabled" );
		}

		wiFiStatus.setUp(true);	
		if( wiFiEnabled ) {
			LOG.info("WiFi Initializing" );
			WiFiThread wfThread = applicationContext.getBean( WiFiThread.class );
			wiFiStatus.setMessage( "Initializing" );
			taskExecutor.execute( wfThread );
		}
		else {
			LOG.info("WiFi Not Enabled" );
			wiFiStatus.setMessage( "Not Enabled" );
		}
		
		dataSynchStatus.setUp( true );
		if( dataSynchEnabled ) {
			LOG.info("Data Synch Enabled" );
			DataSynchThread dbSyncThread = applicationContext.getBean( DataSynchThread.class );
			dataSynchStatus.setMessage( "Enabled" );
			taskExecutor.execute( dbSyncThread );
		}
		else {
			LOG.info("Data Synch Not Enabled" );
			dataSynchStatus.setMessage( "Not Enabled" );
		}
		
	}
}
