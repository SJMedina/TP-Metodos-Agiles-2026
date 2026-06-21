package com.example.tpmetodosagiles2026.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.example.tpmetodosagiles2026.entity.Titular;
import com.example.tpmetodosagiles2026.service.TitularService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@WebMvcTest(TitularController.class)
class TitularControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TitularService titularService;

    private ObjectMapper objectMapper;
    private Titular titular;

    @BeforeEach
    void setUp() {
        // Configuramos ObjectMapper para que sepa parsear LocalDate
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        titular = new Titular();
        titular.setNombre("Juan");
        titular.setApellido("Perez");
    }

    @Test
    void darDeAlta_Retorna201Created_CuandoDatosSonValidos() throws Exception {
        when(titularService.registrarTitular(any(Titular.class))).thenReturn(titular);

        mockMvc.perform(post("/api/titulares")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titular)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    @Test
    void darDeAlta_Retorna400BadRequest_CuandoServicioLanzaExcepcion() throws Exception {
        String mensajeError = "El titular debe ser mayor de 18 años.";
        when(titularService.registrarTitular(any(Titular.class)))
                .thenThrow(new IllegalArgumentException(mensajeError));

        mockMvc.perform(post("/api/titulares")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titular)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mensajeError));
    }


    @Test
    void modificarTitular_DebeRetornarOk_CuandoLaActualizacionEsExitosa() throws Exception {
        
        Long id = 1L;
        Titular titularModificado = new Titular();
        titularModificado.setId(id);
        titularModificado.setNombre("Juan Carlos");
        titularModificado.setApellido("Perez");

        when(titularService.modificarTitular(eq(id), any(Titular.class))).thenReturn(titularModificado);

        String jsonRequestBody = "{\"nombre\":\"Juan Carlos\",\"apellido\":\"Perez\"}";

        mockMvc.perform(put("/api/titulares/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk()) // Esperamos un 200 OK
                .andExpect(jsonPath("$.nombre").value("Juan Carlos")) // Verificamos el JSON de respuesta
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    @Test
    void modificarTitular_DebeRetornarBadRequest_CuandoFallaLaValidacionDeNegocio() throws Exception {
            Long id = 1L;
        
    
        when(titularService.modificarTitular(eq(id), any(Titular.class)))
                .thenThrow(new IllegalArgumentException("El código postal debe contener exactamente 4 dígitos."));

        String jsonRequestBody = "{\"nombre\":\"Juan\",\"direccion\":{\"codigoPostal\":\"12\"}}";

       
        mockMvc.perform(put("/api/titulares/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isBadRequest()) 
                .andExpect(content().string("El código postal debe contener exactamente 4 dígitos.")); 

    }
}