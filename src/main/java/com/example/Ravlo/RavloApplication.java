package com.example.Ravlo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RavloApplication {

	public static void main(String[] args) {
		SpringApplication.run(RavloApplication.class, args);
	}

}
