package com.brewery.service;

import com.brewery.model.Style;
import com.brewery.model.Process;
import com.brewery.model.MeasureType;
import com.brewery.model.Batch;
import com.brewery.model.Measurement;
import com.brewery.model.Sensor;
import com.brewery.repository.StyleRepository;
import com.brewery.repository.ProcessRepository;
import com.brewery.repository.MeasureTypeRepository;
import com.brewery.repository.BatchRepository;
import com.brewery.repository.MeasurementRepository;
import com.brewery.repository.SensorRepository;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    private Logger LOG = LoggerFactory.getLogger(DataService.class);

	private StyleRepository styleRepository;
	@Autowired
	public void styleRepository( StyleRepository styleRepository ) {
		this.styleRepository = styleRepository;
	}

	private ProcessRepository processRepository;
	@Autowired
	public void  processRepository( ProcessRepository processRepository ) {
		this.processRepository = processRepository;
	}
	
	private MeasureTypeRepository measureTypeRepository;
	@Autowired
	public void  measureTypeRepository( MeasureTypeRepository measureTypeRepository ) {
		this.measureTypeRepository = measureTypeRepository;
	}
	
	private BatchRepository batchRepository;
	@Autowired
	public void batchRepository( BatchRepository batchRepository ) {
		this.batchRepository = batchRepository;
	}

	private MeasurementRepository measurementRepository;
	@Autowired
	public void  measurementRepository( MeasurementRepository measurementRepository ) {
		this.measurementRepository = measurementRepository;
	}

	private SensorRepository sensorRepository;
	@Autowired
	public void sensorRepository( SensorRepository sensorRepository ) {
		this.sensorRepository = sensorRepository;
	}

	
	//
	//	Style table access methods
	//
	//
    public Style getStyle( Long id ) {
        LOG.info("Getting Stype, id:" + id);
        return styleRepository.findOne(id);
    }
    
    public List<Style> getAllStyles() {
    	return styleRepository.findAll();
    }

    public List<Style> getStylesToSynchronize() {
    	return styleRepository.findStylesToSynchronize();
    }
    
    public Style saveStyle( Style style ) {
    	Style styleToSave;
        try {
            LOG.info("Saving Style...");
            styleToSave = styleRepository.save(style);
            return styleToSave;
        } catch (Exception e) {
            LOG.error("DataService: Exception: saveStyle: " + e.getMessage());
        }
        return new Style();
    }

    public Style updateStyle( Style styleToUpdate ) {
    	Style foundStyle = styleRepository.findOne( styleToUpdate.getId() );
        try {
        	foundStyle.setName( styleToUpdate.getName() );
        	foundStyle.setBjcpCategory( styleToUpdate.getBjcpCategory() );
        	foundStyle.setDescription( styleToUpdate.getDescription() );
        	foundStyle.setDbSynch( styleToUpdate.getDbSynch() );
            return styleRepository.save( foundStyle );
        } catch (Exception e) {
            LOG.error("DataService: Exception: updateStyle: " + e.getMessage());
        }
        return styleToUpdate;
    }

    public void deleteStyle( Long id ) {
        try {
        	styleRepository.delete( id );
        } catch (Exception e) {
            LOG.error("DataService: Exception: deleteStyle: " + e.getMessage());
        }
    }

	//
	//	Process table access methods
	//
	//
    public Process getProcess( String code ) {
        LOG.info("Getting Process, code:" + code);
        return processRepository.findOne( code );
    }
    
    public List<Process> getAllProcesses() {
    	return processRepository.findAll();
    }

    public List<Process> getProcessesToSynchronize() {
    	return processRepository.findProcessToSynchronize();
    }
    
    public Process saveProcess( Process process ) {
    	Process processToSave;
        LOG.info("Saving Process:" + process);
        try {
            LOG.info("Saving Process: " + process );
            processToSave = processRepository.save( process );
            return processToSave;
        } catch (Exception e) {
            LOG.error("DataService: Exception: saveProcess: " + e.getMessage());
        }
        return new Process();
    }

    public Process updateProcess( Process processToUpdate ) {
        LOG.info("Update Process:" + processToUpdate);
    	Process foundProcess = processRepository.findOne( processToUpdate.getCode() );
        try {
        	foundProcess.setName( processToUpdate.getName() );
        	foundProcess.setDbSynch( processToUpdate.getDbSynch() );
            return processRepository.save( foundProcess );
        } catch (Exception e) {
            LOG.error("DataService: Exception: updateProcess: " + e.getMessage());
        }
        return processToUpdate;
    }

    public void deleteProcess( String code ) {
        try {
        	processRepository.delete( code );
        } catch (Exception e) {
            LOG.error("DataService: Exception: deleteProcess: " + e.getMessage());
        }
    }

	//
	//	MeasurementType table access methods
	//
	//
    public MeasureType getMeasureType( String code ) {
        LOG.info("Getting Process, code:" + code);
        return measureTypeRepository.findOne( code );
    }

    public List<MeasureType> getAllMeasureTypes() {
    	return measureTypeRepository.findAll();
    }

    public List<MeasureType> getMeasureTypesToSynchronize() {
    	return measureTypeRepository.findMeasureTypesToSynchronize();
    }
    
    public MeasureType saveMeasureType( MeasureType measureType ) {
    	MeasureType measureTypeToSave;
        try {
            LOG.info("Saving MeasureType: " + measureType );
            measureTypeToSave = measureTypeRepository.save( measureType );
            return measureTypeToSave;
        } catch (Exception e) {
            LOG.error("DataService: Exception: saveMeasureType: " + e.getMessage());
        }
        return new MeasureType();
    }

    public MeasureType updateMeasureType( MeasureType measureTypeToUpdate ) {
    	MeasureType foundMeasureType = measureTypeRepository.findOne( measureTypeToUpdate.getCode() );
        try {
        	foundMeasureType.setName( measureTypeToUpdate.getName() );
        	foundMeasureType.setDbSynch( measureTypeToUpdate.getDbSynch() );
            return measureTypeRepository.save( foundMeasureType );
        } catch (Exception e) {
            LOG.error("DataService: Exception: updateMeasureType: " + e.getMessage());
        }
        return measureTypeToUpdate;
    }

    public void deleteMeasureType( String code ) {
        try {
        	measureTypeRepository.delete( code );
        } catch (Exception e) {
            LOG.error("DataService: Exception: deleteMeasureType: " + e.getMessage());
        }
    }

	//
	//	Batch table access methods
	//
	//
    public Batch getBatch( Long id ) {
        LOG.info("Getting Batch, id:" + id );
        return batchRepository.findOne( id );
    }

    public List<Batch> getAllBatches() {
    	return batchRepository.findAll();
    }

    public List<Batch> getActiveBatches() {
    	return batchRepository.findActiveBatches();
    }

    public List<Batch> getBatchesToSynchronize() {
    	return batchRepository.findBatchesToSynchronize();
    }
    
    public Batch saveBatch( Batch batch ) {
    	Batch batchToSave;
        try {
            LOG.info("Saving Batch: " + batch );
        	//
        	//	Can't use primary key for as remote DB may have different value
        	//
            if( batch.getStyle().getName() != null ) {
            	Style style = styleRepository.findStyleByName( batch.getStyle().getName() );
            	batch.setStyle( style );
            }
            
            batchToSave = batchRepository.save( batch );
            return batchToSave;
        } catch (Exception e) {
            LOG.error("DataService: Exception: saveBatch: " + e.getMessage());
        }
        return new Batch();
    }

    public Batch updateBatch( Batch batchToUpdate ) {
    	Batch foundBatch = batchRepository.findOne( batchToUpdate.getId() );
        try {
        	//
        	//	Can't use primary key for as remote DB may have different value
        	//
        	if( batchToUpdate.getStyle().getName() != null ) {
        		Style style = styleRepository.findStyleByName( batchToUpdate.getStyle().getName() );
            	foundBatch.setStyle( style );
        	}
        	else {
            	foundBatch.setStyle( batchToUpdate.getStyle() );        		
        	}
        	
        	foundBatch.setActive( batchToUpdate.isActive() );
        	foundBatch.setName( batchToUpdate.getName() );
        	foundBatch.setDescription( batchToUpdate.getDescription() );
        	foundBatch.setStartTime( batchToUpdate.getStartTime() );
        	foundBatch.setDbSynch( batchToUpdate.getDbSynch() );
            return batchRepository.save( foundBatch );
        } catch (Exception e) {
            LOG.error("DataService: Exception: updateBatch: " + e.getMessage());
        }
        return batchToUpdate;
    }

    public void deleteBatch( Long id ) {
        try {
        	batchRepository.delete( id );
        } catch (Exception e) {
            LOG.error("DataService: Exception: deleteBatch: " + e.getMessage());
        }
    }

	//
	//	Measurement table access methods
	//
	//
    public Measurement getMeasurement( Long id ) {
        LOG.info("Getting Measurement, id:" + id );
        return measurementRepository.findOne( id );
    }

    public List<Measurement> getMeasurementsByBatch( Long id ) {
    	return measurementRepository.findByBatchId( id );
    }

    public List<Measurement> getMeasurementsToSynchronize( ) {
    	return measurementRepository.findMeasurementsToSynchronize();
    }
    
    public Measurement saveMeasurement( Measurement measurement ) {
    	Measurement measurementToSave;
        try {
            LOG.info("Saving Measurement: " + measurement );
        	//
        	//	Can't use primary key for as remote DB may have different value
        	//
            if(  measurement.getBatch().getName() != null ) {
            	Batch batch = batchRepository.findBatchByName( measurement.getBatch().getName() );
            	measurement.setBatch( batch );
            }
            
            measurementToSave = measurementRepository.save( measurement );
            return measurementToSave;
        } catch (Exception e) {
            LOG.error("DataService: Exception: saveMeasurement: " + e.getMessage());
        }
        return new Measurement();
    }

    public Measurement updateMeasurement( Measurement measurementToUpdate ) {
    	Measurement foundMeasurement = measurementRepository.findOne( measurementToUpdate.getId() );
        try {
        	//
        	//	Can't use primary key for as remote DB may have different value
        	//
        	if( measurementToUpdate.getBatch().getName() != null ) {
        		Batch batch = batchRepository.findBatchByName( measurementToUpdate.getBatch().getName() );
            	foundMeasurement.setBatch( batch );
        	}
        	else {
            	foundMeasurement.setBatch( measurementToUpdate.getBatch() );        		
        	}
        	
        	foundMeasurement.setValueNumber( measurementToUpdate.getValueNumber() );
        	foundMeasurement.setValueText( measurementToUpdate.getValueText() );
        	foundMeasurement.setProcess( measurementToUpdate.getProcess() );
        	foundMeasurement.setType(measurementToUpdate.getType() );
        	foundMeasurement.setMeasurementTime( measurementToUpdate.getMeasurementTime() );
        	foundMeasurement.setDbSynch( measurementToUpdate.getDbSynch() );
            return measurementRepository.save( foundMeasurement );
        } catch (Exception e) {
            LOG.error("DataService: Exception: updateMeasurement: " + e.getMessage());
        }
        return measurementToUpdate;
    }

    public void deleteMeasurement( Long id ) {
        try {
        	measurementRepository.delete( id );
        } catch (Exception e) {
            LOG.error("DataService: Exception: deleteMeasurement: " + e.getMessage());
        }
    }

	//
	//	Sensor table access methods
	//
	//
    public Sensor getSensor( Long id ) {
        LOG.info("Getting Sensor, id:" + id);
        return sensorRepository.findOne(id);
    }
    
    public List<Sensor> getAllSensors() {
    	return sensorRepository.findAll();
    }

    public List<Sensor> getEnabledSensors() {
    	return sensorRepository.findEnabledSensors();
    }

    public List<Sensor> getSensorsToSynchronize() {
    	return sensorRepository.findSensorsToSynchronize();
    }
    
    public Sensor saveSensor( Sensor sensor ) {
    	Sensor sensorToSave;
        try {
            LOG.info("Saving Sensor...");
        	//
        	//	Can't use primary key for as remote DB may have different value
        	//
            if( sensor.getBatch().getName() != null ) {
            	Batch batch = batchRepository.findBatchByName( sensor.getBatch().getName() );
                sensor.setBatch( batch );
            }
            
            sensor.setUpdateTime( new Date() );
            sensorToSave = sensorRepository.save(sensor);
            return sensorToSave;
        } catch (Exception e) {
            LOG.error("DataService: Exception: saveSensor: " + e.getMessage());
        }
        return new Sensor();
    }

    public Sensor updateSensor( Sensor sensorToUpdate ) {
    	Sensor foundSensor = sensorRepository.findOne( sensorToUpdate.getId() );
        try {
        	//
        	//	Can't use primary key for as remote DB may have different value
        	//
        	if( sensorToUpdate.getBatch().getName() != null ) {
        		Batch batch = batchRepository.findBatchByName( sensorToUpdate.getBatch().getName() );
            	foundSensor.setBatch( batch );
        	}
        	else {
            	foundSensor.setBatch( sensorToUpdate.getBatch() );
        	}
        	
        	foundSensor.setEnabled( sensorToUpdate.isEnabled() );
        	foundSensor.setName( sensorToUpdate.getName() );
        	foundSensor.setUrl( sensorToUpdate.getUrl() );
        	foundSensor.setUserId( sensorToUpdate.getUserId() );
        	foundSensor.setPin( sensorToUpdate.getPin() );
        	foundSensor.setCommunicationType( sensorToUpdate.getCommunicationType() );
        	foundSensor.setTrigger( sensorToUpdate.getTrigger() );
        	foundSensor.setProcess( sensorToUpdate.getProcess() );
        	foundSensor.setMeasureType( sensorToUpdate.getMeasureType() );
        	foundSensor.setUpdateTime( new Date() );
        	foundSensor.setDbSynch( sensorToUpdate.getDbSynch() );
            return sensorRepository.save( foundSensor );
        } catch (Exception e) {
            LOG.error("DataService: Exception: updateSensor: " + e.getMessage());
        }
        return sensorToUpdate;
    }

    public void deleteSensor( Long id ) {
        try {
        	sensorRepository.delete( id );
        } catch (Exception e) {
            LOG.error("DataService: Exception: deleteSensor: " + e.getMessage());
        }
    }
    
}
