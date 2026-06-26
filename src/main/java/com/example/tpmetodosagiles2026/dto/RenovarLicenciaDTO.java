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
    private Integer vigencia;
    private Boolean renovarPorVencimiento;
    private String grupoSanguineo;
    private String factorRH;
    private Boolean donanteOrganos;

}