package com.example.miapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "ComiCommerce API - Backend",
    version = "1.0",
    description = "API RESTful para la gestión de productos, usuarios y carrito de ComiCommerce"
))

public class MiappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiappApplication.class, args);
	}

}