package com.kp.moneyManager.service;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    @Async
    public void sendMail(String to, String subject, String body) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML mail

            mailSender.send(mimeMessage);
            log.info("✅ Email sent successfully to " + to);


        } catch (Exception e) {
            log.error("❌ Error while sending email: " + e.getMessage());

            System.out.println("Error while sending email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }


    }


}