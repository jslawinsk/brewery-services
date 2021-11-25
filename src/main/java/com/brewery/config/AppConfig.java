package com.brewery.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class AppConfig {
    
	@Bean( name = "restTemplate" )
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
	    return builder.build();
	}       
    
	@Bean( name = "restTemplateWifi" )
	public RestTemplate restTemplateWifi(RestTemplateBuilder builder) {
        return builder.setConnectTimeout( Duration.ofMillis(250) )
        		.setReadTimeout(Duration.ofMillis( 250) )
        		.build();
	}       

	
}
