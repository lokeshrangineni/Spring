package com.assignment.log.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
/**
 * Spring boot application to start the spring batch job.
 * 
 * @author Lokesh
 * Since 02/25/2018
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class LogAnalysisApplication {
	
   private static final Logger log = LoggerFactory.getLogger(LogAnalysisApplication.class);

	public static void main(String[] args) {
		log.info("Staring Log Analysis application");
		SpringApplication.run(LogAnalysisApplication.class, args);
	}

	
  
}
