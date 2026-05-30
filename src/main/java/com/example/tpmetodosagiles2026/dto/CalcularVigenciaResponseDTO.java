package com.example.tpmetodosagiles2026.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class CalcularVigenciaResponseDTO {

    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private Integer anios;
    private Integer edad;

    public CalcularVigenciaResponseDTO() {
    }

    public CalcularVigenciaResponseDTO(LocalDate fechaInicio, LocalDate fechaVencimiento, Integer anios) {
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.anios = anios;
    }

    public CalcularVigenciaResponseDTO(LocalDate fechaInicio, LocalDate fechaVencimiento, Integer anios, Integer edad) {
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.anios = anios;
        this.edad = edad;
    }

}

