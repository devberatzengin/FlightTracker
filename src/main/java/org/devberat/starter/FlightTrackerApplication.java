package org.devberat.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan( basePackages = {"org.devberat"})
@EnableJpaRepositories( basePackages = {"org.devberat"})
@ComponentScan(basePackages = {"org.devberat"})
@SpringBootApplication
public class FlightTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightTrackerApplication.class, args);
    }

}
