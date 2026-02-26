package com.manjula.jobportal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.manjula.jobportal.repository.UserRepository;
import com.manjula.jobportal.model.User;
import com.manjula.jobportal.model.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {

            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            System.out.println("Admin user created!");
        }
    }
}
