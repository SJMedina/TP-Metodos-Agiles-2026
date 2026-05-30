package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VigenciaService Tests")
class VigenciaServiceTest {

    private VigenciaService vigenciaService;

    @BeforeEach
    void setUp() {
        vigenciaService = new VigenciaService();
    }

    @Nested
    @DisplayName("determinarAnios - tests for age-based vigencia determination")
    class DeterminarAniosTests {

        @Test
        @DisplayName("Should return 1 year for age < 21 and first license")
        void testMenor21PrimeraLicencia() {
            int resultado = vigenciaService.determinarAnios(20, true);
            assertEquals(1, resultado);
        }

        @Test
        @DisplayName("Should return 3 years for age < 21 and not first license")
        void testMenor21NoprimeraLicencia() {
            int resultado = vigenciaService.determinarAnios(20, false);
            assertEquals(3, resultado);
        }

        @Test
        @DisplayName("Should return 5 years for age 21")
        void testEdad21() {
            int resultado = vigenciaService.determinarAnios(21, true);
            assertEquals(5, resultado);
        }

        @Test
        @DisplayName("Should return 5 years for age 46")
        void testEdad46() {
            int resultado = vigenciaService.determinarAnios(46, false);
            assertEquals(5, resultado);
        }

        @Test
        @DisplayName("Should return 5 years for age 35")
        void testEdad35() {
            int resultado = vigenciaService.determinarAnios(35, false);
            assertEquals(5, resultado);
        }

        @Test
        @DisplayName("Should return 4 years for age 47")
        void testEdad47() {
            int resultado = vigenciaService.determinarAnios(47, false);
            assertEquals(4, resultado);
        }

        @Test
        @DisplayName("Should return 4 years for age 60")
        void testEdad60() {
            int resultado = vigenciaService.determinarAnios(60, true);
            assertEquals(4, resultado);
        }

        @Test
        @DisplayName("Should return 3 years for age 61")
        void testEdad61() {
            int resultado = vigenciaService.determinarAnios(61, true);
            assertEquals(3, resultado);
        }

        @Test
        @DisplayName("Should return 3 years for age 70")
        void testEdad70() {
            int resultado = vigenciaService.determinarAnios(70, false);
            assertEquals(3, resultado);
        }

        @Test
        @DisplayName("Should return 1 year for age 71")
        void testEdad71() {
            int resultado = vigenciaService.determinarAnios(71, true);
            assertEquals(1, resultado);
        }

        @Test
        @DisplayName("Should return 1 year for age 100")
        void testEdad100() {
            int resultado = vigenciaService.determinarAnios(100, false);
            assertEquals(1, resultado);
        }
    }

    @Nested
    @DisplayName("calcularEdad - tests for age calculation")
    class CalcularEdadTests {

        @Test
        @DisplayName("Should calculate exact age correctly")
        void testCalcularEdadExacta() {
            LocalDate fechaNacimiento = LocalDate.of(1990, 5, 29);
            LocalDate enFecha = LocalDate.of(2026, 5, 29);
            int edad = vigenciaService.calcularEdad(fechaNacimiento, enFecha);
            assertEquals(36, edad);
        }

        @Test
        @DisplayName("Should calculate age before birthday in same year")
        void testCalcularEdadAntesCumpleaños() {
            LocalDate fechaNacimiento = LocalDate.of(1990, 5, 29);
            LocalDate enFecha = LocalDate.of(2026, 5, 28);
            int edad = vigenciaService.calcularEdad(fechaNacimiento, enFecha);
            assertEquals(35, edad);
        }

        @Test
        @DisplayName("Should calculate age after birthday in same year")
        void testCalcularEdadDespuesCumpleaños() {
            LocalDate fechaNacimiento = LocalDate.of(1990, 5, 29);
            LocalDate enFecha = LocalDate.of(2026, 5, 30);
            int edad = vigenciaService.calcularEdad(fechaNacimiento, enFecha);
            assertEquals(36, edad);
        }

        @Test
        @DisplayName("Should calculate age 0 when born on same date")
        void testCalcularEdad0() {
            LocalDate fechaNacimiento = LocalDate.of(2026, 5, 29);
            LocalDate enFecha = LocalDate.of(2026, 5, 29);
            int edad = vigenciaService.calcularEdad(fechaNacimiento, enFecha);
            assertEquals(0, edad);
        }

