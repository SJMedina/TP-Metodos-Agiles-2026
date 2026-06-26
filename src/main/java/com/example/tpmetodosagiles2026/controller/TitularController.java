package com.example.tpmetodosagiles2026.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tpmetodosagiles2026.entity.Titular;
import com.example.tpmetodosagiles2026.service.TitularService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/titulares")
@CrossOrigin(origins = "http://localhost:4200")
public class TitularController {

    private final TitularService titularService;

    @Autowired
    public TitularController(TitularService titularService) {
        this.titularService = titularService;
    }

    @PostMapping
    public ResponseEntity<?> darDeAlta(@RequestBody Titular titular) {
        try {
            Titular nuevoTitular = titularService.registrarTitular(titular);
            return new ResponseEntity<>(nuevoTitular, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Devuelve 400 Bad Request si no cumple las reglas de negocio
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<java.util.List<Titular>> listarTitulares() {
        return ResponseEntity.ok(titularService.listarTitulares());
    }

    // Endpoint para obtener los datos de un titular específico (Soluciona el error 405)
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarTitularPorId(@PathVariable Long id) {
        try {
            Titular titular = titularService.buscarTitularPorId(id);
            return ResponseEntity.ok(titular);
        } catch (IllegalArgumentException e) {
            // Si no existe, devolvemos un 404 Not Found
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarTitular(@PathVariable Long id, @RequestBody Titular titularActualizado) {
        try {
            Titular titularGuardado = titularService.modificarTitular(id, titularActualizado);
            return ResponseEntity.ok(titularGuardado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}