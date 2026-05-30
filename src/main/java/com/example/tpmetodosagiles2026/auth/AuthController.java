package com.example.tpmetodosagiles2026.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            String username = authentication.getName();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("username", username);
            response.put("token", "Bearer " + generateSimpleToken(username));
            response.put("roles", authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Usuario o contraseña incorrectos");
            return ResponseEntity.badRequest().body(error);
        } catch (AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Error de autenticación");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Sesión cerrada correctamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate() {
        Map<String, String> response = new HashMap<>();
        response.put("valid", "true");
        return ResponseEntity.ok(response);
    }

    private String generateSimpleToken(String username) {
        // En producción, usar JWT. Por ahora, un token simple.
        return System.currentTimeMillis() + ":" + username;
    }
}
