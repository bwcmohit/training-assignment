package org.stark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "org.stark")
@EnableJpaAuditing
public class personalNotesApplication {

	public static void main(String[] args) {
		SpringApplication.run( personalNotesApplication.class, args);
	}

}
