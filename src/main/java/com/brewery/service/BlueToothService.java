package com.brewery.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.intel.bluetooth.RemoteDeviceHelper;
import com.brewery.core.BluetoothThread;
import com.brewery.model.Sensor;


/**
* Class that implements an SPP Server which accepts single line of
* message from an SPP client and sends a single line of response to the client.
*/
@Service
public class BlueToothService implements CommandLineRunner {
    //private static LocalDevice localDevice;
    static LocalDevice localDevice;
    DiscoveryAgent agent;

    private Logger LOG = LoggerFactory.getLogger( BlueToothService.class );
    
    @Value("${blueTooth.enabled}")
    private boolean blueToothEnabled;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;
    
    
    @Override
    public void run(String... strings) throws Exception {
        //display local device address and name
        LOG.info("Bluetooth Discovery: run: " + blueToothEnabled );   	
        if( blueToothEnabled ) {
	        try{
	            localDevice = LocalDevice.getLocalDevice();
	            LOG.info( "Address: "+localDevice.getBluetoothAddress() );
	            LOG.info( "Name: "+localDevice.getFriendlyName() );

	        }catch(Exception e){
	            System.err.println(e.toString());
	            System.err.println(e.getStackTrace());
	            e.printStackTrace();
	        }
        }       
    }
    
    public List<Sensor> discoverSensors(  ) throws IOException, InterruptedException 
    {
        List<RemoteDevice> remoteDevices = discoverDevices();
        for( RemoteDevice remoteDevice : remoteDevices ) {
        	LOG.info( "Device Discovered: " + remoteDevice.getFriendlyName(false) );
        }
        List<Sensor> sensors = discoverServices( remoteDevices );
        return sensors;
    }

    public boolean pairSensor( String deviceName, String pin  ) throws IOException, InterruptedException 
    {
    	boolean paired = false; 
    	LOG.info( "Pairing Sensors: " +  deviceName );
        List<RemoteDevice> remoteDevices = discoverDevices();
        for( RemoteDevice remoteDevice : remoteDevices ) {
        	String remoteName = remoteDevice.getFriendlyName(false);
        	LOG.info( "Device Discovered: " +  remoteName );
        	if( deviceName.equals( remoteName ) ) {
                paired = RemoteDeviceHelper.authenticate( remoteDevice, pin );
                LOG.info("Pair with " + remoteName + (paired ? " successful" : " failed"));   	
                break;
        	}
        }
        return paired;
    }
    
