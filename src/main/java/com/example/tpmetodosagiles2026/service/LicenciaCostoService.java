package com.example.tpmetodosagiles2026.service;

import org.springframework.stereotype.Service;
@Service
public class LicenciaCostoService {

    public static final double GASTO_ADMINISTRATIVO = 8.0;

    public double calcularCostoTotal(String clase, int vigenciaAnios) {
        double costoBase = obtenerCostoBase(clase.toUpperCase(), vigenciaAnios);
        return costoBase + GASTO_ADMINISTRATIVO;
    }

    private double obtenerCostoBase(String clase, int vigencia) {

        switch (clase) {
            case "A":
            case "B":
            case "E":
                return switch (vigencia) {
                    case 5 -> 40.0; 
                    case 4 -> 30.0; 
                    case 3 -> 25.0; 
                    case 1 -> 20.0; 
                    default -> throw new IllegalArgumentException("Vigencia no válida");
                };
            case "C":
                return switch (vigencia) {
                    case 5 -> 47.0; 
                    case 4 -> 35.0; 
                    case 3 -> 30.0; 
                    case 1 -> 23.0; 
                    default -> throw new IllegalArgumentException("Vigencia no válida");
                };
            case "F":
            case "G":
                return switch (vigencia) {
                    case 5 -> 40.0;
                    case 4 -> 30.0;
                    case 3 -> 25.0;
                    case 1 -> 20.0;
                    default -> throw new IllegalArgumentException("Vigencia no válida");
                };
            case "D":
                return switch (vigencia) {
                    case 5 -> 59.0; 
                    case 4 -> 44.0; 
                    case 3 -> 39.0; 
                    case 1 -> 29.0; 
                    default -> throw new IllegalArgumentException("Vigencia no válida");
                };
            default:
                throw new IllegalArgumentException("Clase de licencia no válida");
        }
    }
}