        @Test
        @DisplayName("Should handle leap year dates correctly")
        void testCalcularEdadAnioBisiesto() {
            LocalDate fechaNacimiento = LocalDate.of(2000, 2, 29);
            LocalDate enFecha = LocalDate.of(2026, 2, 28);
            int edad = vigenciaService.calcularEdad(fechaNacimiento, enFecha);
            assertEquals(25, edad);
        }
    }

    @Nested
    @DisplayName("calcularVencimiento - tests for expiry date calculation")
    class CalcularVencimientoTests {

        @Test
        @DisplayName("Should throw exception when fechaNacimiento is null")
        void testNullFechaNacimiento() {
            LocalDate fechaInicio = LocalDate.now();
            assertThrows(IllegalArgumentException.class, () -> {
                vigenciaService.calcularVencimiento(null, true, fechaInicio);
            });
        }

        @Test
        @DisplayName("Should throw exception when fechaInicio is null")
        void testNullFechaInicio() {
            LocalDate fechaNacimiento = LocalDate.of(1990, 5, 29);
            assertThrows(IllegalArgumentException.class, () -> {
                vigenciaService.calcularVencimiento(fechaNacimiento, true, null);
            });
        }

        @Test
        @DisplayName("Should calculate expiry for person under 21 with first license")
        void testVencimientoMenor21Primera() {
            LocalDate fechaNacimiento = LocalDate.of(2010, 5, 29);
            LocalDate fechaInicio = LocalDate.of(2026, 5, 29);
            LocalDate vencimiento = vigenciaService.calcularVencimiento(fechaNacimiento, true, fechaInicio);

            assertEquals(2027, vencimiento.getYear());
            assertEquals(5, vencimiento.getMonthValue());
            assertEquals(29, vencimiento.getDayOfMonth());
        }

        @Test
        @DisplayName("Should calculate expiry for person under 21 without first license")
        void testVencimientoMenor21Renovacion() {
            LocalDate fechaNacimiento = LocalDate.of(2010, 5, 29);
            LocalDate fechaInicio = LocalDate.of(2026, 5, 29);
            LocalDate vencimiento = vigenciaService.calcularVencimiento(fechaNacimiento, false, fechaInicio);

            assertEquals(2029, vencimiento.getYear());
            assertEquals(5, vencimiento.getMonthValue());
            assertEquals(29, vencimiento.getDayOfMonth());
        }

        @Test
        @DisplayName("Should calculate expiry for person aged 21-46")
        void testVencimiento21A46() {
            LocalDate fechaNacimiento = LocalDate.of(1980, 5, 29);
            LocalDate fechaInicio = LocalDate.of(2026, 5, 29);
            LocalDate vencimiento = vigenciaService.calcularVencimiento(fechaNacimiento, true, fechaInicio);

            assertEquals(2031, vencimiento.getYear());
            assertEquals(5, vencimiento.getMonthValue());
            assertEquals(29, vencimiento.getDayOfMonth());
        }

        @Test
        @DisplayName("Should maintain birth day and month in expiry date")
        void testVencimientoMantieneDiaYMes() {
            LocalDate fechaNacimiento = LocalDate.of(1990, 12, 25);
            LocalDate fechaInicio = LocalDate.of(2026, 1, 1);
            LocalDate vencimiento = vigenciaService.calcularVencimiento(fechaNacimiento, true, fechaInicio);

            assertEquals(12, vencimiento.getMonthValue());
            assertEquals(25, vencimiento.getDayOfMonth());
        }

        @Test
        @DisplayName("Should handle leap year birth date (Feb 29)")
        void testVencimientoFeb29() {
            LocalDate fechaNacimiento = LocalDate.of(2000, 2, 29);
            LocalDate fechaInicio = LocalDate.of(2026, 2, 1);
            LocalDate vencimiento = vigenciaService.calcularVencimiento(fechaNacimiento, true, fechaInicio);

            assertEquals(2, vencimiento.getMonthValue());
        }

        @Test
        @DisplayName("Should ensure expiry date is not before start date")
        void testVencimientoNuncaAntesDeFechaInicio() {
            LocalDate fechaNacimiento = LocalDate.of(1990, 3, 15);
            LocalDate fechaInicio = LocalDate.of(2026, 6, 1);
            LocalDate vencimiento = vigenciaService.calcularVencimiento(fechaNacimiento, false, fechaInicio);

            assertTrue(!vencimiento.isBefore(fechaInicio));
        }
    }
}


