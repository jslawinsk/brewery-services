package com.brewery.repository;

import com.brewery.model.Measurement;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
	
	 @Query( value = "SELECT * FROM brewery.measurement WHERE batch_id = ?1 ORDER BY process_code, type_code, start_time ASC", nativeQuery = true )
	 List<Measurement> findByBatchId( Long id );	

	 @Query( value = "SELECT * FROM brewery.measurement WHERE batch_id = ?1 ORDER BY start_time DESC", nativeQuery = true )
	 Page<Measurement> findPageByBatchId( Long id, Pageable pageable );	
	 
	 @Query( value = "SELECT * FROM brewery.measurement WHERE db_synch NOT IN ( 'SYNCHED', 'IGNORE' ) ORDER BY batch_id, process_code, type_code, start_time ASC", nativeQuery = true )
	 List<Measurement> findMeasurementsToSynchronize( );
	 
	 @Query( value = "SELECT DISTINCT ON (type_code) type_code, start_time, id, db_synch, value_number, value_text, batch_id, process_code FROM brewery.measurement WHERE batch_id = ?1 ORDER BY type_code, start_time DESC LIMIT 10", nativeQuery = true )
	 List<Measurement> findMostRecent( Long id );

	 @Query( value = "SELECT type_code, start_time, id, db_synch, value_number, value_text, batch_id, process_code FROM brewery.measurement WHERE batch_id = ?1 AND type_code = ?2 ORDER BY type_code, start_time", nativeQuery = true )
	 List<Measurement> findByBatchIdType( Long id, String type );
}
