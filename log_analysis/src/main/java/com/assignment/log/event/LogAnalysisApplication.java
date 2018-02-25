package com.assignment.log.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class LogAnalysisApplication implements CommandLineRunner  {
	
   private static final Logger log = LoggerFactory.getLogger(LogAnalysisApplication.class);

	public static void main(String[] args) {
		log.debug("Staring spring boot application");
		SpringApplication.run(LogAnalysisApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}
  
}
