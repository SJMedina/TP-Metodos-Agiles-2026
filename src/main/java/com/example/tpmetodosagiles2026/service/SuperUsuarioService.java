package com.example.tpmetodosagiles2026.service;

import com.example.tpmetodosagiles2026.model.SuperUsuario;
import com.example.tpmetodosagiles2026.repository.SuperUsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SuperUsuarioService {

    private final SuperUsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public SuperUsuarioService(SuperUsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean autenticar(String id, String password) {
        Optional<SuperUsuario> superUsuario = repository.findById(id);
        return superUsuario.isPresent() && passwordEncoder.matches(password, superUsuario.get().getPasswordHash());
    }
}
