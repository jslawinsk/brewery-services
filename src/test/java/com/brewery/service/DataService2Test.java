package com.brewery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import com.brewery.model.DbSync;
import com.brewery.model.Style;
import com.brewery.repository.StyleRepository;

@RunWith( SpringRunner.class)
@SpringBootTest( 
				properties = { "blueTooth.enabled=false", 
								"wiFi.enabled=false",
								"dataSynch.enabled=false" }
			)
class DataService2Test {

	@MockBean
	StyleRepository styleRepository;

	@MockBean
	JavaMailSender mailSender;

	@MockBean
	BlueToothService blueToothService;
	
	@Autowired
	DataService dataService;

	private Style testStyle = new Style( "IPA", "18a", "Hoppy", DbSync.SYNCHED, "TestToken" );
	
	@Test
	void testSaveStyle() throws Exception {
        
        Mockito.when(styleRepository.save( testStyle )).thenReturn( testStyle );
        testStyle.setDbSynchToken( "" );
        Style style = dataService.saveStyle( testStyle );
        assertEquals( style.getName(), "IPA");
        testStyle.setDbSynchToken( "TestToken" );

        assertEquals( 1, 1 );
        
   	        
		try (MockedStatic<InetAddress> inetAddress = Mockito.mockStatic(InetAddress.class)) {
			inetAddress.when(() -> InetAddress.getLocalHost() ).thenThrow( new UnknownHostException("test"));

	        testStyle.setDbSynchToken( "" );

/*
 * 	Sample of catching an exception, which my class does not throw.
 * 	        
	        Assertions.assertThrows( java.net.UnknownHostException.class, () -> {
	    		dataService.saveStyle( testStyle );
	    	}, "UnknownHostException was expected");
	        testStyle.setDbSynchToken( "TestToken" );
*/
    		style = dataService.saveStyle( testStyle );
    		
    		assertThat( style.getDbSynchToken() ).doesNotStartWith( "test" );

		}
        
        
        
	}

}
