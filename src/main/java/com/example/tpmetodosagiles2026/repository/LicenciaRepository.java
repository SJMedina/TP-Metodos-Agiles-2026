package com.example.tpmetodosagiles2026.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tpmetodosagiles2026.model.Licencia;

public interface LicenciaRepository extends JpaRepository<Licencia, Long> {
    List<Licencia> findByNumeroDocumento(String numeroDocumento);
    List<Licencia> findByClaseAndNumeroDocumento(String clase, String numeroDocumento);
    
}
