package com.doppler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * The application configuration.
 */
@SpringBootApplication
@EnableRetry
public class Application {

	/**
	 * The main entry of the application.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
