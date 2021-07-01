package com.brewery.service;

import com.brewery.model.Style;
import com.brewery.model.User;
import com.brewery.model.Process;
import com.brewery.model.MeasureType;
import com.brewery.model.Batch;
import com.brewery.model.Measurement;
import com.brewery.model.Sensor;
import com.brewery.repository.StyleRepository;
import com.brewery.repository.UserRepository;
import com.brewery.repository.ProcessRepository;
import com.brewery.repository.MeasureTypeRepository;
import com.brewery.repository.BatchRepository;
import com.brewery.repository.MeasurementRepository;
import com.brewery.repository.SensorRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DataService implements UserDetailsService {

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

    private UserRepository userRepository;
    @Autowired
	public void userRepository( UserRepository userRepository ) {
		this.userRepository = userRepository;
	}
    
	//
	//	Style table access methods
	//
	//
    public Style getStyle( Long id ) {
        LOG.info("Getting Stype, id:" + id);
        return styleRepository.getOne(id);
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
    	Style foundStyle = styleRepository.getOne( styleToUpdate.getId() );
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
        	Style foundStyle = styleRepository.getOne( id );
        	styleRepository.delete( foundStyle );
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
        return processRepository.getOne( code );
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
    	Process foundProcess = processRepository.getOne( processToUpdate.getCode() );
        try {
        	foundProcess.setName( processToUpdate.getName() );
        	foundProcess.setVoiceAssist( processToUpdate.isVoiceAssist() );
        	foundProcess.setDbSynch( processToUpdate.getDbSynch() );
            return processRepository.save( foundProcess );
        } catch (Exception e) {
            LOG.error("DataService: Exception: updateProcess: " + e.getMessage());
        }
        return processToUpdate;
    }

    public void deleteProcess( String code ) {
        try {
        	Process foundProcess = processRepository.getOne( code );
        	processRepository.delete( foundProcess );
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
        return measureTypeRepository.getOne( code );
    }

    public List<MeasureType> getAllMeasureTypes() {
    	return measureTypeRepository.findAll();
    }

    public List<MeasureType> getMeasureTypesToSynchronize() {
    	return measureTypeRepository.findMeasureTypesToSynchronize();
    }

    public List<MeasureType> getMeasureTypesToGraph() {
    	return measureTypeRepository.findMeasureTypesToGraph();
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
    	MeasureType foundMeasureType = measureTypeRepository.getOne( measureTypeToUpdate.getCode() );
        try {
        	foundMeasureType.setName( measureTypeToUpdate.getName() );
        	foundMeasureType.setDbSynch( measureTypeToUpdate.getDbSynch() );
        	foundMeasureType.setVoiceAssist( measureTypeToUpdate.isVoiceAssist() );
        	foundMeasureType.setGraphData( measureTypeToUpdate.isGraphData() );
        	foundMeasureType.setMinValue( measureTypeToUpdate.getMinValue() );
        	foundMeasureType.setMaxValue( measureTypeToUpdate.getMaxValue() );
        	foundMeasureType.setGraphType( measureTypeToUpdate.getGraphType() );
            return measureTypeRepository.save( foundMeasureType );
        } catch (Exception e) {
            LOG.error("DataService: Exception: updateMeasureType: " + e.getMessage());
        }
        return measureTypeToUpdate;
    }

    public void deleteMeasureType( String code ) {
        try {
        	MeasureType foundMeasureType = measureTypeRepository.getOne( code );
        	measureTypeRepository.delete( foundMeasureType );
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
        return batchRepository.getOne( id );
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
            if( batch.getStyle() !=null && batch.getStyle().getName() != null ) {
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
    	Batch foundBatch = batchRepository.getOne( batchToUpdate.getId() );
        try {
        	//
        	//	Can't use primary key for as remote DB may have different value
        	//
        	if( batchToUpdate.getStyle() != null && batchToUpdate.getStyle().getName() != null ) {
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
        	Batch foundBatch = batchRepository.getOne( id );
        	measurementRepository.deleteByBatchId( id );
        	batchRepository.delete( foundBatch );
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
        return measurementRepository.getOne( id );
    }

    public List<Measurement> getRecentMeasurement( Long id ) {
        LOG.info("Getting recent Measurement" );
        return measurementRepository.findMostRecent( id );
    }
    
    public List<Measurement> getMeasurementsByBatch( Long id ) {
    	return measurementRepository.findByBatchId( id );
    }

    public List<Measurement> getMeasurementsByBatchType( Long id, String type ) {
    	return measurementRepository.findByBatchIdType( id, type );
    }
    
    public Page<Measurement> getMeasurementsPageByBatch( int pageNo, Long id ) {
    	int noOfRecords = 10;
    	PageRequest pageRequest = PageRequest.of( pageNo, noOfRecords );
        Pageable page = pageRequest;
        
        Page<Measurement> pagedResult = (Page<Measurement>) measurementRepository.findPageByBatchId(id, page);
        return pagedResult;
        // changing to List
/*        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<Measurement>();
        }
*/        
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
            if( measurement.getBatch() != null && measurement.getBatch().getName() != null ) {
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
    	LOG.info("updateMeasurement:" + measurementToUpdate );
    	Measurement foundMeasurement = measurementRepository.getOne( measurementToUpdate.getId() );
        try {
        	//
        	//	Can't use primary key for as remote DB may have different value
        	//
        	if( measurementToUpdate.getBatch() != null &&  measurementToUpdate.getBatch().getName() != null ) {
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
        	Measurement foundMeasurement = measurementRepository.getOne( id );
        	measurementRepository.delete( foundMeasurement );
        } catch (Exception e) {
            LOG.error("DataService: Exception: deleteMeasurement: " + e.getMessage());
        }
    }

    public void deleteDuplicateMeasurements( Long batchId ) {
    	List<Measurement> measurements = measurementRepository.findByBatchId( batchId );
    	
    	Measurement baseMeasurement = null;
    	for( Measurement measurement:measurements) {
    		if( baseMeasurement != null) {
    			if( baseMeasurement.getType().getCode().equals( measurement.getType().getCode() )
    					&& baseMeasurement.getProcess().getCode().equals( measurement.getProcess().getCode() )
    					&& baseMeasurement.getValueNumber() == measurement.getValueNumber()
    					&& baseMeasurement.getValueText().equals( measurement.getValueText() ) 
    			) {
    		    	LOG.info("Delete Measurement:" + measurement );
    		    	deleteMeasurement( measurement.getId() );
    			}
    		}
			baseMeasurement = measurement;    				
    	}
    	
    }
    
	//
	//	Sensor table access methods
	//
	//
    public Sensor getSensor( Long id ) {
        LOG.info("Getting Sensor, id:" + id);
        return sensorRepository.getOne(id);
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
            if( sensor.getBatch() != null && sensor.getBatch().getName() != null ) {
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
    	Sensor foundSensor = sensorRepository.getOne( sensorToUpdate.getId() );
        try {
        	//
        	//	Can't use primary key for as remote DB may have different value
        	//
        	if( sensorToUpdate.getBatch() != null && sensorToUpdate.getBatch().getName() != null ) {
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
        	Sensor foundSensor = sensorRepository.getOne( id );
        	sensorRepository.delete( foundSensor );
        } catch (Exception e) {
            LOG.error("DataService: Exception: deleteSensor: " + e.getMessage());
        }
    }
    
    //
    //	User table access methods
    //
    @Override
    public UserDetails loadUserByUsername(String username) {

    	LOG.info("loadUserByUsername: " + username );    	
    	User user = userRepository.findByUsername(username);
        
        UserBuilder builder = null;
        if (user != null) {
          builder = org.springframework.security.core.userdetails.User.withUsername(username);
          builder.password(new BCryptPasswordEncoder().encode(user.getPassword()));
          builder.roles(user.getRoles());
        } else {
          throw new UsernameNotFoundException("User not found.");
        }
        return builder.build();        
    }
    
    public User getUser( Long id ) {
        LOG.info("Getting User, id:" + id);
        return userRepository.getOne(id);
    }

    public User getUserByName(String username) {

    	LOG.info("getUserByName: " + username );    	
    	return userRepository.findByUsername(username);
    }
    
    
    public List<User> getAllUsers() {
    	return userRepository.findAll();
    }

    public List<User> getUsersToSynchronize() {
    	return userRepository.findUsersToSynchronize();
    }
    
    public User saveUser( User user ) {
    	User userToSave;
        try {
            LOG.info("Saving User...");
            userToSave = userRepository.save(user);
            return userToSave;
        } catch (Exception e) {
            LOG.error("DataService: Exception: saveUser: " + e.getMessage());
        }
        return new User();
    }

    public User updateUser( User user ) {
    	User foundUser = userRepository.getOne( user.getId() );
        try {
        	foundUser.setUsername( user.getUsername() );
        	foundUser.setPassword( user.getPassword() );
        	foundUser.setRoles( user.getRoles() );
        	foundUser.setDbSynch( user.getDbSynch() );
            return userRepository.save( foundUser );
        } catch (Exception e) {
            LOG.error("DataService: Exception: updateUser: " + e.getMessage());
        }
        return user;
    }

    public void deleteUser( Long id ) {
        try {
        	User user = userRepository.getOne( id );
        	userRepository.delete( user );
        } catch (Exception e) {
            LOG.error("DataService: Exception: deleteUser: " + e.getMessage());
        }
    }
    
}
