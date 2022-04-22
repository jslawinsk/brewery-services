package com.brewery.security;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.brewery.model.DbSync;
import com.brewery.model.User;
import com.brewery.model.UserRoles;
import com.brewery.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;

@RunWith( SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
				properties = { "blueTooth.enabled=false", "wiFi.enabled=false" }
			)
@AutoConfigureMockMvc
class JWTAuthorizationFilterTest {

	@LocalServerPort
	private int port;

    @Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	DataService dataService;
	
	@MockBean
	JavaMailSender mailSender;

	@Mock
	HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
	@Mock
	HttpServletResponse response = Mockito.mock( HttpServletResponse.class );
	@Mock
	FilterChain chain = Mockito.mock( FilterChain.class );

	JWTAuthorizationFilter jWTAuthorizationFilter = new JWTAuthorizationFilter( );
	
	@Test
	void testDoFilterInternal() throws Exception, IOException {

		User user = new User( "TEST", "test", DbSync.ADD, UserRoles.ADMIN.toString() );
		user.setPassword( passwordEncoder.encode( "test" ));
		Mockito.when(dataService.getUserByName( "TEST" )).thenReturn( user );
	    MvcResult result = mockMvc.perform( MockMvcRequestBuilders.post("http://localhost:" + port + "/api/authorize?user=TEST&password=test")
	            .accept(MediaType.ALL))
	            .andDo(print())
	            .andExpect(status().isOk())
	            .andReturn();
		String json = result.getResponse().getContentAsString();		
		User respUserEntity = new ObjectMapper().readValue(json, User.class);
		Mockito.when( request.getHeader( "Authorization" ) ).thenReturn( respUserEntity.getToken() );
		jWTAuthorizationFilter.doFilterInternal(request, response, chain);
		
		Mockito.when( request.getHeader( "Authorization" ) ).thenReturn( "Bearer eyJhbGciO" );
		jWTAuthorizationFilter.doFilterInternal(request, response, chain);
		
		assertTrue( true );
	}
	
}
