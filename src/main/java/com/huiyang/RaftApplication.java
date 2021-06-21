package com.huiyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RaftApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaftApplication.class, args);
    }

}
