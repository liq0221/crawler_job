package com.mypro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobInfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobInfoApplication.class,args);
    }
}
