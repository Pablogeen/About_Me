package com.ben.my_portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class MyPortfolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyPortfolioApplication.class, args);
	}

}
