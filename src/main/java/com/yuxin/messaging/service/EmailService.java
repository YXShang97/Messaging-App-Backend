package com.yuxin.messaging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    public void sendEmail(String sendTo, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(sendTo);
        msg.setSubject(subject);
        msg.setText(text);
        javaMailSender.send(msg);
    }
}
