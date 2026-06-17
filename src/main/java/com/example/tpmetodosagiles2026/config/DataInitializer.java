package com.example.tpmetodosagiles2026.config;

import com.example.tpmetodosagiles2026.model.SuperUsuario;
import com.example.tpmetodosagiles2026.model.UsuarioAdministrativo;
import com.example.tpmetodosagiles2026.repository.SuperUsuarioRepository;
import com.example.tpmetodosagiles2026.repository.UsuarioAdministrativoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SuperUsuarioRepository superUsuarioRepository;
    private final UsuarioAdministrativoRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SuperUsuarioRepository superUsuarioRepository, UsuarioAdministrativoRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.superUsuarioRepository = superUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!superUsuarioRepository.existsById("superadmin")) {
            superUsuarioRepository.save(
                new SuperUsuario("superadmin", passwordEncoder.encode("super1234"))
            );
        }

        if (!usuarioRepository.existsById("emp01")) {
            usuarioRepository.save(
                new UsuarioAdministrativo("emp01", "Juan Pérez", passwordEncoder.encode("emp123"))
            );
        }

        if (!usuarioRepository.existsById("emp02")) {
            usuarioRepository.save(
                new UsuarioAdministrativo("emp02", "María García", passwordEncoder.encode("emp123"))
            );
        }

        if (!usuarioRepository.existsById("emp03")) {
            usuarioRepository.save(
                new UsuarioAdministrativo("emp03", "Carlos López", passwordEncoder.encode("emp123"))
            );
        }
    }
}

