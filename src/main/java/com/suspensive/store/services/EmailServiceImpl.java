package com.suspensive.store.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.suspensive.store.services.interfaces.IEmailService;
import com.suspensive.store.util.ExcludeFromJacocoGeneratedReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl implements IEmailService {

    @Value("${java.mail.sender.username}")
    private String mailUsername;

    @Autowired
    private JavaMailSender javaMailSender;


    @Override
    @ExcludeFromJacocoGeneratedReport
    public void sendEmail(String toUser, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailUsername);
        mailMessage.setTo(toUser);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }

    @Override
    public void validateEmail(String email) throws Exception {
        final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);

        if(!matcher.matches()){
            throw new Exception("Email is not valid");
        }
    }

}
