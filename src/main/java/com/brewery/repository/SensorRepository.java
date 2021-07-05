package com.brewery.repository;

import com.brewery.model.Sensor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

	@Query( value = "SELECT * FROM brewery.sensor WHERE communication_type = ?1 AND enabled = true", nativeQuery = true )
	List<Sensor> findEnabledSensors( String type);	
	
	@Query( value = "SELECT * FROM brewery.sensor WHERE db_synch != 'SYNCHED'", nativeQuery = true )
	List<Sensor> findSensorsToSynchronize( );	
	
}
