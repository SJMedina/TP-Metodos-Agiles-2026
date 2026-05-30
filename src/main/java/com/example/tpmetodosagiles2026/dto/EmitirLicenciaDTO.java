package com.example.tpmetodosagiles2026.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class EmitirLicenciaDTO {

    // Getters and Setters
    private String titular;
    private Integer edad;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private String clase;
    private String observaciones;
    private Boolean poseeLicenciaB;
    private Integer antiguedadLicenciaBEnAnios;
    private Boolean tieneLicenciaProfesionalAnterior;
    private Integer vigencia;
    private Double costo;

    // Constructors
    public EmitirLicenciaDTO() {
    }

    public EmitirLicenciaDTO(String titular, Integer edad, String numeroDocumento, 
                             LocalDate fechaNacimiento, String clase) {
        this.titular = titular;
        this.edad = edad;
        this.numeroDocumento = numeroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.clase = clase;
    }

}
