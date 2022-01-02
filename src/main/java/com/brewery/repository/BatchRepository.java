package com.brewery.repository;

import com.brewery.model.Batch;
import com.brewery.model.Style;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
	
	 @Query( value = "SELECT * FROM brewery.batch WHERE active = true", nativeQuery = true )
	 List<Batch> findActiveBatches( );	
	
	 @Query( value = "SELECT * FROM brewery.batch WHERE db_synch!= 'SYNCHED'", nativeQuery = true )
	 List<Batch> findBatchesToSynchronize( );	

	 @Query( value = "SELECT * FROM brewery.batch WHERE name = ?", nativeQuery = true )
	 Batch findBatchByName( String name );	
	 
	 @Query( value = "SELECT * FROM brewery.batch WHERE db_synch_token = ?" , nativeQuery = true )
	 Batch findBatchBySynchToken( String dbSynchToken );	
	 
	@Query(value = "SELECT count(id) FROM brewery.batch where style_id = ?1", nativeQuery = true)
	public Long styleCount( Long id );	 

	
}
