package com.brewery.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.brewery.model.Password;
import com.brewery.service.UserService;

import java.util.UUID;

@Component
public class PasswordListener implements ApplicationListener<OnPasswordResetEvent> {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;
    
    @Override
    public void onApplicationEvent(OnPasswordResetEvent event) {
        this.resetPassword(event);
    }

    private void resetPassword(OnPasswordResetEvent event) {
        //create password token
        Password password = event.getPassword();
        String token = UUID.randomUUID().toString();
        userService.createResetToken(password, token);
        //get email properties
        String recipientAddress = password.getEmail();
        String subject = "Reset Password";
        String confirmationUrl = event.getServerUrl() + "/"  + "passwordReset?token=" + token;
        String message = "Reset password:";
        //send email
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + confirmationUrl );
        mailSender.send(email);
    }
}
