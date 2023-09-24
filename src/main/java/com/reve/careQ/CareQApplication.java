package com.reve.careQ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CareQApplication {

	public static void main(String[] args) {
		SpringApplication.run(CareQApplication.class, args);
	}

}
