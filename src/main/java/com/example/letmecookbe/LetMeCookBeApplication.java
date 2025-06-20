package com.example.letmecookbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LetMeCookBeApplication {

	public static void main(String[] args)
	{
		SpringApplication.run(LetMeCookBeApplication.class, args);
	}

}
