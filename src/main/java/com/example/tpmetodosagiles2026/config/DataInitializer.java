package com.example.tpmetodosagiles2026.config;

import com.example.tpmetodosagiles2026.model.SuperUsuario;
import com.example.tpmetodosagiles2026.repository.SuperUsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SuperUsuarioRepository superUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SuperUsuarioRepository superUsuarioRepository, PasswordEncoder passwordEncoder) {
        this.superUsuarioRepository = superUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!superUsuarioRepository.existsById("superadmin")) {
            superUsuarioRepository.save(
                new SuperUsuario("superadmin", passwordEncoder.encode("super1234"))
            );
        }
    }
}
