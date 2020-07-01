package com.brewery.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import com.brewery.service.DataService;
import com.brewery.security.JWTAuthorizationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
    private Logger LOG = LoggerFactory.getLogger( WebSecurityConfigurerAdapter.class );
    
    private DataService dataService;
    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
	
    @Bean
    public UserDetailsService userDetailsService() {
      return dataService;
    };
    
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	};
	  
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOG.info("configure: " + http.toString() );		
		http
			.csrf().disable()
			.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
				.antMatchers( "/css/**", "/js/**", "/webjars/**", "/images/**" ).permitAll()		
				.and()
			.authorizeRequests()
				.antMatchers( "/style/**", "/process/**", "/measureType/**", "/batch/**", "/measurement/**", "/sensor/**", "/user/**"  ).hasRole( "ADMIN" )
				.and()
			.authorizeRequests()
				.antMatchers( "/" ).hasAnyRole( "ADMIN", "USER" )
				.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/authorize").permitAll()
				.and()
			.authorizeRequests()
				.antMatchers( "/api/**" ).hasAuthority( "API" )
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll();
		http.cors();
	}
}