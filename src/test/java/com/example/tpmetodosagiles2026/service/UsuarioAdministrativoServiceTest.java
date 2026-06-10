package com.example.tpmetodosagiles2026.service;

import com.example.tpmetodosagiles2026.dto.CrearUsuarioDTO;
import com.example.tpmetodosagiles2026.model.UsuarioAdministrativo;
import com.example.tpmetodosagiles2026.repository.UsuarioAdministrativoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioAdministrativoServiceTest {

    @Mock
    private UsuarioAdministrativoRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioAdministrativoService service;

    @Test
    void crear_usuarioNuevo_hasheaPasswordYPersiste() {
        CrearUsuarioDTO dto = crearDTO("emp01", "Juan Perez", "pass123");

        when(repository.existsById("emp01")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$HASH");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsuarioAdministrativo resultado = service.crear(dto);

        assertNotNull(resultado);
        assertEquals("emp01", resultado.getId());
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("$2a$HASH", resultado.getPasswordHash());
        verify(repository).save(any(UsuarioAdministrativo.class));
    }

    @Test
    void crear_idDuplicado_lanzaIllegalArgumentException() {
        CrearUsuarioDTO dto = crearDTO("emp01", "Juan Perez", "pass123");

        when(repository.existsById("emp01")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.crear(dto)
        );

        assertEquals("Ya existe un usuario con el ID: emp01", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void crear_passwordSehashea_noSeGuardaEnPlano() {
        CrearUsuarioDTO dto = crearDTO("emp02", "Maria Lopez", "secreto");

        when(repository.existsById("emp02")).thenReturn(false);
        when(passwordEncoder.encode("secreto")).thenReturn("$2a$BCRYPT_HASH");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsuarioAdministrativo resultado = service.crear(dto);

        assertEquals("$2a$BCRYPT_HASH", resultado.getPasswordHash());
        verify(passwordEncoder).encode("secreto");
    }

    @Test
    void listar_delegaAlRepositorio() {
        List<UsuarioAdministrativo> lista = List.of(
                new UsuarioAdministrativo("emp01", "Juan Perez", "$2a$HASH1"),
                new UsuarioAdministrativo("emp02", "Maria Lopez", "$2a$HASH2")
        );
        when(repository.findAll()).thenReturn(lista);

        List<UsuarioAdministrativo> resultado = service.listar();

        assertEquals(2, resultado.size());
        assertEquals("emp01", resultado.get(0).getId());
        assertEquals("emp02", resultado.get(1).getId());
    }

    private CrearUsuarioDTO crearDTO(String id, String nombre, String password) {
        CrearUsuarioDTO dto = new CrearUsuarioDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setPassword(password);
        return dto;
    }
}
