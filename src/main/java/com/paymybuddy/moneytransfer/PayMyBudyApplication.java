package com.paymybuddy.moneytransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.paymybuddy.moneytransfer.repository")
public class PayMyBudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayMyBudyApplication.class, args);
	}

}
