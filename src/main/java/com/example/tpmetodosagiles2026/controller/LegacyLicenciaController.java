package com.example.tpmetodosagiles2026.controller;

import com.example.tpmetodosagiles2026.dto.EmitirLicenciaDTO;
import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.service.LicenciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/licencias")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class LegacyLicenciaController {

    private final LicenciaService service;

    public LegacyLicenciaController(LicenciaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Licencia>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<?> emitir(@RequestBody EmitirLicenciaDTO dto) {
        try {
            Licencia licencia = service.emitir(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(licencia);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
