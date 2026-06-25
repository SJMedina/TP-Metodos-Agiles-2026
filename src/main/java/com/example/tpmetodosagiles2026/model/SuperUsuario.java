package com.example.tpmetodosagiles2026.model;

import jakarta.persistence.*;

@Entity
@Table(name = "super_usuarios")
public class SuperUsuario {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String passwordHash;

    public SuperUsuario() {}

    public SuperUsuario(String id, String passwordHash) {
        this.id = id;
        this.passwordHash = passwordHash;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
