package com.nicokuchling.wegfest.iteration_composite_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan("com.nicokuchling.wegfest")
public class IterationCompositeServiceApplication {

    @Bean
    RestTemplate restTemplate() { return new RestTemplate(); }

    public static void main(String[] args) {
        SpringApplication.run(IterationCompositeServiceApplication.class, args);
    }

}
