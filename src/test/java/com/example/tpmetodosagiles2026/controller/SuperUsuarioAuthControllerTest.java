package com.example.tpmetodosagiles2026.controller;

import com.example.tpmetodosagiles2026.auth.LoginRequest;
import com.example.tpmetodosagiles2026.auth.SuperUsuarioAuthController;
import com.example.tpmetodosagiles2026.service.SuperUsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuperUsuarioAuthControllerTest {

    @Mock
    private SuperUsuarioService service;

    @InjectMocks
    private SuperUsuarioAuthController controller;

    @Test
    void login_credencialesCorrectas_devuelve200ConToken() {
        when(service.autenticar("superadmin", "super1234")).thenReturn(true);

        ResponseEntity<?> response = controller.login(loginRequest("superadmin", "super1234"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(true, body.get("success"));
        assertEquals("superadmin", body.get("id"));
        assertTrue(body.get("token").toString().startsWith("super:"));
    }

    @Test
    void login_credencialesIncorrectas_devuelve401() {
        when(service.autenticar("superadmin", "wrong")).thenReturn(false);

        ResponseEntity<?> response = controller.login(loginRequest("superadmin", "wrong"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(false, body.get("success"));
    }

    @Test
    void login_idInexistente_devuelve401() {
        when(service.autenticar("fantasma", "pass")).thenReturn(false);

        ResponseEntity<?> response = controller.login(loginRequest("fantasma", "pass"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private LoginRequest loginRequest(String username, String password) {
        LoginRequest req = new LoginRequest();
        req.setUsername(username);
        req.setPassword(password);
        return req;
    }
}
