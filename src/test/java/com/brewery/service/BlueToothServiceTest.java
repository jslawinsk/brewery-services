package com.brewery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.brewery.model.Sensor;
import com.intel.bluetooth.RemoteDeviceHelper;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

@RunWith( SpringRunner.class)
@SpringBootTest( 
				properties = { "blueTooth.enabled=true", "blueTooth.timeout=1" }
			)
class BlueToothServiceTest {

	static private Logger LOG = LoggerFactory.getLogger( BlueToothServiceTest.class );
			
	@SpyBean
	private BlueToothService blueToothService;
	
	@Mock
	LocalDevice localDevice2;
	
	@Mock
	RemoteDevice remoteDevice;

	@Mock
	RemoteDevice remoteDevice2;
	
	@Mock
	DiscoveryAgent discoveryAgent;
	
	@Mock
	ServiceRecord serviceRecord;

	@Mock
	ServiceRecord serviceRecord2;

	@Mock
	ServiceRecord serviceRecord3;
	
	@Mock
	DataElement dataElement;
	
	@Mock
	StreamConnection streamConnection;
	
	@Mock 
	OutputStream outputStream;
	
	@Mock
	InputStream inputStream;
	
	@Mock 
	InputStreamReader inputStreamReader;
	
	@Mock
	BufferedReader bufferedReader;

	@Mock
	BufferedReader bufferedReader2;
	
	@MockBean
	JavaMailSender mailSender;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testRun() throws Exception {
		LOG.info( "BlueToothServiceTest: run");

		
		try (MockedStatic<LocalDevice> localDevice = Mockito.mockStatic(LocalDevice.class)) {
			localDevice.when(() -> LocalDevice.getLocalDevice( )).thenAnswer( (Answer<LocalDevice>) invocation -> localDevice2);

		    Mockito.when( ((LocalDevice) localDevice2).getBluetoothAddress() ).thenReturn( "010101010101" );
		    Mockito.when( ((LocalDevice) localDevice2).getFriendlyName() ).thenReturn( "TEST_BLUTOOTH_DEVICE" );
			
			String[] testStrings = new String[1];
			blueToothService.run( testStrings );
	        verify( localDevice2, times(1)).getBluetoothAddress(  );
		}

		try (MockedStatic<LocalDevice> localDevice = Mockito.mockStatic(LocalDevice.class)) {
			localDevice.when(() -> LocalDevice.getLocalDevice( )).thenThrow( new BluetoothStateException());

			String[] testStrings = new String[1];
			blueToothService.run( testStrings );
	        verify( localDevice2, times(1)).getBluetoothAddress(  );
		}
	}

	@Test
	void testDiscoverSensors() throws Exception {
		
	    Mockito.when( remoteDevice.getFriendlyName( false ) ).thenReturn( "TEST_BLUTOOTH_DEVICE" );

		try (MockedStatic<LocalDevice> localDevice = Mockito.mockStatic(LocalDevice.class)) {
			localDevice.when(() -> LocalDevice.getLocalDevice( )).thenAnswer( (Answer<LocalDevice>) invocation -> localDevice2);
			
		    Mockito.when( discoveryAgent.startInquiry(Mockito.anyInt(), Mockito.any( DiscoveryListener.class )) ).thenReturn( true );
		    Mockito.when( ((LocalDevice) localDevice2).getDiscoveryAgent() ).thenReturn( discoveryAgent );
		    Mockito.when( ((LocalDevice) localDevice2).getBluetoothAddress() ).thenReturn( "010101010101" );
		    Mockito.when( ((LocalDevice) localDevice2).getFriendlyName() ).thenReturn( "TEST_BLUTOOTH_DEVICE" );
		    
		    List<RemoteDevice> remoteDevices = new ArrayList<RemoteDevice>();
		    remoteDevices.add( remoteDevice2 );
		    remoteDevices.add( remoteDevice );
		    Mockito.doReturn( remoteDevices ).when( blueToothService ).getRemoteDevices();
		    
		    List<Sensor> sensors = blueToothService.discoverSensors();
		    assertNotNull( sensors );
		}
	}

	@Test
	void testDeviceListener() throws Exception {
	    
	    DiscoveryListener deviceListener = blueToothService.getDeviceListener();
	    deviceListener.deviceDiscovered( remoteDevice, new DeviceClass(0 ));
        verify( remoteDevice, times(1)).getBluetoothAddress(  );
        
		Mockito.when( remoteDevice.getFriendlyName( false )).thenThrow( new IOException( "test") );
	    deviceListener.deviceDiscovered( remoteDevice, new DeviceClass(0 ));
	    deviceListener.inquiryCompleted( DiscoveryListener.INQUIRY_COMPLETED );
	    
	    ServiceRecord[] servRecord = new ServiceRecord[ 1 ];
	    deviceListener.servicesDiscovered( 0, servRecord);
	    deviceListener.serviceSearchCompleted( 0, 0 );
        verify( remoteDevice, times(2)).getBluetoothAddress(  );
	}

