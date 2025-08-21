package org.stark.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtTokenConfig {

    @Bean
    public JwtUtil jwtUtil(JwtConfig jwtConfig) {
        return new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpirationMs());
    }
}
