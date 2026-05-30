package com.example.tpmetodosagiles2026.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class CalcularVigenciaRequestDTO {

    private LocalDate fechaNacimiento;
    private Boolean primeraLicencia;

    public CalcularVigenciaRequestDTO() {
    }

    public CalcularVigenciaRequestDTO(LocalDate fechaNacimiento, Boolean primeraLicencia) {
        this.fechaNacimiento = fechaNacimiento;
        this.primeraLicencia = primeraLicencia;
    }

}

