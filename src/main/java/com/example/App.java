package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class App {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		int exitCode = SpringApplication.exit(SpringApplication.run(App.class, args));
		Logger logger = LoggerFactory.getLogger(App.class);
		logger.info("exitCode = {}", exitCode);
		System.exit(exitCode);
	}
}