    /**
     * Device Discovery
     */
    private List<RemoteDevice> discoverDevices(  ) throws IOException, InterruptedException 
    {
    	final Object inquiryCompletedEvent = new Object();

    	List<RemoteDevice> remoteDevices = new ArrayList<RemoteDevice>();
        
    	DiscoveryListener listener = new DiscoveryListener() {

    		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
    			LOG.info("Device " + btDevice.getBluetoothAddress() + " found");    			
				remoteDevices.add( btDevice );
    			try {
    				LOG.info("     name " + btDevice.getFriendlyName(false));
                } catch (IOException e ) {
					e.printStackTrace();
                }
    		}

    		public void inquiryCompleted(int discType) {
    			LOG.info("Device Inquiry completed!");
    			synchronized(inquiryCompletedEvent){
    				inquiryCompletedEvent.notifyAll();
    			}
    		}

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            }
            public void serviceSearchCompleted(int transID, int respCode) {
            }   		
    	};
    	
    	synchronized(inquiryCompletedEvent) {
    		boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
    		if (started) {
    			LOG.info("wait for device inquiry to complete...");
    			inquiryCompletedEvent.wait();
    			LOG.info( remoteDevices.size() +  " device(s) found");
    		}
    	}   	
    	return remoteDevices;
    }    

    /**
    *
    * Minimal Services Search example.
    */
    private List<Sensor> discoverServices( List<RemoteDevice> remoteDevices ) throws IOException, InterruptedException 
    {
    	final UUID UUID_SDP = new UUID(0x0001);
    	final UUID UUID_RFCOMM = new UUID(0x0003);
    	final UUID UUID_OBEX = new UUID(0x0008);
    	final UUID UUID_HTTP = new UUID(0x000C);
    	final UUID UUID_L2CAP = new UUID(0x0100);
    	final UUID UUID_BNEP = new UUID(0x000F);
    	final UUID UUID_SERIAL = new UUID(0x1101);
    	final UUID UUID_SERVICE_DISCOVERY_SERVER = new UUID(0x1000);
    	final UUID UUID_BROWSE_GROUP = new UUID(0x1001);
    	final UUID UUID_PUBLIC_BROWSE_GROUP = new UUID(0x1002);
    	final UUID UUID_OBEX_OBJECT_PUSH = new UUID(0x1105);
    	final UUID UUID_OBEX_FILE_TRANSFER = new UUID(0x1106);
    	final UUID UUID_PERSONAL_NETWORK = new UUID(0x1115);
    	final UUID UUID_ACCESS_POINT = new UUID(0x1116);
    	final UUID UUID_GROUP_NETWORK = new UUID(0x1117);
 
    	List<Sensor> sensors = new ArrayList<Sensor>();
    	
    	final Object serviceSearchCompletedEvent = new Object();
    	
    	DiscoveryListener listener = new DiscoveryListener() {

    		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
    		}

    		public void inquiryCompleted(int discType) {
    		}

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {

            	LOG.info( "servicesDiscovered " );
                for (int i = 0; i < servRecord.length; i++) {
                    String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    if (url == null) {
                        continue;
                    }
                    Sensor sensor = new Sensor();
                    sensor.setUrl( url );
                    DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
                    RemoteDevice remoteDevice = servRecord[i].getHostDevice();
                    try {
                    	LOG.info( "Device Service Discovered: " + remoteDevice.getFriendlyName(false) );
						sensor.setName( remoteDevice.getFriendlyName(false) );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    sensors.add( sensor );
                    if (serviceName != null) {
                    	LOG.info("service " + serviceName.getValue() + " found " + url);
                    } else {
                    	LOG.info("service found " + url);
                    }
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            	LOG.info("service search completed!");
            	LOG.info( sensors.size() +  " service(s) found");
    			switch (respCode) {
                  case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
                	  LOG.info( "The service search completed normally");
                	  break;
                  case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
                	  LOG.info( "The service search request was cancelled by a call to DiscoveryAgent.cancelServiceSearch(int)");
                	  break;
                  case DiscoveryListener.SERVICE_SEARCH_ERROR:
                	  LOG.info( "An error occurred while processing the request");
                	  break;
                  case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
                	  LOG.info( "No records were found during the service search");
                	  break;
                  case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
                	  LOG.info( "The device specified in the search request could not be reached or the local device could not establish a connection to the remote device");
                	  break;
                  default:
                	  LOG.info( "Unknown Response Code - " + respCode);
                	  break;
    			}    			
                synchronized(serviceSearchCompletedEvent){
                    serviceSearchCompletedEvent.notifyAll();
                }
            }   		
    	};

        UUID[] searchUuidSet = new UUID[] { UUID_SDP, 
        		UUID_RFCOMM, 
        		UUID_OBEX, 
        		UUID_HTTP,
        		UUID_L2CAP,
        		UUID_BNEP,
        		UUID_SERIAL,
        		UUID_SERVICE_DISCOVERY_SERVER,
        		UUID_BROWSE_GROUP,
        		UUID_PUBLIC_BROWSE_GROUP,
        		UUID_OBEX_OBJECT_PUSH,
        		UUID_OBEX_FILE_TRANSFER,
        		UUID_PERSONAL_NETWORK,
        		UUID_ACCESS_POINT,
        		UUID_GROUP_NETWORK
        	};
        UUID[] searchUuidSet2 = new UUID[] { 
        		UUID_RFCOMM 
        	};
        int[] attrIDs =  new int[] {
                0x0100 // Service name
        };
    	
        for( RemoteDevice remoteDevice : remoteDevices ) {
            synchronized(serviceSearchCompletedEvent) {
            	LOG.info("search services on " + remoteDevice.getBluetoothAddress() + " " + remoteDevice.getFriendlyName(false));
            	LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet2, remoteDevice, listener);
            	serviceSearchCompletedEvent.wait();
            }
        }
 
        return sensors;
    }    
    
    
    
    public void connect( String connectUrl ) throws IOException, InterruptedException 
    {
    	LOG.info("Connecting to " + connectUrl);

    	StreamConnection streamConnection=(StreamConnection)Connector.open(connectUrl);

    	Thread.sleep(500);
    	
    	OutputStream outStream=streamConnection.openOutputStream();
        PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
        pWriter.write("Brew Services\n\r");
        pWriter.flush();


        //read response
        InputStream inStream=streamConnection.openInputStream();
        BufferedReader bReader2=new BufferedReader(new InputStreamReader(inStream));
        String lineRead=bReader2.readLine();
        LOG.info(lineRead);    	
    	
    }    
    
}