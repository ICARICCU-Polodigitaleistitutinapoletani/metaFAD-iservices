package com.gruppometa.poloigitale.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class Application {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    public static void main2(String[] args) {
        try (ConfigurableApplicationContext context =
                     SpringApplication.run(Application.class, args)) {
            LOGGER.error("context: " + context);
        }
    }
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    protected int timeout = 30000;
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder)
    {
        return restTemplateBuilder
                .setConnectTimeout(timeout)
           .setReadTimeout(timeout)
           .build();
    }
}
