package com.ling.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LingGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LingGatewayApplication.class, args);
    }

}
