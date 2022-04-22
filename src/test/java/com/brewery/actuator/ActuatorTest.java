package com.brewery.actuator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest( properties = { "blueTooth.enabled=false", "wiFi.enabled=false", "dataSynch.enabled=false" } )
class ActuatorTest {

    @Autowired
    private BluetoothStatus bluetoothStatus;

    @Autowired
    private BluetoothHealthIndicator bluetoothHealthIndicator;
    
    @Autowired
    private DataSynchStatus dataSynchStatus;
    
    @Autowired
    private DataSynchHealthIndicator dataSynchHealthIndicator;
    
    @Autowired
    private WiFiStatus wiFiStatus;

    @Autowired
    private WiFiHealthIndicator wiFiHealthIndicator;
    
	@MockBean
	JavaMailSender mailSender;
   
	@Test
	void testBluetoothHealth() {
		bluetoothStatus.setUp( false );
		
		assertFalse( bluetoothStatus.isUp() );
		assertTrue( bluetoothStatus.toString().startsWith( "BluetoothStatus [up=" ) );
		assertEquals( "DOWN {Bluetooth Service=Not Enabled}", bluetoothHealthIndicator.health().toString() );
	}

	@Test
	void testDataSynchHealth() {
		dataSynchStatus.setUp( false );
		
		assertFalse( dataSynchStatus.isUp() );
		assertTrue( dataSynchStatus.toString().startsWith( "DataSynchStatus [up=" ) );
		assertEquals( "DOWN {Data Synch Service=Not Enabled}", dataSynchHealthIndicator.health().toString() );
	}

	@Test
	void testWiFiHealth() {
		wiFiStatus.setUp( false );
		
		assertFalse( wiFiStatus.isUp() );
		assertTrue( wiFiStatus.toString().startsWith( "WiFiStatus [up=" ) );
		assertEquals( "DOWN {WiFi Service=Not Enabled}", wiFiHealthIndicator.health().toString() );
	}
	
}
