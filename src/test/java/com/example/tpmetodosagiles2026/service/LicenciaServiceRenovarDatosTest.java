package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
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

import com.example.tpmetodosagiles2026.dto.RenovarLicenciaDTO;
import com.example.tpmetodosagiles2026.model.FactorRH;
import com.example.tpmetodosagiles2026.model.GrupoSanguineo;
import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.repository.LicenciaRepository;

@ExtendWith(MockitoExtension.class)
class LicenciaServiceRenovarDatosTest {

    @Mock
    private LicenciaRepository repository;

    @Mock
    private LicenciaCostoService costoService;

    @InjectMocks
    private LicenciaService service;

    private Licencia actual;

    @BeforeEach
    void setUp() {
        actual = new Licencia();
        actual.setId(1L);
        actual.setTitular("Juan Pérez");
        actual.setNumeroDocumento("12345678");
        actual.setClase("B");
        actual.setEdad(40);
        actual.setFechaNacimiento(LocalDate.of(1986, 3, 15));
        actual.setVigencia(5);
        actual.setVigente(true);
        actual.setGrupoSanguineo(GrupoSanguineo.A);
        actual.setFactorRH(FactorRH.POSITIVO);
        actual.setDonanteOrganos(false);
        // Emitida hace 2 años -> fuera de la ventana de renovación por vencimiento
        actual.setFechaEmision(LocalDateTime.now().minusYears(2));
    }

    @Test
    void renovarConDatos_archivaLaActualYCreaNuevaConDatosActualizados() {
        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);
        dto.setTitular("García Juan");
        dto.setGrupoSanguineo("O");
        dto.setFactorRH("NEGATIVO");
        dto.setDonanteOrganos(true);

        when(repository.findById(1L)).thenReturn(Optional.of(actual));
        when(costoService.calcularCostoTotal(anyString(), anyInt())).thenReturn(48.0);
        when(repository.save(any(Licencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Licencia nueva = service.renovarConDatosActualizados(dto);

        // La actual queda archivada
        assertFalse(actual.getVigente());
        // La nueva queda vigente con los datos actualizados
        assertTrue(nueva.getVigente());
        assertEquals("García Juan", nueva.getTitular());
        assertEquals(GrupoSanguineo.O, nueva.getGrupoSanguineo());
        assertEquals(FactorRH.NEGATIVO, nueva.getFactorRH());
        assertTrue(nueva.getDonanteOrganos());
        // Recalcula vigencia (40 años -> 5) y costo
        assertEquals(5, nueva.getVigencia());
        assertEquals(48.0, nueva.getCosto());
        // Conserva documento, clase y fecha de nacimiento
        assertEquals("12345678", nueva.getNumeroDocumento());
        assertEquals("B", nueva.getClase());
        // Vencimiento sobre el cumpleaños (día/mes coinciden)
        assertEquals(actual.getFechaNacimiento().getDayOfMonth(), nueva.getFechaVencimiento().getDayOfMonth());
        assertEquals(actual.getFechaNacimiento().getMonth(), nueva.getFechaVencimiento().getMonth());
    }

    @Test
    void renovarConDatos_noExigeVentanaDeVencimiento() {
        // La licencia vence en años (fuera de la ventana 1-6 meses) y aún así debe renovar.
        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(actual));
        when(costoService.calcularCostoTotal(anyString(), anyInt())).thenReturn(48.0);
        when(repository.save(any(Licencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Licencia nueva = service.renovarConDatosActualizados(dto);

        assertTrue(nueva.getVigente());
        // Sin datos en el DTO, conserva los de la licencia actual
        assertEquals("Juan Pérez", nueva.getTitular());
        assertEquals(GrupoSanguineo.A, nueva.getGrupoSanguineo());
        verify(repository, org.mockito.Mockito.times(2)).save(any(Licencia.class));
    }

    @Test
    void renovarConDatos_licenciaNoVigente_lanzaExcepcion() {
        actual.setVigente(false);
        RenovarLicenciaDTO dto = new RenovarLicenciaDTO();
        dto.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(actual));

        assertThrows(IllegalArgumentException.class, () -> service.renovarConDatosActualizados(dto));
    }
}
