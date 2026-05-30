package com.example.tpmetodosagiles2026.repository;

import com.example.tpmetodosagiles2026.model.Licencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LicenciaRepository extends JpaRepository<Licencia, Long> {
    List<Licencia> findByNumeroDocumento(String numeroDocumento);
    List<Licencia> findByClaseAndNumeroDocumento(String clase, String numeroDocumento);
}
