package com.brewery.config;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import com.brewery.service.DataService;
import com.brewery.security.JWTAuthorizationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{
	
//	@Bean
//	public BCryptPasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	};
	
    @Configuration
    @Order(1)
    public static class RestApiSecurityConfig extends WebSecurityConfigurerAdapter {
        private Logger LOG = LoggerFactory.getLogger( RestApiSecurityConfig.class );

    	@Override
    	protected void configure(final HttpSecurity http) throws Exception {
    		LOG.info("configure: " + http.toString() );		
    		http
    			.antMatcher("/api/**")
    			.csrf().disable()
    			.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
    			.authorizeRequests()
    				.antMatchers(HttpMethod.POST, "/api/authorize/**").permitAll()
    				.and()
    			.authorizeRequests()
    				.antMatchers( "/api/**" ).hasAuthority( "API" )
    				.and()
    			.formLogin()
    				.loginPage("/api/notauthenticated")
    				.permitAll();
    		http.cors();
    	}
    }

    @Configuration
    @Order(2)
    public static class UiSecurityConfig extends WebSecurityConfigurerAdapter {
        private Logger LOG = LoggerFactory.getLogger( UiSecurityConfig.class );

        private DataService dataService;
        @Autowired
        public void setDataService(DataService dataService) {
            this.dataService = dataService;
        }
    	
        @Bean
        public UserDetailsService userDetailsService() {
          return dataService;
        };
        
//        @Autowired
//        private PasswordEncoder passwordEncoder;

    	@Bean
    	public BCryptPasswordEncoder passwordEncoder() {
    		return new BCryptPasswordEncoder();
    	};
        
    	@Override
    	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    	}	
        
    	@Override
    	protected void configure(final HttpSecurity http) throws Exception {
    		LOG.info("configure: " + http.toString() );		
    		http
    			.authorizeRequests()
    				.antMatchers( "/css/**", "/js/**", "/webjars/**", "/images/**", "/validate/**", "/password/**", "/passwordReset/**" ).permitAll()		
    				.and()
    			.authorizeRequests()
    				.antMatchers( "/style/**", "/process/**", "/measureType/**", "/batch/**", "/measurement/**", "/sensor/**", "/user/**"  ).hasRole( "ADMIN" )
    				.and()
    			.authorizeRequests()
    				.antMatchers( "/", "/profile/**" ).hasAnyRole( "ADMIN", "USER" )
    				.and()
    			.formLogin()
    				.loginPage("/login")
    				.permitAll()
    				.and()
    			.logout()
    				.permitAll();
    		http.cors();
    	}
    	
    	@Override
	    public void configure(WebSecurity web) throws Exception {
	        web
	            .ignoring()
	            .antMatchers("/h2/**", "/swagger-ui**");
	    }
    	
    	
    }
}