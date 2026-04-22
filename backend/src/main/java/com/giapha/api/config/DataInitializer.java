package com.giapha.api.config;

import com.giapha.api.entity.User;
import com.giapha.api.enums.Role;
import com.giapha.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User admin = userRepository.findByUsername("admin").orElseGet(() -> 
            User.builder().username("admin").role(Role.ADMIN).build()
        );
        admin.setPassword(passwordEncoder.encode("admin123"));
        userRepository.save(admin);
        System.out.println("Admin account guaranteed to be: admin / admin123");
    }
}
