package com.bfh.qualifier;

import com.bfh.qualifier.config.Settings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(Settings.class)
public class QualifierApplication {

    public static void main(String[] args) {
        SpringApplication.run(QualifierApplication.class, args);
    }

    @Bean
    CommandLineRunner run(RunnerService service) {
        return args -> service.run();
    }
}
