package com.example.tpmetodosagiles2026.controller;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return Map.of("status", "ok", "db", "ok");
        } catch (Exception ex) {
            return Map.of("status", "error", "db", "down", "message", ex.getMessage());
        }
    }

    @GetMapping("/api/health/licencias/count")
    public Map<String, Object> licenciasCount() {
        try {
            Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM licencias", Long.class);
            return Map.of("status", "ok", "db", "ok", "licencias", count);
        } catch (Exception ex) {
            return Map.of("status", "error", "db", "down", "message", ex.getMessage());
        }
    }
}

