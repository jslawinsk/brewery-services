package com.brewery.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.springframework.stereotype.Component;

@Component
public class BluetoothUtil {
	
	public StreamConnection getStreamConnection(String url) throws IOException
	{
		StreamConnection streamConnection;
		streamConnection = (StreamConnection)Connector.open( url );
		return streamConnection;
	}
	
	public BufferedReader getBufferedReader( InputStream inStream ) {
		BufferedReader bReader2=new BufferedReader( new InputStreamReader( inStream ) );
		return bReader2;
	}

}
