package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.tpmetodosagiles2026.dto.EmitirLicenciaDTO;
import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.repository.LicenciaRepository;

@ExtendWith(MockitoExtension.class)
class LicenciaServiceVigenteUnicaTest {

    @Mock
    private LicenciaRepository repository;

    @Mock
    private LicenciaCostoService costoService;

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

        when(repository.findByNumeroDocumentoAndClaseAndVigenteTrue("12345678", "B"))
                .thenReturn(Optional.of(anterior));
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
        when(repository.findByNumeroDocumentoAndClaseAndVigenteTrue("12345678", "B"))
                .thenReturn(Optional.empty());
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of());
        when(costoService.calcularCostoTotal(anyString(), anyInt())).thenReturn(40.0);
        when(repository.save(any(Licencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Licencia nueva = service.emitir(dtoClase("B"));

        assertTrue(nueva.getVigente());
    }

    @Test
    void emitir_otraClase_noTocaLaVigenteDeClaseDistinta() {
        // Existe vigente clase B; se emite clase A -> no se archiva la B.
        when(repository.findByNumeroDocumentoAndClaseAndVigenteTrue("12345678", "A"))
                .thenReturn(Optional.empty());
        when(repository.findByNumeroDocumento("12345678")).thenReturn(List.of(new Licencia()));
        when(costoService.calcularCostoTotal(anyString(), anyInt())).thenReturn(40.0);
        when(repository.save(any(Licencia.class))).thenAnswer(inv -> inv.getArgument(0));

        service.emitir(dtoClase("A"));

        // Nunca se consultó por la clase B al emitir una clase A.
        verify(repository, never()).findByNumeroDocumentoAndClaseAndVigenteTrue(eq("12345678"), eq("B"));
    }
}
