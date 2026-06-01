package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.tpmetodosagiles2026.dto.RenovarLicenciaDTO;
import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.repository.LicenciaRepository;

@ExtendWith(MockitoExtension.class)
class LicenciaServiceRenovarTest {

    @Mock
    private LicenciaRepository repository;

    @Mock
    private LicenciaCostoService costoService;

    @InjectMocks
    private LicenciaService service;

    private Licencia licenciaBase;

    @BeforeEach
    void setUp() {
        licenciaBase = new Licencia();
        licenciaBase.setId(1L);
        licenciaBase.setTitular("Juan Pérez");
        licenciaBase.setNumeroDocumento("12345678");
        licenciaBase.setFechaNacimiento(LocalDate.of(1989, 3, 15)); 
        licenciaBase.setEdad(35);
        licenciaBase.setClase("B");
        licenciaBase.setVigencia(5);
        // Fecha de emisión hace 4 años y 8 meses -> vence en 4 meses (dentro de
        // ventana)
        licenciaBase.setFechaEmision(LocalDateTime.now().minusYears(4).minusMonths(8));
        licenciaBase.setCosto(1500.0);
    }

    @Test
    void renovarPorVencimiento_dentroDeVentana_renovaCorrectamente() {
        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);
        dto.setVigencia(5);
        dto.setRenovarPorVencimiento(true);

        when(repository.findById(1L)).thenReturn(Optional.of(licenciaBase));
        when(costoService.calcularCostoTotal("B", 5)).thenReturn(1500.0);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Licencia resultado = service.renovar(dto);

        assertNotNull(resultado);
        assertEquals(5, resultado.getVigencia());
        assertEquals(1500.0, resultado.getCosto());
        verify(repository).save(licenciaBase);
    }

    @Test
    void renovarPorVencimiento_fueraDeVentana_lanzaExcepcion() {
        licenciaBase.setFechaEmision(LocalDateTime.now().minusYears(1));

        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);
        dto.setVigencia(5);
        dto.setRenovarPorVencimiento(true);

        when(repository.findById(1L)).thenReturn(Optional.of(licenciaBase));

        assertThrows(IllegalArgumentException.class, () -> service.renovar(dto));
    }

    @Test
    void renovarPorVencimiento_licenciaYaVencida_lanzaExcepcion() {
        licenciaBase.setFechaEmision(LocalDateTime.now().minusYears(6));

        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);
        dto.setVigencia(5);
        dto.setRenovarPorVencimiento(true);

        when(repository.findById(1L)).thenReturn(Optional.of(licenciaBase));

        assertThrows(IllegalArgumentException.class, () -> service.renovar(dto));
    }

    @Test
    void renovarPorVencimiento_licenciaNoExiste_lanzaExcepcion() {
        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(99L);
        dto.setRenovarPorVencimiento(true);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.renovar(dto));

        assertTrue(ex.getMessage().contains("No existe una licencia con id=99"));
    }

    // renovacion por modificacion de datos

    @Test
    void renovarPorModificacion_cambiaTitular_guardaCorrectamente() {
        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);
        dto.setTitular("Juan Carlos Pérez");
        dto.setVigencia(5);
        dto.setRenovarPorVencimiento(false);

        when(repository.findById(1L)).thenReturn(Optional.of(licenciaBase));
        when(costoService.calcularCostoTotal("B", 5)).thenReturn(1500.0);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Licencia resultado = service.renovar(dto);

        assertEquals("Juan Carlos Pérez", resultado.getTitular());
        verify(repository).save(licenciaBase);
    }

    @Test
    void renovarPorModificacion_cambiaFechaNacimiento_actualizaEdad() {
        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);
        dto.setFechaNacimiento(LocalDate.of(1990, 6, 20));
        dto.setVigencia(5);
        dto.setRenovarPorVencimiento(false);

        when(repository.findById(1L)).thenReturn(Optional.of(licenciaBase));
        when(costoService.calcularCostoTotal("B", 5)).thenReturn(1500.0);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Licencia resultado = service.renovar(dto);

        assertEquals(LocalDate.of(1990, 6, 20), resultado.getFechaNacimiento());
        verify(repository).save(licenciaBase);
    }

    @Test
    void renovarPorModificacion_cambiaObservaciones_guardaCorrectamente() {
        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);
        dto.setObservaciones("Usa lentes correctivos");
        dto.setVigencia(5);
        dto.setRenovarPorVencimiento(false);

        when(repository.findById(1L)).thenReturn(Optional.of(licenciaBase));
        when(costoService.calcularCostoTotal("B", 5)).thenReturn(1500.0);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Licencia resultado = service.renovar(dto);

        assertEquals("Usa lentes correctivos", resultado.getObservaciones());
        verify(repository).save(licenciaBase);
    }

    @Test
    void renovarPorModificacion_licenciaNoExiste_lanzaExcepcion() {
        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(99L);
        dto.setRenovarPorVencimiento(false);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.renovar(dto));

        assertTrue(ex.getMessage().contains("No existe una licencia con id=99"));
    }

    @Test
    void renovarPorModificacion_noRequiereVentanaDeVencimiento() {
        // Licencia emitida hace 1 año por lo que esta fuera del plazo de renovacion,
        // pero modificación no la necesita
        licenciaBase.setFechaEmision(LocalDateTime.now().minusYears(1));

        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);
        dto.setTitular("Nuevo Nombre");
        dto.setVigencia(5);
        dto.setRenovarPorVencimiento(false);

        when(repository.findById(1L)).thenReturn(Optional.of(licenciaBase));
        when(costoService.calcularCostoTotal("B", 5)).thenReturn(1500.0);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // No debe lanzar excepción aunque esté fuera de la ventana
        assertDoesNotThrow(() -> service.renovar(dto));
    }
}