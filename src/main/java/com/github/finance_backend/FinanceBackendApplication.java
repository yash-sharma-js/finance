package com.github.finance_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FinanceBackendApplication {

	public static void main(String[] args) {
        System.out.println("${jwt.secret}");
		SpringApplication.run(FinanceBackendApplication.class, args);
	}
}
