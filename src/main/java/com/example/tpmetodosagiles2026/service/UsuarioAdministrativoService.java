package com.example.tpmetodosagiles2026.service;

import com.example.tpmetodosagiles2026.dto.CrearUsuarioDTO;
import com.example.tpmetodosagiles2026.model.UsuarioAdministrativo;
import com.example.tpmetodosagiles2026.repository.UsuarioAdministrativoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioAdministrativoService {

    private final UsuarioAdministrativoRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAdministrativoService(UsuarioAdministrativoRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioAdministrativo crear(CrearUsuarioDTO dto) {
        if (repository.existsById(dto.getId())) {
            throw new IllegalArgumentException("Ya existe un usuario con el ID: " + dto.getId());
        }
        String hash = passwordEncoder.encode(dto.getPassword());
        UsuarioAdministrativo usuario = new UsuarioAdministrativo(dto.getId(), dto.getNombre(), hash);
        return repository.save(usuario);
    }

    public List<UsuarioAdministrativo> listar() {
        return repository.findAll();
    }
}
