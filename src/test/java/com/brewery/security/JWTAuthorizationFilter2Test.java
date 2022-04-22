package com.brewery.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.brewery.model.DbSync;
import com.brewery.model.User;
import com.brewery.model.UserRoles;
import com.brewery.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

@RunWith( SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
				properties = { "blueTooth.enabled=false", "wiFi.enabled=false" }
			)
@AutoConfigureMockMvc
class JWTAuthorizationFilter2Test {

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

	@Mock
	Jwt jwt = Mockito.mock( Jwt.class );
	@Mock
	Jwts jwts = Mockito.mock( Jwts.class );
	@Mock
	Jws jws = Mockito.mock( Jws.class );
	@Mock
	JwtParser jwtParser = Mockito.mock( JwtParser.class );
	@Mock
	JwtParser jwtParser2 = Mockito.mock( JwtParser.class );
	@Mock
	Claims claims = Mockito.mock( Claims.class );
	
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

		Mockito.when( claims.get("authorities") ).thenReturn( null );
		Mockito.when( jwtParser.setSigningKey( "mySecretKey".getBytes()) ).thenReturn( jwtParser2 );
		Mockito.when( jwtParser2.parseClaimsJws( Mockito.any( String.class ) ) ).thenReturn( jws );
		Mockito.when( jws.getBody() ).thenReturn( claims );
		
		try (MockedStatic<Jwts> jwts = Mockito.mockStatic(Jwts.class)) {
			jwts.when(() -> Jwts.parser()).thenReturn( jwtParser );

			jWTAuthorizationFilter.doFilterInternal(request, response, chain);
		}		
		assertTrue( true );
	}
	
}
