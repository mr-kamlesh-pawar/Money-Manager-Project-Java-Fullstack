package com.kp.moneyManager.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class EmailService {

     private final JavaMailSender mailSender;

     @Value("${spring.mail.properties.mail.smtp.from}")
     private String fromEmail;


     public  void sendMail(String to, String subject , String body){

     try {
          SimpleMailMessage message = new SimpleMailMessage();
          message.setFrom(fromEmail);
          message.setTo(to);
          message.setSubject(subject);
          message.setText(body);

          mailSender.send(message);


     } catch (Exception e){
          System.out.println( "Error while sending email: " +e.getMessage());
          e.printStackTrace();
          throw new RuntimeException(e.getMessage());
     }


     }



}