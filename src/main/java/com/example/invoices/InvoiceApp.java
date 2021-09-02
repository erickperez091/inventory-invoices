package com.example.invoices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Hello world!
 *
 */

@SpringBootApplication
@ComponentScan( { "com.example" } )
@EnableJpaRepositories( basePackages = { "com.example.invoices.repository" } )
@EnableKafka
@EnableEurekaClient
public class InvoiceApp
{
    public static void main( String[] args )
    {
        SpringApplication.run( InvoiceApp.class, args );
    }
}
