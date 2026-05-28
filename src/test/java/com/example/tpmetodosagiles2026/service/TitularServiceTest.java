package com.example.tpmetodosagiles2026.service;


import com.example.tpmetodosagiles2026.entity.Direccion;
import com.example.tpmetodosagiles2026.entity.Titular;
import com.example.tpmetodosagiles2026.repository.TitularRepository;

import org.mockito.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.tpmetodosagiles2026.service.TitularService;

@ExtendWith(MockitoExtension.class)
class TitularServiceTest {

    @Mock
    private TitularRepository titularRepository;

    @InjectMocks
    private TitularService titularService;

    private Titular titularValido;

    @BeforeEach
    void setUp() {
        // Configuramos un titular válido que usaremos como base para los tests
        Direccion direccion = new Direccion();
        direccion.setCalle("San Martin");
        direccion.setNro(1234);
        direccion.setCodigoPostal("3000"); // 4 dígitos
        direccion.setLocalidad("Santa Fe");
        direccion.setProvincia("Santa Fe");

        titularValido = new Titular();
        titularValido.setNombre("Juan");
        titularValido.setApellido("Perez");
        titularValido.setNumeroDocumento("12345678"); // 8 dígitos
        titularValido.setFechaNacimiento(LocalDate.now().minusYears(25)); // 25 años (Mayor de 18)
        titularValido.setDireccion(direccion);
    }

    @Test
    void debeRegistrarTitularExitosamente() {
        when(titularRepository.save(any(Titular.class))).thenReturn(titularValido);

        Titular resultado = titularService.registrarTitular(titularValido);

        assertNotNull(resultado);
        verify(titularRepository, times(1)).save(titularValido);
    }

    @Test
    void debeFallarSiDocumentoNoTiene8Digitos() {
        titularValido.setNumeroDocumento("12345"); // Menos de 8

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
                () -> titularService.registrarTitular(titularValido));
        
        assertEquals("El número de documento debe contener exactamente 8 dígitos.", ex.getMessage());
        verify(titularRepository, never()).save(any());
    }

    @Test
    void debeFallarSiDocumentoEsNulo() {
        titularValido.setNumeroDocumento(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
                () -> titularService.registrarTitular(titularValido));
        
        assertEquals("El número de documento debe contener exactamente 8 dígitos.", ex.getMessage());
    }

    @Test
    void debeFallarSiEsMenorDe18Anios() {
        titularValido.setFechaNacimiento(LocalDate.now().minusYears(17)); // 17 años

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
                () -> titularService.registrarTitular(titularValido));
        
        assertEquals("El titular debe ser mayor de 18 años.", ex.getMessage());
        verify(titularRepository, never()).save(any());
    }

    @Test
    void debeFallarSiFechaDeNacimientoEsNula() {
        titularValido.setFechaNacimiento(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
                () -> titularService.registrarTitular(titularValido));
        
        assertEquals("La fecha de nacimiento es obligatoria.", ex.getMessage());
    }

    @Test
    void debeFallarSiCodigoPostalNoTiene4Digitos() {
        titularValido.getDireccion().setCodigoPostal("30005"); // 5 dígitos

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
                () -> titularService.registrarTitular(titularValido));
        
        assertEquals("El código postal debe contener exactamente 4 dígitos.", ex.getMessage());
        verify(titularRepository, never()).save(any());
    }

    @Test
    void debeFallarSiDireccionEsNula() {
        titularValido.setDireccion(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
                () -> titularService.registrarTitular(titularValido));
        
        assertEquals("El código postal debe contener exactamente 4 dígitos.", ex.getMessage());
    }
}