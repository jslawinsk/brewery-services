package com.brewery.repository;

import com.brewery.model.Batch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
	
	 @Query( value = "SELECT * FROM Batch WHERE active = true", nativeQuery = true )
	 List<Batch> findActiveBatches( );	
	
}
