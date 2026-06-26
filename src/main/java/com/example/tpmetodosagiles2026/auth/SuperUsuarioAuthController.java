package com.example.tpmetodosagiles2026.auth;

import com.example.tpmetodosagiles2026.service.SuperUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/super")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class SuperUsuarioAuthController {

    private final SuperUsuarioService service;

    public SuperUsuarioAuthController(SuperUsuarioService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (service.autenticar(request.getUsername(), request.getPassword())) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id", request.getUsername(),
                "token", "super:" + System.currentTimeMillis() + ":" + request.getUsername()
            ));
        }
        return ResponseEntity.status(401).body(Map.of(
            "success", false,
            "message", "Credenciales incorrectas"
        ));
    }
}
