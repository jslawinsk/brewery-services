package com.brewery.repository;

import com.brewery.model.MeasureType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasureTypeRepository extends JpaRepository<MeasureType, String> {

	 @Query( value = "SELECT * FROM brewery.measure_type WHERE db_synch != 'SYNCHED'" , nativeQuery = true )
	 List<MeasureType> findMeasureTypesToSynchronize( );	

}
