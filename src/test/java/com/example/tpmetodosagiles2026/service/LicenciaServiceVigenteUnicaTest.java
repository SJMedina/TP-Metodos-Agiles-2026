package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class LicenciaServiceVigenteUnicaTest {

    @Mock
    private LicenciaRepository repository;

    @Mock
    private LicenciaCostoService costoService;

    @Mock
    private TitularRepository titularRepository;

    @InjectMocks
    private LicenciaService service;

    private EmitirLicenciaDTO dtoClase(String clase) {
        LocalDate fechaNacimiento = LocalDate.now().minusYears(30).minusDays(1);
        EmitirLicenciaDTO dto = new EmitirLicenciaDTO();
        dto.setTitular("Juan Pérez");
        dto.setNumeroDocumento("12345678");
        dto.setClase(clase);
        dto.setEdad(Period.between(fechaNacimiento, LocalDate.now()).getYears());
        dto.setFechaNacimiento(fechaNacimiento);
        return dto;
    }

    @Test
    void emitir_conVigentePreviaMismaClase_archivaLaAnterior() {
        Licencia anterior = new Licencia();
        anterior.setId(99L);
        anterior.setNumeroDocumento("12345678");
        anterior.setClase("B");
        anterior.setVigente(true);
        anterior.setFechaEmision(LocalDateTime.now().minusYears(2));
        anterior.setVigencia(5);

        when(titularRepository.existsByNumeroDocumento("12345678")).thenReturn(true);
        when(repository.findByNumeroDocumentoAndVigenteTrue("12345678"))
                .thenReturn(List.of(anterior));
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of(anterior));
        when(costoService.calcularCostoTotal(anyString(), anyInt())).thenReturn(40.0);
        when(repository.save(any(Licencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Licencia nueva = service.emitir(dtoClase("B"));

        // La anterior queda archivada (historial), la nueva vigente.
        assertFalse(anterior.getVigente(), "La licencia previa debe quedar como no vigente");
        assertTrue(nueva.getVigente(), "La nueva licencia debe quedar vigente");
        // Se guardó la anterior (archivada) y la nueva.
        verify(repository).save(anterior);
    }

    @Test
    void emitir_sinVigentePrevia_noArchivaNada() {
        when(titularRepository.existsByNumeroDocumento("12345678")).thenReturn(true);
        when(repository.findByNumeroDocumentoAndVigenteTrue("12345678"))
                .thenReturn(List.of());
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of());
        when(costoService.calcularCostoTotal(anyString(), anyInt())).thenReturn(40.0);
        when(repository.save(any(Licencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Licencia nueva = service.emitir(dtoClase("B"));

        assertTrue(nueva.getVigente());
    }

    @Test
    void emitir_conVigenteDeOtraClase_archivaLaAnterior() {
        // Una sola licencia vigente por persona: existe vigente clase B; al emitir clase A
        // (p. ej. tras modificar el titular) la B debe quedar archivada.
        Licencia anteriorB = new Licencia();
        anteriorB.setId(99L);
        anteriorB.setNumeroDocumento("12345678");
        anteriorB.setClase("B");
        anteriorB.setVigente(true);

        when(titularRepository.existsByNumeroDocumento("12345678")).thenReturn(true);
        when(repository.findByNumeroDocumentoAndVigenteTrue("12345678"))
                .thenReturn(List.of(anteriorB));
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of(anteriorB));
        when(costoService.calcularCostoTotal(anyString(), anyInt())).thenReturn(40.0);
        when(repository.save(any(Licencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Licencia nueva = service.emitir(dtoClase("A"));

        assertFalse(anteriorB.getVigente(), "La licencia previa de otra clase debe quedar archivada");
        assertTrue(nueva.getVigente(), "La nueva licencia debe quedar vigente");
        verify(repository).save(anteriorB);
    }
}
