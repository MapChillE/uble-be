package com.ureca.uble;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UbleApplication {

    public static void main(String[] args) {
        SpringApplication.run(UbleApplication.class, args);
    }

}
