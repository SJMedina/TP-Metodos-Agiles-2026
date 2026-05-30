package com.example.tpmetodosagiles2026.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.example.tpmetodosagiles2026.dto.CalcularVigenciaRequestDTO;
import com.example.tpmetodosagiles2026.dto.CalcularVigenciaResponseDTO;
import com.example.tpmetodosagiles2026.service.VigenciaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vigencia")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class VigenciaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VigenciaController.class);
    private final VigenciaService vigenciaService;

    public VigenciaController(VigenciaService vigenciaService) {
        this.vigenciaService = vigenciaService;
    }

    @PostMapping("/calcular")
    public ResponseEntity<?> calcular(@RequestBody CalcularVigenciaRequestDTO solicitud) {
        LOGGER.info("Solicitud de cálculo de vigencia: fechaNacimiento='{}', primeraLicencia='{}'",
            solicitud.getFechaNacimiento(), solicitud.getPrimeraLicencia());

        try {
            if (solicitud.getFechaNacimiento() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La fecha de nacimiento es requerida");
                return ResponseEntity.badRequest().body(error);
            }

            if (solicitud.getPrimeraLicencia() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El indicador de primera licencia es requerido");
                return ResponseEntity.badRequest().body(error);
            }

            if (solicitud.getFechaNacimiento().isAfter(LocalDate.now())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La fecha de nacimiento no puede ser en el futuro");
                return ResponseEntity.badRequest().body(error);
            }

            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaVencimiento = vigenciaService.calcularVencimiento(
                solicitud.getFechaNacimiento(),
                solicitud.getPrimeraLicencia(),
                fechaInicio
            );
            int edad = vigenciaService.calcularEdad(solicitud.getFechaNacimiento(), fechaInicio);
            int anios = vigenciaService.determinarAnios(edad, solicitud.getPrimeraLicencia());

            CalcularVigenciaResponseDTO respuesta = new CalcularVigenciaResponseDTO(
                fechaInicio,
                fechaVencimiento,
                anios,
                edad
            );

            LOGGER.info("Cálculo de vigencia completado: edad='{}', anios='{}', fechaVencimiento='{}'",
                edad, anios, fechaVencimiento);

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Error de validación al calcular vigencia: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al calcular vigencia", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al calcular la vigencia: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}

