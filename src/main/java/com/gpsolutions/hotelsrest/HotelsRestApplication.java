package com.gpsolutions.hotelsrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class HotelsRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelsRestApplication.class, args);
    }

}
