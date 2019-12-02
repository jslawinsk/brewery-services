package com.brewery.api.service;

import com.brewery.api.model.Style;
import com.brewery.api.model.Process;
import com.brewery.api.model.MeasureType;
import com.brewery.api.model.Batch;
import com.brewery.api.model.Measurement;
import com.brewery.api.repository.StyleRepository;
import com.brewery.api.repository.ProcessRepository;
import com.brewery.api.repository.MeasureTypeRepository;
import com.brewery.api.repository.BatchRepository;
import com.brewery.api.repository.MeasurementRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrewService {

    private Logger LOG = LoggerFactory.getLogger(BrewService.class);

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
	
	//
	//	Style table access methods
	//
	//
    public Style getStyle( Long id ) {
        LOG.info("Getting Stype, id:" + id);
        return styleRepository.findOne(id);
    }

    public Style saveStyle( Style style ) {
    	Style styleToSave;
        try {
            LOG.info("Saving Style...");
            styleToSave = styleRepository.save(style);
            return styleToSave;
        } catch (Exception e) {
            LOG.error("BrewService: Exception: saveStyle: " + e.getMessage());
        }
        return new Style();
    }

    public Style updateStyle( Style styleToUpdate ) {
    	Style foundStyle = styleRepository.findOne( styleToUpdate.getId() );
        try {
        	foundStyle.setName( styleToUpdate.getName() );
        	foundStyle.setBjcpCategory( styleToUpdate.getBjcpCategory() );
        	foundStyle.setDescription( styleToUpdate.getDescription() );
            return styleRepository.save( foundStyle );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: updateStyle: " + e.getMessage());
        }
        return styleToUpdate;
    }

    public void deleteStyle( Long id ) {
        try {
        	styleRepository.delete( id );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: deleteStyle: " + e.getMessage());
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

    public Process saveProcess( Process process ) {
    	Process processToSave;
        try {
            LOG.info("Saving Process: " + process );
            processToSave = processRepository.save( process );
            return processToSave;
        } catch (Exception e) {
            LOG.error("BrewService: Exception: saveProcess: " + e.getMessage());
        }
        return new Process();
    }

    public Process updateProcess( Process processToUpdate ) {
    	Process foundProcess = processRepository.findOne( processToUpdate.getCode() );
        try {
        	foundProcess.setName( processToUpdate.getName() );
            return processRepository.save( foundProcess );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: updateProcess: " + e.getMessage());
        }
        return processToUpdate;
    }

    public void deleteProcess( String code ) {
        try {
        	processRepository.delete( code );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: deleteProcess: " + e.getMessage());
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

    public MeasureType saveMeasureType( MeasureType measureType ) {
    	MeasureType measureTypeToSave;
        try {
            LOG.info("Saving MeasureType: " + measureType );
            measureTypeToSave = measureTypeRepository.save( measureType );
            return measureTypeToSave;
        } catch (Exception e) {
            LOG.error("BrewService: Exception: saveMeasureType: " + e.getMessage());
        }
        return new MeasureType();
    }

    public MeasureType updateMeasureType( MeasureType measureTypeToUpdate ) {
    	MeasureType foundMeasureType = measureTypeRepository.findOne( measureTypeToUpdate.getCode() );
        try {
        	foundMeasureType.setName( measureTypeToUpdate.getName() );
            return measureTypeRepository.save( foundMeasureType );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: updateMeasureType: " + e.getMessage());
        }
        return measureTypeToUpdate;
    }

    public void deleteMeasureType( String code ) {
        try {
        	measureTypeRepository.delete( code );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: deleteMeasureType: " + e.getMessage());
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

    public Batch saveBatch( Batch batch ) {
    	Batch batchToSave;
        try {
            LOG.info("Saving Batch: " + batch );
            batchToSave = batchRepository.save( batch );
            return batchToSave;
        } catch (Exception e) {
            LOG.error("BrewService: Exception: saveBatch: " + e.getMessage());
        }
        return new Batch();
    }

    public Batch updateBatch( Batch batchToUpdate ) {
    	Batch foundBatch = batchRepository.findOne( batchToUpdate.getId() );
        try {
        	foundBatch.setName( batchToUpdate.getName() );
        	foundBatch.setDescription( batchToUpdate.getDescription() );
        	foundBatch.setStyle( batchToUpdate.getStyle() );
        	foundBatch.setStartTime( batchToUpdate.getStartTime() );
            return batchRepository.save( foundBatch );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: updateBatch: " + e.getMessage());
        }
        return batchToUpdate;
    }

    public void deleteBatch( Long id ) {
        try {
        	batchRepository.delete( id );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: deleteBatch: " + e.getMessage());
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

    public Measurement saveMeasurement( Measurement measurement ) {
    	Measurement measurementToSave;
        try {
            LOG.info("Saving Measurement: " + measurement );
            measurementToSave = measurementRepository.save( measurement );
            return measurementToSave;
        } catch (Exception e) {
            LOG.error("BrewService: Exception: saveMeasurement: " + e.getMessage());
        }
        return new Measurement();
    }

    public Measurement updateMeasurement( Measurement measurementToUpdate ) {
    	Measurement foundMeasurement = measurementRepository.findOne( measurementToUpdate.getId() );
        try {
        	foundMeasurement.setMeasurement( measurementToUpdate.getMeasurement() );
        	foundMeasurement.setObservation( measurementToUpdate.getObservation() );
        	foundMeasurement.setBatch( measurementToUpdate.getBatch() );
        	foundMeasurement.setProcess( measurementToUpdate.getProcess() );
        	foundMeasurement.setType(measurementToUpdate.getType() );
        	foundMeasurement.setMeasurementTime( measurementToUpdate.getMeasurementTime() );
            return measurementRepository.save( foundMeasurement );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: updateMeasurement: " + e.getMessage());
        }
        return measurementToUpdate;
    }

    public void deleteMeasurement( Long id ) {
        try {
        	measurementRepository.delete( id );
        } catch (Exception e) {
            LOG.error("BrewService: Exception: deleteMeasurement: " + e.getMessage());
        }
    }
    
}
