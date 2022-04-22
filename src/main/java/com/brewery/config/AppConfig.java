package com.brewery.config;

import java.time.Duration;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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

    @Value("${spring.mail.host}")
    private String mailHost;
	
    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUser;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Bean
    public JavaMailSender javaMailSender() {
    	
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost( mailHost );
        mailSender.setPort( mailPort );
          
        mailSender.setUsername( mailUser );
        mailSender.setPassword( mailPassword );
          
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
          
        return mailSender;    	
    }
		
}
