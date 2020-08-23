package br.com.ifood.logistics.demos.spring.integration.aws.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringIntegrationAwsDemoApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SpringIntegrationAwsDemoApplication.class, args);
    }

}
