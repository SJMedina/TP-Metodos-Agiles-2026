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
}