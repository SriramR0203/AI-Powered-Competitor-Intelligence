package com.competitorintel.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
@ConfigurationPropertiesScan
public class CompetitorIntelligenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompetitorIntelligenceApplication.class, args);
    }
}
