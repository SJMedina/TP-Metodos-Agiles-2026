package com.example.tpmetodosagiles2026.controller;

import com.example.tpmetodosagiles2026.dto.ActualizarUsuarioDTO;
import com.example.tpmetodosagiles2026.dto.CrearUsuarioDTO;
import com.example.tpmetodosagiles2026.model.UsuarioAdministrativo;
import com.example.tpmetodosagiles2026.service.UsuarioAdministrativoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioAdministrativoControllerTest {

    @Mock
    private UsuarioAdministrativoService service;

    @InjectMocks
    private UsuarioAdministrativoController controller;

    @Test
    void crear_usuarioValido_devuelve201ConUsuario() {
        UsuarioAdministrativo usuario = new UsuarioAdministrativo("emp01", "Juan Perez", "$2a$HASH");
        when(service.crear(any(CrearUsuarioDTO.class))).thenReturn(usuario);

        ResponseEntity<?> response = controller.crear(crearDTO("emp01", "Juan Perez", "pass123"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UsuarioAdministrativo body = (UsuarioAdministrativo) response.getBody();
        assertNotNull(body);
        assertEquals("emp01", body.getId());
        assertEquals("Juan Perez", body.getNombre());
    }

    @Test
    void crear_idDuplicado_devuelve400ConMensajeDeError() {
        when(service.crear(any(CrearUsuarioDTO.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un usuario con el ID: emp01"));

        ResponseEntity<?> response = controller.crear(crearDTO("emp01", "Juan Perez", "pass123"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Ya existe un usuario con el ID: emp01", body.get("error"));
    }

    @Test
    void listar_devuelve200ConListaDeUsuarios() {
        List<UsuarioAdministrativo> lista = List.of(
                new UsuarioAdministrativo("emp01", "Juan Perez", "$2a$HASH1"),
                new UsuarioAdministrativo("emp02", "Maria Lopez", "$2a$HASH2")
        );
        when(service.listar()).thenReturn(lista);

        ResponseEntity<List<UsuarioAdministrativo>> response = controller.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void listar_sinUsuarios_devuelveListaVacia() {
        when(service.listar()).thenReturn(List.of());

        ResponseEntity<List<UsuarioAdministrativo>> response = controller.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void actualizar_datosValidos_devuelve200() {
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO("Nuevo Nombre", "newPass");
        UsuarioAdministrativo usuario = new UsuarioAdministrativo("emp01", "Nuevo Nombre", "$2a$HASH");

        when(service.actualizar("emp01", dto)).thenReturn(usuario);

        ResponseEntity<?> response = controller.actualizar("emp01", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        UsuarioAdministrativo body = (UsuarioAdministrativo) response.getBody();
        assertEquals("Nuevo Nombre", body.getNombre());
    }

    @Test
    void actualizar_usuarioNoExiste_devuelve404() {
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO("Nuevo", "pass");

        when(service.actualizar("noExiste", dto))
                .thenThrow(new IllegalArgumentException("Usuario no encontrado: noExiste"));

        ResponseEntity<?> response = controller.actualizar("noExiste", dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Usuario no encontrado: noExiste", body.get("error"));
    }

    @Test
    void actualizar_soloNombre_devuelve200() {
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO("Nuevo", null);
        UsuarioAdministrativo usuario = new UsuarioAdministrativo("emp01", "Nuevo", "$2a$HASH");

        when(service.actualizar("emp01", dto)).thenReturn(usuario);

        ResponseEntity<?> response = controller.actualizar("emp01", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void actualizar_soloPassword_devuelve200() {
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO(null, "newPass");
        UsuarioAdministrativo usuario = new UsuarioAdministrativo("emp01", "Juan", "$2a$NEWHASH");

        when(service.actualizar("emp01", dto)).thenReturn(usuario);

        ResponseEntity<?> response = controller.actualizar("emp01", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void actualizar_conNombreLargo_devuelve200() {
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO("María José García López Fernández Rodríguez", "pass");
        UsuarioAdministrativo usuario = new UsuarioAdministrativo("emp01",
                "María José García López Fernández Rodríguez", "$2a$HASH");

        when(service.actualizar("emp01", dto)).thenReturn(usuario);

        ResponseEntity<?> response = controller.actualizar("emp01", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void actualizar_conAmbos_sirvePersistencia() {
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO("ActualizadoTest", "newPassword123");
        UsuarioAdministrativo usuario = new UsuarioAdministrativo("emp01", "ActualizadoTest", "$2a$HASH123");

        when(service.actualizar("emp01", dto)).thenReturn(usuario);

        ResponseEntity<?> response = controller.actualizar("emp01", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UsuarioAdministrativo body = (UsuarioAdministrativo) response.getBody();
        assertEquals("emp01", body.getId());
        assertEquals("ActualizadoTest", body.getNombre());
        assertEquals("$2a$HASH123", body.getPasswordHash());
    }

    private CrearUsuarioDTO crearDTO(String id, String nombre, String password) {
        CrearUsuarioDTO dto = new CrearUsuarioDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setPassword(password);
        return dto;
    }
}
