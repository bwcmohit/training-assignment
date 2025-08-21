package org.stark.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.stark.entities.User;
import org.stark.enums.Roles;
import org.stark.repositories.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin1").isEmpty()) {
                User admin = new User();
                admin.setFirstName("Jarvis");
                admin.setLastName("AI");
                admin.setEmail("jarvis@stark.com");
                admin.setUsername("admin1");
                admin.setMobile("9999999999");
                admin.setPassword(encoder.encode("Admin@123"));
                admin.setRole(Roles.ADMIN);
                repo.save(admin);
            }
        };
    }
}
