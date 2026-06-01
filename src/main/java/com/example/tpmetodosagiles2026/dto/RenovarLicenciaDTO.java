package com.example.tpmetodosagiles2026.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RenovarLicenciaDTO {
    private Long id;
    private String titular;
    private Integer edad;
    private LocalDate fechaNacimiento;
    private String observaciones;
    private Integer vigencia;       // nueva vigencia
    private Boolean renovarPorVencimiento; // true si es por vencimiento, false si es por datos

}