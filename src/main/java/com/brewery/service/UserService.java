package com.brewery.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.brewery.model.Password;
import com.brewery.model.ResetToken;
import com.brewery.model.User;
import com.brewery.model.VerificationToken;
import com.brewery.service.DataService;

@Service
public class UserService {

	private Logger LOG = LoggerFactory.getLogger( UserService.class );
	
    private DataService dataService;
    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUsername( user.getUsername() );
        verificationToken.setExpiryDate(  verificationToken.calculateExpiryDate( 20 ) );

        dataService.saveVerificationToken( verificationToken );
    }

    public boolean confirmUser(String token) {
        //retrieve token
        VerificationToken verificationToken = dataService.getVerificationToken( token );
        //verify date
        LOG.info("confirmUser: " ); 
        if(verificationToken != null && verificationToken.getExpiryDate().after(new Date())) {
        	User user = dataService.getUserByName( verificationToken.getUsername() );
            //update user details
        	user.setValidated( true );
        	dataService.saveUser( user );
            //delete from tokens
            dataService.deleteVerificationToken(token);
            return true;
        }
        return false;
    }

	public void createResetToken(Password password, String token) {
        LOG.info("createResetToken: " ); 
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(password.getEmail());
        resetToken.setUsername(password.getUsername());
        resetToken.setExpiryDate( resetToken.calculateExpiryDate( 20 ) );

        dataService.saveResetToken(resetToken);
        LOG.info("createResetToken: created" ); 
	}
}
