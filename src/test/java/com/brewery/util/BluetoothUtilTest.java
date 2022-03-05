package com.brewery.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.StreamConnection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class BluetoothUtilTest {

	static BluetoothUtil bluetoothUtil;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		bluetoothUtil = new BluetoothUtil();
	}

	@Mock
	StreamConnection streamConnection;

	@Mock
	InputStream inStream = Mockito.mock( InputStream.class );
	
	@Test
	public void testGetStreamConnection() throws IOException {
		try (MockedStatic<Connector> connector = Mockito.mockStatic(Connector.class)) {
			connector.when(() -> Connector.open( "test" )).thenReturn( streamConnection );
			assertThat( ((InputConnection) Connector.open( "test" )) ).isNull();

			StreamConnection streamConnection;
			streamConnection = bluetoothUtil.getStreamConnection( "test" );
			assertThat( streamConnection ).isNull();
		}
	}
	
	@Test
	void testGetBufferedReader() throws IOException {
		BufferedReader bufferedReader;
		bufferedReader = bluetoothUtil.getBufferedReader( inStream );
		assertThat( bufferedReader ).isNotNull();
	}


}
