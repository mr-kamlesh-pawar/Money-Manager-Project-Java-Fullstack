package com.kp.moneyManager.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class EmailService {

     private final JavaMailSender mailSender;



}