	@Test
	void testServiceListener() throws Exception {
	    
		Mockito.when( dataElement.getValue() ).thenReturn( "TestDataElement" );
	    Mockito.when( remoteDevice.getFriendlyName( false ) ).thenReturn( "TEST_BLUTOOTH_DEVICE" );
		Mockito.when( remoteDevice2.getFriendlyName( false )).thenThrow( new IOException( "test") );
	    
		Mockito.when( serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false) ).thenReturn( "testURL" );
		Mockito.when( serviceRecord.getAttributeValue(0x0100) ).thenReturn( dataElement );
		Mockito.when( serviceRecord.getHostDevice() ).thenReturn( remoteDevice );

		Mockito.when( serviceRecord2.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false) ).thenReturn( null );

		Mockito.when( serviceRecord3.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false) ).thenReturn( "testURL" );
		Mockito.when( serviceRecord3.getAttributeValue(0x0100) ).thenReturn( null );
		Mockito.when( serviceRecord3.getHostDevice() ).thenReturn( remoteDevice2 );
		
	    ServiceRecord[] servRecords = new ServiceRecord[ 3 ];
	    servRecords[ 0 ] = serviceRecord2;
	    servRecords[ 1 ] = serviceRecord3;
	    servRecords[ 2 ] = serviceRecord;
	    
	    DiscoveryListener serviceListener = blueToothService.getServiceListener();
	    serviceListener.servicesDiscovered( 0, servRecords);
	    serviceListener.inquiryCompleted( 0 );
	    serviceListener.deviceDiscovered( remoteDevice, new DeviceClass(0 ));
	    serviceListener.serviceSearchCompleted( 0, DiscoveryListener.SERVICE_SEARCH_COMPLETED );
	    serviceListener.serviceSearchCompleted( 0, DiscoveryListener.SERVICE_SEARCH_TERMINATED );
	    serviceListener.serviceSearchCompleted( 0, DiscoveryListener.SERVICE_SEARCH_ERROR );
	    serviceListener.serviceSearchCompleted( 0, DiscoveryListener.SERVICE_SEARCH_NO_RECORDS );
	    serviceListener.serviceSearchCompleted( 0, DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE );
	    serviceListener.serviceSearchCompleted( 0, DiscoveryListener.INQUIRY_COMPLETED );
        verify( remoteDevice, times(2)).getFriendlyName( false );
	}
	
	
	@Test
	void testPairSensor() throws Exception {

	    Mockito.when( remoteDevice.getFriendlyName( false ) ).thenReturn( "TEST_BLUTOOTH_DEVICE" );
	    Mockito.when( remoteDevice2.getFriendlyName( false ) ).thenReturn( "TEST" );

		try (MockedStatic<LocalDevice> localDevice = Mockito.mockStatic(LocalDevice.class)) {
			localDevice.when(() -> LocalDevice.getLocalDevice( )).thenAnswer( (Answer<LocalDevice>) invocation -> localDevice2);
			
		    Mockito.when( discoveryAgent.startInquiry(Mockito.anyInt(), Mockito.any( DiscoveryListener.class )) ).thenReturn( true );
		    Mockito.when( ((LocalDevice) localDevice2).getDiscoveryAgent() ).thenReturn( discoveryAgent );
		    Mockito.when( ((LocalDevice) localDevice2).getBluetoothAddress() ).thenReturn( "010101010101" );
		    Mockito.when( ((LocalDevice) localDevice2).getFriendlyName() ).thenReturn( "TEST_BLUTOOTH_DEVICE" );

		    List<RemoteDevice> remoteDevices = new ArrayList<RemoteDevice>();
		    remoteDevices.add( remoteDevice2 );
		    remoteDevices.add( remoteDevice );
		    Mockito.doReturn( remoteDevices ).when( blueToothService ).getRemoteDevices();
			
			try (MockedStatic<RemoteDeviceHelper> remoteDeviceHelper = Mockito.mockStatic(RemoteDeviceHelper.class)) {
				remoteDeviceHelper.when(() -> RemoteDeviceHelper.authenticate( remoteDevice, "1234" )).thenReturn( true );
				boolean result = blueToothService.pairSensor( "TEST_BLUTOOTH_DEVICE", "1234" );
				assertTrue( result );

				remoteDeviceHelper.when(() -> RemoteDeviceHelper.authenticate( remoteDevice, "1234" )).thenReturn( false );
				result = blueToothService.pairSensor( "TEST_BLUTOOTH_DEVICE", "1234" );
				assertFalse( result );
			}
		}
	}

	@Test
	void testGetRemoteDevices() {
		List<RemoteDevice> remoteDevices = blueToothService.getRemoteDevices();
		assertNotNull( remoteDevices );
	}
	
	@Test
	void testConnect() throws Exception {
		try (MockedStatic<Connector> connector = Mockito.mockStatic(Connector.class)) {
			connector.when(() -> Connector.open( "test" ) ).thenReturn( streamConnection );
			
			Mockito.when( streamConnection.openOutputStream( ) ).thenReturn( outputStream );
			Mockito.when( streamConnection.openInputStream( ) ).thenReturn( inputStream );
			
			try( MockedConstruction<BufferedReader> bufferedReader = Mockito.mockConstruction(BufferedReader.class)) {
				BufferedReader bufferedReader2 = new BufferedReader( new InputStreamReader(inputStream) );
				Mockito.when( bufferedReader2.readLine( ) ).thenReturn( "Test data" );
				blueToothService.connect( "test" ); 
			}			
		}
	}

}
