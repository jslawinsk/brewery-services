package com.brewery.repository;

import com.brewery.model.Measurement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
	
	 @Query( value = "SELECT * FROM brewery.measurement WHERE batch_id = ?1", nativeQuery = true )
	 List<Measurement> findByBatchId( Long id );	
}
