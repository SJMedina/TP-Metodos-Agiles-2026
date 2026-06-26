package com.example.tpmetodosagiles2026.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    private GrupoSanguineo grupoSanguineo;

    @Enumerated(EnumType.STRING)
    private FactorRH factorRH;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean donanteOrganos = false;

    // true = registro actual; false = historial
    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean vigente = true;

    private Integer vigencia;
    private Double costo;
    private LocalDate fechaNacimiento;
    private LocalDate fechaVencimiento;

    public Licencia(String titular, int edad, String clase, String numeroDocumento) {
        this.titular = titular;
        this.edad = edad;
        this.clase = clase;
        this.numeroDocumento = numeroDocumento;
        this.donanteOrganos = false;
        this.vigente = true;
    }
}
