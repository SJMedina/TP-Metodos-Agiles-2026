package com.example.tpmetodosagiles2026.controller;

import com.example.tpmetodosagiles2026.dto.CrearUsuarioDTO;
import com.example.tpmetodosagiles2026.model.UsuarioAdministrativo;
import com.example.tpmetodosagiles2026.service.UsuarioAdministrativoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class UsuarioAdministrativoController {

    private final UsuarioAdministrativoService service;

    public UsuarioAdministrativoController(UsuarioAdministrativoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CrearUsuarioDTO dto) {
        try {
            UsuarioAdministrativo usuario = service.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioAdministrativo>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}
