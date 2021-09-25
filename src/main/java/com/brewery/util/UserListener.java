package com.brewery.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.brewery.model.User;
import com.brewery.service.UserService;

import java.util.UUID;

@Component
public class UserListener implements ApplicationListener<OnCreateUserEvent> {

//    private String serverUrl = "http://localhost:8080/";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(OnCreateUserEvent event) {
        this.confirmCreateUser(event);
    }

    private void confirmCreateUser(OnCreateUserEvent event) {
        //get the account
        //create verification token
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken( user, token);
        //get email properties
        String recipientAddress = user.getEmail();
        String subject = "Brewery Services User Confirmation";
        String confirmationUrl = event.getServerUrl() + "/" + event.getAppPath() + "?token=" + token;
        String message = "Please confirm:";
        //send email
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + confirmationUrl);
        mailSender.send(email);

    }
}
