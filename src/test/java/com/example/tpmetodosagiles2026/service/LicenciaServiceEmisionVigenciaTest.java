package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.tpmetodosagiles2026.dto.EmitirLicenciaDTO;
import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.repository.LicenciaRepository;
import com.example.tpmetodosagiles2026.repository.TitularRepository;

@ExtendWith(MockitoExtension.class)
class LicenciaServiceEmisionVigenciaTest {

    @Mock
    private LicenciaRepository repository;

    @Mock
    private LicenciaCostoService costoService;

    @Mock
    private TitularRepository titularRepository;

    @InjectMocks
    private LicenciaService service;

    /**
     * Construye un DTO de emisión válido (clase B, no profesional) con la edad
     * derivada de la fecha de nacimiento indicada para pasar las validaciones.
     */
    private EmitirLicenciaDTO dtoConFechaNacimiento(LocalDate fechaNacimiento) {
        int edad = java.time.Period.between(fechaNacimiento, LocalDate.now()).getYears();
        EmitirLicenciaDTO dto = new EmitirLicenciaDTO();
        dto.setTitular("Juan Pérez");
        dto.setNumeroDocumento("12345678");
        dto.setClase("B");
        dto.setEdad(edad);
        dto.setFechaNacimiento(fechaNacimiento);
        return dto;
    }

    /** Fecha de nacimiento para una edad exacta (cumpleaños fue ayer). */
    private LocalDate nacimientoParaEdad(int edad) {
        return LocalDate.now().minusYears(edad).minusDays(1);
    }

    private void stubGuardado() {
        when(titularRepository.existsByNumeroDocumento("12345678")).thenReturn(true);
        when(costoService.calcularCostoTotal(anyString(), anyInt())).thenReturn(40.0);
        when(repository.save(any(Licencia.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Licencia emitirYCapturar(EmitirLicenciaDTO dto) {
        stubGuardado();
        service.emitir(dto);
        ArgumentCaptor<Licencia> captor = ArgumentCaptor.forClass(Licencia.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void menorDe21_primeraVez_vigencia1() {
        EmitirLicenciaDTO dto = dtoConFechaNacimiento(nacimientoParaEdad(18));
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of());

        Licencia guardada = emitirYCapturar(dto);

        assertEquals(1, guardada.getVigencia());
    }

    @Test
    void menorDe21_conLicenciaPrevia_vigencia3() {
        EmitirLicenciaDTO dto = dtoConFechaNacimiento(nacimientoParaEdad(18));
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of(new Licencia()));

        Licencia guardada = emitirYCapturar(dto);

        assertEquals(3, guardada.getVigencia());
    }

    @Test
    void edad30_vigencia5() {
        EmitirLicenciaDTO dto = dtoConFechaNacimiento(nacimientoParaEdad(30));
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of());

        Licencia guardada = emitirYCapturar(dto);

        assertEquals(5, guardada.getVigencia());
    }

    @Test
    void edad50_vigencia4() {
        EmitirLicenciaDTO dto = dtoConFechaNacimiento(nacimientoParaEdad(50));
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of());

        Licencia guardada = emitirYCapturar(dto);

        assertEquals(4, guardada.getVigencia());
    }

    @Test
    void edad65_vigencia3() {
        EmitirLicenciaDTO dto = dtoConFechaNacimiento(nacimientoParaEdad(65));
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of());

        Licencia guardada = emitirYCapturar(dto);

        assertEquals(3, guardada.getVigencia());
    }

    @Test
    void mayorDe70_vigencia1() {
        EmitirLicenciaDTO dto = dtoConFechaNacimiento(nacimientoParaEdad(75));
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of());

        Licencia guardada = emitirYCapturar(dto);

        assertEquals(1, guardada.getVigencia());
    }

    @Test
    void fechaVencimiento_coincideDiaYMesConNacimiento() {
        LocalDate nacimiento = LocalDate.of(1990, 3, 15);
        EmitirLicenciaDTO dto = dtoConFechaNacimiento(nacimiento);
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of());

        Licencia guardada = emitirYCapturar(dto);
        LocalDate venc = guardada.getFechaVencimiento();

        // El día y mes coinciden con la fecha de nacimiento.
        assertEquals(nacimiento.getDayOfMonth(), venc.getDayOfMonth());
        assertEquals(nacimiento.getMonth(), venc.getMonth());

        // La vigencia se cuenta completa desde hoy: el vencimiento es el primer
        // cumpleaños en o posterior a (hoy + vigencia años).
        LocalDate base = LocalDate.now().plusYears(guardada.getVigencia());
        assertFalse(venc.isBefore(base), "El vencimiento debe garantizar la vigencia completa desde hoy");
        assertTrue(venc.isBefore(base.plusYears(1)), "El vencimiento no debe exceder un año extra sobre la vigencia");
    }
}
