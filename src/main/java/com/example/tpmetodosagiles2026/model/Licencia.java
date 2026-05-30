package com.example.tpmetodosagiles2026.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "licencias")
public class Licencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titular;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false)
    private String clase;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private String usuarioAdministrativo;

    @Column(nullable = false)
    private String numeroDocumento;

    private Integer vigencia;
    private Double costo;
    private LocalDate fechaNacimiento;

    public Licencia() {
    }

    public Licencia(String titular, Integer edad, String clase, String numeroDocumento) {
        this.titular = titular;
        this.edad = edad;
        this.clase = clase;
        this.numeroDocumento = numeroDocumento;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getUsuarioAdministrativo() { return usuarioAdministrativo; }
    public void setUsuarioAdministrativo(String usuarioAdministrativo) { this.usuarioAdministrativo = usuarioAdministrativo; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public Integer getVigencia() { return vigencia; }
    public void setVigencia(Integer vigencia) { this.vigencia = vigencia; }

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}
