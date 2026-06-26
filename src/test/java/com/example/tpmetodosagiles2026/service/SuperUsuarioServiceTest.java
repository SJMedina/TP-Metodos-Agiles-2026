package com.example.tpmetodosagiles2026.service;

import com.example.tpmetodosagiles2026.model.SuperUsuario;
import com.example.tpmetodosagiles2026.repository.SuperUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuperUsuarioServiceTest {

    @Mock
    private SuperUsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SuperUsuarioService service;

    private static final PasswordEncoder REAL_ENCODER = new BCryptPasswordEncoder();

    @Test
    void autenticar_credencialesCorrectas_devuelveTrue() {
        String rawPassword = "super1234";
        String hash = REAL_ENCODER.encode(rawPassword);
        SuperUsuario superUsuario = new SuperUsuario("superadmin", hash);

        when(repository.findById("superadmin")).thenReturn(Optional.of(superUsuario));
        when(passwordEncoder.matches(rawPassword, hash)).thenReturn(true);

        assertTrue(service.autenticar("superadmin", rawPassword));
    }

    @Test
    void autenticar_passwordIncorrecto_devuelveFalse() {
        String hash = REAL_ENCODER.encode("super1234");
        SuperUsuario superUsuario = new SuperUsuario("superadmin", hash);

        when(repository.findById("superadmin")).thenReturn(Optional.of(superUsuario));
        when(passwordEncoder.matches("wrongpass", hash)).thenReturn(false);

        assertFalse(service.autenticar("superadmin", "wrongpass"));
    }

    @Test
    void autenticar_idInexistente_devuelveFalse() {
        when(repository.findById("noexiste")).thenReturn(Optional.empty());

        assertFalse(service.autenticar("noexiste", "cualquierpass"));
    }
}
