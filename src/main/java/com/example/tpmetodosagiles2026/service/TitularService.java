package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tpmetodosagiles2026.entity.Titular;
import com.example.tpmetodosagiles2026.repository.TitularRepository;

@Service
public class TitularService {

    private final TitularRepository titularRepository;

    @Autowired
    public TitularService(TitularRepository titularRepository) {
        this.titularRepository = titularRepository;
    }

    public Titular registrarTitular(Titular titular) {
        // Regla 2.2: Documento de 8 dígitos exactos
        if (titular.getNumeroDocumento() == null || !titular.getNumeroDocumento().matches("^\\d{8}$")) {
            throw new IllegalArgumentException("El número de documento debe contener exactamente 8 dígitos.");
        }

        // Regla 2.1: Mayor de 18 años
        if (titular.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        int edad = Period.between(titular.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 18) {
            throw new IllegalArgumentException("El titular debe ser mayor de 18 años.");
        }

        // Validación adicional requerida: Código Postal de 4 dígitos
        if (titular.getDireccion() == null || titular.getDireccion().getCodigoPostal() == null || 
            !titular.getDireccion().getCodigoPostal().matches("^\\d{4}$")) {
            throw new IllegalArgumentException("El código postal debe contener exactamente 4 dígitos.");
        }

        return titularRepository.save(titular);
    }
}