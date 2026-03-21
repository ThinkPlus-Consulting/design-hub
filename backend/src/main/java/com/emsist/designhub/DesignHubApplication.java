package com.emsist.designhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DesignHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesignHubApplication.class, args);
    }
}
