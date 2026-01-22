package com.fenix.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Fenix Commerce Platform
 * Multi-tenant eCommerce Order Management System
 */
@SpringBootApplication
@EnableJpaAuditing
public class FenixCommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FenixCommerceApplication.class, args);
    }
}
