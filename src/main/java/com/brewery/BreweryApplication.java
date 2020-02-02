package com.brewery;

import com.brewery.model.Style;
import com.brewery.core.BluetoothThread;
import com.brewery.core.DataSynchThread;
import com.brewery.model.Batch;
import com.brewery.model.Process;
import com.brewery.model.Measurement;
import com.brewery.model.MeasureType;
import com.brewery.repository.StyleRepository;
import com.brewery.repository.BatchRepository;
import com.brewery.repository.ProcessRepository;
import com.brewery.repository.MeasurementRepository;
import com.brewery.repository.MeasureTypeRepository;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
public class BreweryApplication implements CommandLineRunner {

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

    @Value("${blueTooth.enabled}")
    private boolean blueToothEnabled;

    @Value("${dataSynch.enabled}")
    private boolean dataSynchEnabled;
    
    @Value("${testdata.create}")
    private boolean createTestData;
    
    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(BreweryApplication.class, args);
	}

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
			
			MeasureType measureType = new MeasureType( "PH", "PH" );
			measureTypeRepository.save(measureType);
			
			measureType = new MeasureType( "TA", "Total Alcalinity" );
			measureTypeRepository.save(measureType);
			
			measureType = new MeasureType( "TMP", "Temperature" );
			measureTypeRepository.save(measureType);
	
			Batch testBatch = new Batch( true, "Joe's IPA", "Old School IPA", testStyle, new Date() );
			batchRepository.save( testBatch );
			
			Measurement measurement = new Measurement( 70.3, null, testBatch, process, measureType, new Date() );
			measurementRepository.save( measurement );
			
			measurement = new Measurement( 70.5, null, testBatch, process, measureType, new Date() );
			measurementRepository.save( measurement );
	
			Batch testBatch2 = new Batch( false, "Joe's Stout", "Old School Stout", testStyle2, new Date() );
			batchRepository.save( testBatch2 );
			
			measurement = new Measurement( 60.5, null, testBatch2, process, measureType, new Date() );
			measurementRepository.save( measurement );
		}
		if( blueToothEnabled ) {
			BluetoothThread btThread = applicationContext.getBean( BluetoothThread.class );
			taskExecutor.execute( btThread );
		}
		if( dataSynchEnabled ) {
			DataSynchThread dbSyncThread = applicationContext.getBean( DataSynchThread.class );
			taskExecutor.execute( dbSyncThread );
		}
		
	}
}
