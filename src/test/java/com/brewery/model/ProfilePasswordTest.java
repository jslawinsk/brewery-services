package com.brewery.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProfilePasswordTest {

	@Test
	void testSetUsername() {
		ProfilePassword profilePassword = new ProfilePassword();
		profilePassword.setUsername( "Test" );
		assertEquals( "Test", profilePassword.getUsername() );
	}

}
