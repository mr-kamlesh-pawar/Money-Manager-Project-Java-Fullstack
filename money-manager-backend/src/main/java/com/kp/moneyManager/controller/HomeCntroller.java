package com.kp.moneyManager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/status", "/health"})
public class HomeCntroller {

    @GetMapping()
    public String healthCheck(){

        return "Running Successfully";
    }


}
