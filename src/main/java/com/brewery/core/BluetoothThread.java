package com.brewery.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.bluetooth.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.brewery.model.Sensor;

@Component
@Scope("prototype")
public class BluetoothThread implements Runnable {
	
	private Sensor sensor;
	
	private static final Logger LOG = LoggerFactory.getLogger( BluetoothThread.class );
   
    @Override
    public void run() {
        LOG.info("Running BluetoothThread");
    	LOG.info("Connecting to " + sensor.getUrl() );

    	StreamConnection streamConnection;
		try {
			streamConnection = (StreamConnection)Connector.open( sensor.getUrl() );

	    	Thread.sleep(500);
	    	
	    	OutputStream outStream=streamConnection.openOutputStream();
	        PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
	        pWriter.write("Brew Services\n\r");
	        pWriter.flush();
	
	        InputStream inStream=streamConnection.openInputStream();
	        while( true ) {
		        //read response
		        BufferedReader bReader2=new BufferedReader(new InputStreamReader(inStream));
		        String lineRead=bReader2.readLine();
		        LOG.info(lineRead);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public Sensor getSensor() {
		return sensor;
	}
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

    
}