package com.example.tpmetodosagiles2026.repository;

import com.example.tpmetodosagiles2026.entity.Titular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitularRepository extends JpaRepository<Titular, Long> {
}