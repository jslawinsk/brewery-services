package com.brewery.service;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import javax.bluetooth.UUID;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.RemoteDevice;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.powermock.api.mockito.PowerMockito;

import com.brewery.model.Sensor;

@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest( { BlueToothService.class, Connector.class, InputStreamReader.class, BufferedReader.class, LocalDevice.class } )
//@SpringBootTest( properties = { "blueTooth.enabled=true" } )
public class BlueToothServiceTest {

	static private Logger LOG = LoggerFactory.getLogger( BlueToothServiceTest.class );
//	static private Thread serverThread;	
//	private static final UUID uuid = new UUID(0x2108);
//	private static final String echoGreeting = "I echo";
	
	
	@Mock
	LocalDevice localDevice;
	
	@Mock
	RemoteDevice remoteDevice;
	
//	@Mock
//	DiscoveryListener discoveryListener;
	
	@Spy
	BlueToothService blueToothService = new BlueToothService( );
	
	@BeforeClass
    public static void beforeAllTestMethods() throws BluetoothStateException {
	//	EmulatorTestsHelper.startInProcessServer();
	//	EmulatorTestsHelper.useThreadLocalEmulator();
	//	serverThread = EmulatorTestsHelper.runNewEmulatorStack( new EchoServerRunnable());   
	}
 
	@AfterClass
	public static void afterAllTestMethods() throws InterruptedException {
//		if ((serverThread != null) && (serverThread.isAlive())) {
//			serverThread.interrupt();
//			serverThread.join();
//		}
//		EmulatorTestsHelper.stopInProcessServer();
	}
	
    @Before
    public void beforeEachTestMethod() {
    }
 
    @After
    public void afterEachTestMethod() {
    }
 
	@Test
	public void run() throws Exception
	{
		LOG.info( "BlueToothServiceTest: run");
		PowerMockito.mockStatic( LocalDevice.class ); 
	    PowerMockito.when( LocalDevice.getLocalDevice() ).thenReturn( localDevice );
	    Mockito.when( localDevice.getBluetoothAddress() ).thenReturn( "010101010101" );
	    Mockito.when( localDevice.getFriendlyName() ).thenReturn( "TEST_BLUTOOTH_DEVICE" );
	    
//	    PowerMockito.spy(LocalDevice.class);
//	    PowerMockito.doNothing().when(LocalDevice.class, "getLocalDevice" );
//	    PowerMockito.doReturn( "010101010101").when( LocalDevice.class, "getLocalDevice");
//	    PowerMockito.doReturn( "TEST_BLUTOOTH_DEVICE").when( LocalDevice.class, "getFriendlyName");
	    
	    Whitebox.setInternalState( blueToothService, "blueToothEnabled", true);
		
		String[] testStrings = new String[1];
		blueToothService.run( testStrings );
        assertEquals( true, true );
        verify( blueToothService, times(1)).run( testStrings );

	}	
	
	//
	// TODO: Implement unit test for discovery listener
	//
//	@Test
	public void discoverSensors() throws Exception
	{

		// PowerMockito.mockStatic( DiscoveryListener.class ); 
		// DiscoveryListener discoveryListener = Mockito.mock(DiscoveryListener.class);
		// Mockito.doNothing().when(discoveryListener).deviceDiscovered( (RemoteDevice) any( RemoteDevice.class), (DeviceClass) any(DeviceClass.class));

	    //RemoteDevice remoteDevice = Mockito.mock( RemoteDevice.class );
	    
//	    Mockito.when( remoteDevice.getBluetoothAddress() ).thenReturn( "010101010101" );
	    Mockito.when( remoteDevice.getFriendlyName( false ) ).thenReturn( "TEST_BLUTOOTH_DEVICE" );
		
		List<Sensor> sensors = blueToothService.discoverSensors();
	}
	
	@Test
	public void connect() throws Exception
	{

		PowerMockito.mockStatic( Connector.class );
		StreamConnection streamConnection = Mockito.mock( StreamConnection.class );
		OutputStream outStream = Mockito.mock( OutputStream.class );
		InputStream inStream = Mockito.mock( InputStream.class );
		BufferedReader bufferedReader = Mockito.mock( BufferedReader.class );
		
		InputStreamReader inr = Mockito.mock(InputStreamReader.class);
		
		Mockito.when( streamConnection.openInputStream( ) ).thenReturn( inStream );
		PowerMockito.whenNew(InputStreamReader.class).withArguments(inStream).thenReturn(inr);
		PowerMockito.whenNew(BufferedReader.class).withArguments(inr).thenReturn(bufferedReader);
		
		PowerMockito.when( Connector.open( "test" ) ).thenReturn( streamConnection );
		Mockito.when( streamConnection.openOutputStream( ) ).thenReturn( outStream );
				
		PowerMockito.when( bufferedReader.readLine( ) ).thenReturn( "Test data" );
		
		blueToothService.connect( "test" );
	}
	
	
    
}
