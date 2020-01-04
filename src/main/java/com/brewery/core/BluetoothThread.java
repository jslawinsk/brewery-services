package com.brewery.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.bluetooth.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.brewery.model.Batch;
import com.brewery.model.Measurement;
import com.brewery.model.Sensor;
import com.brewery.model.SensorData;
import com.brewery.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("prototype")
public class BluetoothThread implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger( BluetoothThread.class );

    private DataService dataService;
    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
	
    @Override
    public void run() {
        LOG.info("Running BluetoothThread");
        while( true ) {
			try {
				Thread.sleep(5000);

				List<Sensor> sensors= dataService.getEnabledSensors();
				Thread.sleep(500);
				for( Sensor sensor : sensors ) {
					LOG.info( "Active Sensor: " + sensor );
					LOG.info("Connecting to " + sensor.getUrl() );

					StreamConnection streamConnection;
					streamConnection = (StreamConnection)Connector.open( sensor.getUrl() );
					Thread.sleep(500);
    	    	
					OutputStream outStream=streamConnection.openOutputStream();
					PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
					pWriter.write("Brew Services\n\r");
					pWriter.flush();
	    	        outStream.close();
    				
    		        ObjectMapper objectMapper = new ObjectMapper();
    		        
    		        InputStream inStream=streamConnection.openInputStream();
			        //read response
			        BufferedReader bReader2=new BufferedReader(new InputStreamReader(inStream));
			        String lineRead=bReader2.readLine();
			        LOG.info( "Bluetooth Data: " + lineRead);
			        SensorData sensorData = objectMapper.readValue(lineRead, SensorData.class);
			        LOG.info( "SensorData: " + sensorData );
			        Measurement measurement = new Measurement();
			        measurement.setId( 0L );
			    	measurement.setMeasurementTime( new Date() );
			    	measurement.setBatch( sensor.getBatch() );
			    	measurement.setProcess( sensor.getProcess() );
			    	measurement.setType( sensor.getMeasureType() );
			    	measurement.setValueNumber( sensorData.getTemperature() );
			    	measurement.setValueText( "{\"target\":" + sensorData.getTarget() + "}");
			        dataService.saveMeasurement( measurement );
			        inStream.close();
			        
	    	        streamConnection.close();
				}    			        
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch( Exception e ) {
				e.printStackTrace();
			}	
        }
    }
    
    
}