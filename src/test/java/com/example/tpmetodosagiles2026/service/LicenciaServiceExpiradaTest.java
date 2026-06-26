package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.repository.LicenciaRepository;

@ExtendWith(MockitoExtension.class)
class LicenciaServiceExpiradaTest {

    @Mock
    private LicenciaRepository repository;

    @Mock
    private LicenciaCostoService costoService;

    @InjectMocks
    private LicenciaService service;

    // Licencia vencida hace 3 años (emitida hace 8 años con vigencia 5)
    private Licencia licenciaVencidaHace3Anios;
    // Licencia vencida hace 1 año (emitida hace 6 años con vigencia 5)
    private Licencia licenciaVencidaHace1Anio;
    // Licencia vigente (emitida hace 1 año con vigencia 5, vence en 4 años)
    private Licencia licenciaVigente;
    // Licencia sin vigencia definida
    private Licencia licenciaSinVigencia;

    @BeforeEach
    void setUp() {
        licenciaVencidaHace3Anios = new Licencia("Ana García", 45, "B", "11111111");
        licenciaVencidaHace3Anios.setId(1L);
        licenciaVencidaHace3Anios.setVigencia(5);
        licenciaVencidaHace3Anios.setFechaEmision(LocalDateTime.now().minusYears(8));
        licenciaVencidaHace3Anios.setUsuarioAdministrativo("admin");

        licenciaVencidaHace1Anio = new Licencia("Carlos López", 30, "B", "22222222");
        licenciaVencidaHace1Anio.setId(2L);
        licenciaVencidaHace1Anio.setVigencia(5);
        licenciaVencidaHace1Anio.setFechaEmision(LocalDateTime.now().minusYears(6));
        licenciaVencidaHace1Anio.setUsuarioAdministrativo("admin");

        licenciaVigente = new Licencia("María Pérez", 25, "B", "33333333");
        licenciaVigente.setId(3L);
        licenciaVigente.setVigencia(5);
        licenciaVigente.setFechaEmision(LocalDateTime.now().minusYears(1));
        licenciaVigente.setUsuarioAdministrativo("admin");

        licenciaSinVigencia = new Licencia("Luis Torres", 40, "A", "44444444");
        licenciaSinVigencia.setId(4L);
        licenciaSinVigencia.setVigencia(null);
        licenciaSinVigencia.setFechaEmision(LocalDateTime.now().minusYears(10));
        licenciaSinVigencia.setUsuarioAdministrativo("admin");
    }

    @Test
    void listarExpiradas_sinFiltros_devuelveTodasLasVencidas() {
        when(repository.findAll()).thenReturn(
                List.of(licenciaVencidaHace3Anios, licenciaVencidaHace1Anio, licenciaVigente, licenciaSinVigencia));

        List<Licencia> resultado = service.listarExpiradas(null, null);

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(licenciaVencidaHace3Anios));
        assertTrue(resultado.contains(licenciaVencidaHace1Anio));
    }

    @Test
    void listarExpiradas_conRangoFechas_devuelveSoloLasDelRango() {
        when(repository.findAll()).thenReturn(
                List.of(licenciaVencidaHace3Anios, licenciaVencidaHace1Anio, licenciaVigente));

        // Rango que solo incluye licencias vencidas hace 1 año (±6 meses de margen)
        LocalDate desde = LocalDate.now().minusYears(2);
        LocalDate hasta = LocalDate.now();

        List<Licencia> resultado = service.listarExpiradas(desde, hasta);

        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(licenciaVencidaHace1Anio));
    }

    @Test
    void listarExpiradas_soloDesde_devuelveVencidasAPartirDeLaFecha() {
        when(repository.findAll()).thenReturn(
                List.of(licenciaVencidaHace3Anios, licenciaVencidaHace1Anio));

        // "Desde" hace 2 años → solo incluye la vencida hace 1 año
        LocalDate desde = LocalDate.now().minusYears(2);

        List<Licencia> resultado = service.listarExpiradas(desde, null);

        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(licenciaVencidaHace1Anio));
    }

    @Test
    void listarExpiradas_soloHasta_devuelveVencidasHastaLaFecha() {
        when(repository.findAll()).thenReturn(
                List.of(licenciaVencidaHace3Anios, licenciaVencidaHace1Anio));

        // "Hasta" hace 2 años → solo incluye la vencida hace 3 años
        LocalDate hasta = LocalDate.now().minusYears(2);

        List<Licencia> resultado = service.listarExpiradas(null, hasta);

        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(licenciaVencidaHace3Anios));
    }

    @Test
    void listarExpiradas_licenciaSinVigencia_excluida() {
        when(repository.findAll()).thenReturn(List.of(licenciaSinVigencia));

        List<Licencia> resultado = service.listarExpiradas(null, null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarExpiradas_licenciaVigente_excluida() {
        when(repository.findAll()).thenReturn(List.of(licenciaVigente));

        List<Licencia> resultado = service.listarExpiradas(null, null);

        assertTrue(resultado.isEmpty());
    }
}
