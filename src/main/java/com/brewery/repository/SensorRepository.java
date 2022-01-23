package com.brewery.repository;

import com.brewery.model.Sensor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

	@Query( value = "SELECT * FROM brewery.sensor WHERE communication_type = ?1 AND enabled = true", nativeQuery = true )
	List<Sensor> findEnabledSensors( String type);	
	
	@Query( value = "SELECT * FROM brewery.sensor WHERE db_synch IN ('ADD', 'UPDATE', 'DELETE')", nativeQuery = true )
	List<Sensor> findSensorsToSynchronize( );	
	
	@Query( value = "SELECT * FROM brewery.sensor WHERE db_synch_token = ?" , nativeQuery = true )
	Sensor findSensorBySynchToken( String dbSynchToken );	
	
	@Query(value = "SELECT count(id) FROM brewery.sensor where process_code = ?1", nativeQuery = true)
	public Long processCount( String code);	 
	
	@Query(value = "SELECT count(id) FROM brewery.sensor where measure_type_code = ?1", nativeQuery = true)
	public Long measureTypeCount( String code);	 
	
	@Query(value = "SELECT count(id) FROM brewery.sensor where batch_id = ?1", nativeQuery = true)
	public Long batchCount( Long id );	 
	
}
