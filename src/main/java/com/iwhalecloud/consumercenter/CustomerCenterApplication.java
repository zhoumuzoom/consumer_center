package com.iwhalecloud.consumercenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CustomerCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerCenterApplication.class, args);
    }

}
