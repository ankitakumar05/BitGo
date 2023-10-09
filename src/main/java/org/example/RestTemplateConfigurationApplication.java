package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.service.TransactionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableAutoConfiguration
public class RestTemplateConfigurationApplication {

    public static void main(String[] args) throws JsonProcessingException {
        ApplicationContext applicationContext = SpringApplication.run(RestTemplateConfigurationApplication.class, args);

        TransactionService transactionService = applicationContext.getBean(TransactionService.class);
        transactionService.getEmployee("0");
    }
}