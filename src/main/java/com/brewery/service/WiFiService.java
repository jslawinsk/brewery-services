package com.brewery.service;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.net.util.*;

import com.brewery.model.Sensor;

@Service
public class WiFiService {

	private Logger LOG = LoggerFactory.getLogger( WiFiService.class );
	
    @Autowired
    @Qualifier( "restTemplateWifi" )
    private RestTemplate restTemplateWifi;
	
    public List<Sensor> discoverSensors( String pSubnetmask ) throws SocketException {
    	List<Sensor> sensors = new ArrayList<Sensor>();    	
        try {
			InetAddress inetAddress = InetAddress.getLocalHost();
        	LOG.info("InetAddress LocalHost: " + inetAddress.getHostAddress());
        	
        	String subNetMask = inetAddress.getHostAddress();
        	int i = subNetMask.lastIndexOf( "." );
        	if( i> -1) {
        		subNetMask = subNetMask.substring( 0, i+1 );
        		subNetMask = subNetMask + "0/24";
        	}
        	if( pSubnetmask.length() > 0 ) {
        		subNetMask = pSubnetmask;
        	}
        	LOG.info("SubNet Mask: " + subNetMask );
        	
            SubnetUtils utils = new SubnetUtils( subNetMask);
            String[] allIps = utils.getInfo().getAllAddresses();        

            for (String ip : allIps ) {
            	if( checkIp( ip ) ) {
	            	Sensor sensor = new Sensor();
	            	sensor.setUrl( "http://" + ip + "/tempdata?responseFormat=JSON" ); 
	            	sensor.setName( ip );
	                sensors.add( sensor );
            	}
            }
		} catch (Exception e) {
			LOG.error( "discoverSensors: Exception: ", e);
		}
        return sensors;
    }
    
    private boolean checkIp( String ip )
    {
		LOG.info("Checking Ip: " + ip );
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		
	    HttpEntity<String> request = new HttpEntity<>( "", headers);
	    
	    ResponseEntity<String> responseEnt = null;
	    URI uri;
		try {
			uri = new URI( "http://" + ip + "/tempdata?responseFormat=JSON");
			LOG.error( "checkIp: URI: " + uri);
		    responseEnt = restTemplateWifi.exchange( uri, HttpMethod.GET, request, String.class );
		} catch ( HttpClientErrorException e ) {
			LOG.info( "checkIp: HttpClientErrorException: " );
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				LOG.info( "checkIp: Exception: UNAUTHORIZED" );
				return true;
			}
		} catch (Exception e) {
			LOG.error( "checkIp: Exception: " );
		}
		return false;
    }
    
}
