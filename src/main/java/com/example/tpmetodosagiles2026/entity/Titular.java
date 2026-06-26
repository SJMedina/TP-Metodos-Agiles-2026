package com.example.tpmetodosagiles2026.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;



@Entity
public class Titular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoDocumento;
    private String numeroDocumento;
    private String apellido;
    private String nombre;
    private LocalDate fechaNacimiento;
    
    // Relación 1 a 1 con Direccion. CascadeType.ALL asegura que al guardar el Titular, se guarde su Dirección.
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    private Direccion direccion;
    
    // Clase solicitada (A, B, C, D, E, F, G)
    private String claseSolicitada; 
    
    // Grupo sanguíneo: A, B, AB, O
    private String grupoSanguineo;

    // Factor RH: POSITIVO, NEGATIVO
    private String factorRH;

    // true = SI, false = NO
    private Boolean donanteOrganos;

    public Titular() {
    }

    public Titular(Long id, String tipoDocumento, String numeroDocumento, String apellido, String nombre,
            LocalDate fechaNacimiento, Direccion direccion, String claseSolicitada, String grupoSanguineo,
            String factorRH, Boolean donanteOrganos) {
        this.id = id;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.apellido = apellido;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.claseSolicitada = claseSolicitada;
        this.grupoSanguineo = grupoSanguineo;
        this.factorRH = factorRH;
        this.donanteOrganos = donanteOrganos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public String getClaseSolicitada() {
        return claseSolicitada;
    }

    public void setClaseSolicitada(String claseSolicitada) {
        this.claseSolicitada = claseSolicitada;
    }

    public String getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public void setGrupoSanguineo(String grupoSanguineo) {
        this.grupoSanguineo = grupoSanguineo;
    }

    public String getFactorRH() {
        return factorRH;
    }

    public void setFactorRH(String factorRH) {
        this.factorRH = factorRH;
    }

    public Boolean getDonanteOrganos() {
        return donanteOrganos;
    }

    public void setDonanteOrganos(Boolean donanteOrganos) {
        this.donanteOrganos = donanteOrganos;
    }

    

}