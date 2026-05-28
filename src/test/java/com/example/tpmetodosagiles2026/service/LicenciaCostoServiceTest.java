package com.example.tpmetodosagiles2026.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LicenciaCostoServiceTest {

    private final LicenciaCostoService service = new LicenciaCostoService();

    @Test
    void testCalcularCostoTotal_ClaseA_5Anios() {

        double resultado = service.calcularCostoTotal("A", 5);
        // Verificación
        assertEquals(48.0, resultado, "El costo de Clase A por 5 años debe ser 48.0");
    }

    @Test
    void testCalcularCostoTotal_ClaseC_3Anios() {
        // Ejecución: Clase C, 3 años (Costo base 30 + 8 admin = 38)
        double resultado = service.calcularCostoTotal("C", 3);
        // Verificación
        assertEquals(38.0, resultado, "El costo de Clase C por 3 años debe ser 38.0");
    }

    @Test
    void testCalcularCostoTotal_ClaseD_1Anio() {
        // Ejecución: Clase D, 1 año (Costo base 29 + 8 admin = 37)
        double resultado = service.calcularCostoTotal("D", 1);
        // Verificación
        assertEquals(37.0, resultado, "El costo de Clase D por 1 año debe ser 37.0");
    }

    @Test
    void testCalcularCostoTotal_ClaseInvalidaLanzaExcepcion() {
        // Verificamos que falle al pasarle una clase que no existe en el sistema
        assertThrows(IllegalArgumentException.class, () -> service.calcularCostoTotal("Z", 5));
    }

    @Test
    void testCalcularCostoTotal_VigenciaInvalidaLanzaExcepcion() {
        // Verificamos que falle al pasarle años de vigencia incorrectos
        assertThrows(IllegalArgumentException.class, () -> service.calcularCostoTotal("B", 10));
    }
}