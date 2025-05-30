package com.example.BotForDuty;

import com.example.BotForDuty.config.BotConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class BotForDutyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotForDutyApplication.class, args);
	}
}



