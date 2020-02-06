package com.brewery.repository;

import com.brewery.model.Process;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<Process, String> {
	
	 @Query( value = "SELECT * FROM brewery.process WHERE db_synch != 'SYNCHED'" , nativeQuery = true )
	 List<Process> findProcessToSynchronize( );	
	
}
