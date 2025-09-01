package com.kp.moneyManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneyManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyManagerApplication.class, args);
        System.out.println("\n\n\n....................App Successfully Started...................\n\n");

    }

}
