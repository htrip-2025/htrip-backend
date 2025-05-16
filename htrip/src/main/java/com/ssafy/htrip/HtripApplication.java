package com.ssafy.htrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HtripApplication {

	public static void main(String[] args) {
		SpringApplication.run(HtripApplication.class, args);
	}

}
