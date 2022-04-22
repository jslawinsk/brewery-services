package com.brewery;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import com.brewery.repository.BatchRepository;
import com.brewery.repository.MeasureTypeRepository;
import com.brewery.repository.MeasurementRepository;
import com.brewery.repository.ProcessRepository;
import com.brewery.repository.StyleRepository;
import com.brewery.repository.UserRepository;
import com.brewery.util.PasswordListener;

@RunWith(SpringRunner.class)
@SpringBootTest( properties = { "testdata.create=true", "testdata.createAdmin=true", "blueTooth.enabled=false", "wiFi.enabled=false", "dataSynch.enabled=false" } )
class BreweryApplicationTest {

	@MockBean
	StyleRepository styleRepository;
	@MockBean
	BatchRepository batchRepository;
	@MockBean
	ProcessRepository processRepository;
	@MockBean
	MeasurementRepository measurementRepository;
	@MockBean
	MeasureTypeRepository measureTypeRepository;
	@MockBean
	private UserRepository userRepository;

	@MockBean
	PasswordListener passwordListener;

	@MockBean
	JavaMailSender mailSender;

	@Ignore
	@Test
	void runTest() {
		BreweryApplication.main( new String[] {} );
		assertTrue( true);
	}
	
}
