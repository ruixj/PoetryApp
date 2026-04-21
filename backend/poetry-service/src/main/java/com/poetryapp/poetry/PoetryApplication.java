package com.poetryapp.poetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PoetryApplication {
    public static void main(String[] args) {
        SpringApplication.run(PoetryApplication.class, args);
    }
}
