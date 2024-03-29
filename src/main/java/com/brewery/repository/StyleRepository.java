package com.brewery.repository;

import com.brewery.model.Style;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long> {
	
	 @Query( value = "SELECT * FROM brewery.style WHERE db_synch IN ( 'ADD', 'UPDATE', 'DELETE' )" , nativeQuery = true )
	 List<Style> findStylesToSynchronize( );	

	 @Query( value = "SELECT * FROM brewery.style WHERE name = ?" , nativeQuery = true )
	 Style findStyleByName( String name );	
	 
	 @Query( value = "SELECT * FROM brewery.style WHERE db_synch_token = ?" , nativeQuery = true )
	 Style findStyleBySynchToken( String dbSynchToken );	
	 
}
