package com.brewery.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.brewery.model.Password;

class OnPasswordResetEventTest {

	static OnPasswordResetEvent onPasswordResetEvent;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Password password = new Password();
		password.setToken( "test123" );
		onPasswordResetEvent = new OnPasswordResetEvent( password, "localhost:8080", "/test" );
	}

	@Test
	@Order( 1 )
	void testGetAppPath() {
		assertEquals( "/test", onPasswordResetEvent.getAppPath() );
	}

	@Test
	@Order( 2 )
	void testSetAppPath() {
		onPasswordResetEvent.setAppPath( "/test2" );
		assertEquals( "/test2", onPasswordResetEvent.getAppPath() );
	}

	@Test
	@Order( 3 )
	void testSetPassword() {
		Password password = new Password();
		password.setToken( "test456" );
		onPasswordResetEvent.setPassword( password );
		assertEquals( "test456", onPasswordResetEvent.getPassword().getToken() );
	}

	@Test
	@Order( 4 )
	void testSetServerUrl() {
		onPasswordResetEvent.setServerUrl( "localhost:8084" );
		assertEquals( "localhost:8084", onPasswordResetEvent.getServerUrl() );
	}
}
