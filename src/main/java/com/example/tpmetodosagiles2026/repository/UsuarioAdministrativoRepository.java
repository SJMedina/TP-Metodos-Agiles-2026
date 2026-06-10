package com.example.tpmetodosagiles2026.repository;

import com.example.tpmetodosagiles2026.model.UsuarioAdministrativo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioAdministrativoRepository extends JpaRepository<UsuarioAdministrativo, String> {
    boolean existsById(String id);
}
