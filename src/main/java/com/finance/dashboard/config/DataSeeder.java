package com.finance.dashboard.config;

import com.finance.dashboard.model.Role;
import com.finance.dashboard.model.User;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                .username("admin")
                .email("admin@finance.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .active(true)
                .build());
            log.info("Seeded default admin user: admin / admin123");
        }
        if (!userRepository.existsByUsername("analyst")) {
            userRepository.save(User.builder()
                .username("analyst")
                .email("analyst@finance.com")
                .password(passwordEncoder.encode("analyst123"))
                .role(Role.ANALYST)
                .active(true)
                .build());
        }
        if (!userRepository.existsByUsername("viewer")) {
            userRepository.save(User.builder()
                .username("viewer")
                .email("viewer@finance.com")
                .password(passwordEncoder.encode("viewer123"))
                .role(Role.VIEWER)
                .active(true)
                .build());
        }
    }
}
