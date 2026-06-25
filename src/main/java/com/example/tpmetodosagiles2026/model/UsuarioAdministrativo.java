package com.example.tpmetodosagiles2026.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios_administrativos")
public class UsuarioAdministrativo {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String passwordHash;

    public UsuarioAdministrativo() {}

    public UsuarioAdministrativo(String id, String nombre, String passwordHash) {
        this.id = id;
        this.nombre = nombre;
        this.passwordHash = passwordHash;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
