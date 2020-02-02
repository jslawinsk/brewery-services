package com.brewery.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.bluetooth.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.brewery.model.Batch;
import com.brewery.model.DbSync;
import com.brewery.model.Measurement;
import com.brewery.model.Message;
import com.brewery.model.Sensor;
import com.brewery.model.SensorData;
import com.brewery.model.Style;
import com.brewery.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("prototype")
public class DataSynchThread implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger( DataSynchThread.class );

    private DataService dataService;
    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Value("${dataSynch.url}")
    private String dataSynchUrl;    
	
    @Override
    public void run() {
        LOG.info("Running DataSynchThread");
        while( true ) {
			try {
				// Thread.sleep( 1000 * 1800 ); // 1800 seconds every 30 miniutes
				Thread.sleep(15000);

	        	String response = "";
	        	int attempt = 0;
	        	while( !"ACK".equals( response ) && attempt < 10 ) {
					RestTemplate restTemplate = new RestTemplate();
					try {
						response = restTemplate.getForObject( dataSynchUrl + "heartBeat", String.class);
						LOG.info( "DB Synch health response: " + response );
					} catch( Exception e ) {
						LOG.error( e.getMessage() );
					}	
					attempt++;
	        	}
				
				Thread.sleep(500);
				List<Style> styles= dataService.getStylesToSynchronize();
				for( Style style: styles ) {
					LOG.info( "Style to Synchronize: " + style );
					if( style.getDbSynch() == DbSync.ADD ) {
						RestTemplate restTemplate = new RestTemplate();
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);		
						
					    HttpEntity<Style> request = new HttpEntity<>(style, headers);
						
					    URI uri = new URI( dataSynchUrl + "style");
					     
					    ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
					    if( result.getStatusCodeValue() == 201 ) {
					    	style.setDbSynch( DbSync.SYNCHED );
					    	dataService.updateStyle( style );
					    }
					}
				}
				
			} catch (InterruptedException e) {
				LOG.error( e.getMessage() );
			} catch( Exception e ) {
				LOG.error( e.getMessage() );
			}	
        }
    }
    
}