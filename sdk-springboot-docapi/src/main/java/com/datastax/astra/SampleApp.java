package com.datastax.astra;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;

@SpringBootApplication(exclude = CassandraAutoConfiguration.class)
public class SampleApp {

    public static void main(String[] args) {
        SpringApplication.run(SampleApp.class, args);
    }
    
    @PostConstruct
    public void sample() {
        System.out.println("OK");
    }
    
}