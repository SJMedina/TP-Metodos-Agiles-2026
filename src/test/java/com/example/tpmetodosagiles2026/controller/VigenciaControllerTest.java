package com.example.tpmetodosagiles2026.controller;

import java.time.LocalDate;

import com.example.tpmetodosagiles2026.dto.CalcularVigenciaRequestDTO;
import com.example.tpmetodosagiles2026.dto.CalcularVigenciaResponseDTO;
import com.example.tpmetodosagiles2026.service.VigenciaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VigenciaController Tests")
class VigenciaControllerTest {

    @Mock
    private VigenciaService vigenciaService;

    @InjectMocks
    private VigenciaController vigenciaController;

    private LocalDate fechaNacimientoTest;

    @BeforeEach
    void setUp() {
        fechaNacimientoTest = LocalDate.of(1990, 5, 29);
    }

    @Nested
    @DisplayName("POST /api/vigencia/calcular endpoint tests")
    class CalcularEndpointTests {

        @Test
        @DisplayName("Should return 200 OK for valid request")
        void testCalcularExitoso() {
            CalcularVigenciaRequestDTO solicitud = new CalcularVigenciaRequestDTO(fechaNacimientoTest, true);
            LocalDate fechaVencimientoEsperada = LocalDate.of(2031, 5, 29);

            when(vigenciaService.calcularEdad(eq(fechaNacimientoTest), any(LocalDate.class)))
                .thenReturn(35);
            when(vigenciaService.determinarAnios(35, true))
                .thenReturn(5);
            when(vigenciaService.calcularVencimiento(eq(fechaNacimientoTest), eq(true), any(LocalDate.class)))
                .thenReturn(fechaVencimientoEsperada);

            ResponseEntity<?> respuesta = vigenciaController.calcular(solicitud);

            assertEquals(HttpStatus.OK, respuesta.getStatusCode());
            assertNotNull(respuesta.getBody());
            assertInstanceOf(CalcularVigenciaResponseDTO.class, respuesta.getBody());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when fechaNacimiento is null")
        void testCalcularSinFechaNacimiento() {
            CalcularVigenciaRequestDTO solicitud = new CalcularVigenciaRequestDTO(null, true);

            ResponseEntity<?> respuesta = vigenciaController.calcular(solicitud);

            assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when primeraLicencia is null")
        void testCalcularSinPrimeraLicencia() {
            CalcularVigenciaRequestDTO solicitud = new CalcularVigenciaRequestDTO(fechaNacimientoTest, null);

            ResponseEntity<?> respuesta = vigenciaController.calcular(solicitud);

            assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when fechaNacimiento is in future")
        void testCalcularConFechaFutura() {
            CalcularVigenciaRequestDTO solicitud = new CalcularVigenciaRequestDTO(
                LocalDate.now().plusYears(1), true
            );

            ResponseEntity<?> respuesta = vigenciaController.calcular(solicitud);

            assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        }

        @Test
        @DisplayName("Should calculate for person under 21 with first license")
        void testCalcularMenor21Primera() {
            LocalDate fechaNacimiento = LocalDate.of(2010, 5, 29);
            CalcularVigenciaRequestDTO solicitud = new CalcularVigenciaRequestDTO(fechaNacimiento, true);
            LocalDate fechaVencimientoEsperada = LocalDate.of(2027, 5, 29);

            when(vigenciaService.calcularEdad(eq(fechaNacimiento), any(LocalDate.class)))
                .thenReturn(15);
            when(vigenciaService.determinarAnios(15, true))
                .thenReturn(1);
            when(vigenciaService.calcularVencimiento(eq(fechaNacimiento), eq(true), any(LocalDate.class)))
                .thenReturn(fechaVencimientoEsperada);

            ResponseEntity<?> respuesta = vigenciaController.calcular(solicitud);

            assertEquals(HttpStatus.OK, respuesta.getStatusCode());
            CalcularVigenciaResponseDTO respuestaDTO = (CalcularVigenciaResponseDTO) respuesta.getBody();
            assertEquals(1, respuestaDTO.getAnios());
            assertEquals(fechaVencimientoEsperada, respuestaDTO.getFechaVencimiento());
        }

        @Test
        @DisplayName("Should return response with fecha inicio as today")
        void testRespuestaConFechaDeHoy() {
            CalcularVigenciaRequestDTO solicitud = new CalcularVigenciaRequestDTO(fechaNacimientoTest, true);
            LocalDate hoy = LocalDate.now();

            when(vigenciaService.calcularEdad(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(35);
            when(vigenciaService.determinarAnios(35, true))
                .thenReturn(5);
            when(vigenciaService.calcularVencimiento(any(LocalDate.class), eq(true), any(LocalDate.class)))
                .thenReturn(LocalDate.of(2031, 5, 29));

            ResponseEntity<?> respuesta = vigenciaController.calcular(solicitud);

            CalcularVigenciaResponseDTO respuestaDTO = (CalcularVigenciaResponseDTO) respuesta.getBody();
            assertEquals(hoy, respuestaDTO.getFechaInicio());
        }

        @Test
        @DisplayName("Should include age in response")
        void testRespuestaIncluyeEdad() {
            CalcularVigenciaRequestDTO solicitud = new CalcularVigenciaRequestDTO(fechaNacimientoTest, true);

            when(vigenciaService.calcularEdad(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(35);
            when(vigenciaService.determinarAnios(35, true))
                .thenReturn(5);
            when(vigenciaService.calcularVencimiento(any(LocalDate.class), eq(true), any(LocalDate.class)))
                .thenReturn(LocalDate.of(2031, 5, 29));

            ResponseEntity<?> respuesta = vigenciaController.calcular(solicitud);

            CalcularVigenciaResponseDTO respuestaDTO = (CalcularVigenciaResponseDTO) respuesta.getBody();
            assertEquals(35, respuestaDTO.getEdad());
        }
    }

    @Nested
    @DisplayName("HTTP Status Code Tests")
    class HttpStatusTests {

        @Test
        @DisplayName("Should return HTTP 200 OK on successful calculation")
        void testHttpStatus200() {
            CalcularVigenciaRequestDTO solicitud = new CalcularVigenciaRequestDTO(fechaNacimientoTest, true);

            when(vigenciaService.calcularEdad(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(35);
            when(vigenciaService.determinarAnios(35, true))
                .thenReturn(5);
            when(vigenciaService.calcularVencimiento(any(LocalDate.class), eq(true), any(LocalDate.class)))
                .thenReturn(LocalDate.of(2031, 5, 29));

            ResponseEntity<?> respuesta = vigenciaController.calcular(solicitud);

            assertEquals(HttpStatus.OK, respuesta.getStatusCode());
            assertEquals(200, respuesta.getStatusCode().value());
        }
    }
}

