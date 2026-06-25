package com.example.tpmetodosagiles2026.dto;

import java.time.LocalDate;

public class EmitirLicenciaDTO {

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
    private String grupoSanguineo;
    private String factorRH;
    private Boolean donanteOrganos;

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

    // Getters and Setters
    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Boolean getPoseeLicenciaB() { return poseeLicenciaB; }
    public void setPoseeLicenciaB(Boolean poseeLicenciaB) { this.poseeLicenciaB = poseeLicenciaB; }

    public Integer getAntiguedadLicenciaBEnAnios() { return antiguedadLicenciaBEnAnios; }
    public void setAntiguedadLicenciaBEnAnios(Integer antiguedadLicenciaBEnAnios) { 
        this.antiguedadLicenciaBEnAnios = antiguedadLicenciaBEnAnios; 
    }

    public Boolean getTieneLicenciaProfesionalAnterior() { return tieneLicenciaProfesionalAnterior; }
    public void setTieneLicenciaProfesionalAnterior(Boolean tieneLicenciaProfesionalAnterior) { 
        this.tieneLicenciaProfesionalAnterior = tieneLicenciaProfesionalAnterior; 
    }

    public Integer getVigencia() { return vigencia; }
    public void setVigencia(Integer vigencia) { this.vigencia = vigencia; }

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }

    public String getGrupoSanguineo() { return grupoSanguineo; }
    public void setGrupoSanguineo(String grupoSanguineo) { this.grupoSanguineo = grupoSanguineo; }

    public String getFactorRH() { return factorRH; }
    public void setFactorRH(String factorRH) { this.factorRH = factorRH; }

    public Boolean getDonanteOrganos() { return donanteOrganos; }
    public void setDonanteOrganos(Boolean donanteOrganos) { this.donanteOrganos = donanteOrganos; }
}
