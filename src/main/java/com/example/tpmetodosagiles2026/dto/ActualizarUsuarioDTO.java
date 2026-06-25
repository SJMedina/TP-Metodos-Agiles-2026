package com.example.tpmetodosagiles2026.dto;

import jakarta.validation.constraints.NotBlank;

public class ActualizarUsuarioDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    private String password;

    public ActualizarUsuarioDTO() {}

    public ActualizarUsuarioDTO(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
