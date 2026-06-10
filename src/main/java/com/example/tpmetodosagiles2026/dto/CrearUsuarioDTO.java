package com.example.tpmetodosagiles2026.dto;

import jakarta.validation.constraints.NotBlank;

public class CrearUsuarioDTO {

    @NotBlank
    private String id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String password;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
