package com.brewery.repository;

import com.brewery.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByUsername(String username);
	
	@Query( value = "SELECT * FROM brewery.user WHERE db_synch != 'SYNCHED'" , nativeQuery = true )
	List<User> findUsersToSynchronize( );	
	
}
