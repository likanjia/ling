package com.ling.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ling.framework.feign.annotation.EnableLingFeignClients;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.ling"})
public class LingOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(LingOauthApplication.class, args);
    }

}
