package com.example.tpmetodosagiles2026.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.tpmetodosagiles2026.model.FactorRH;
import com.example.tpmetodosagiles2026.model.GrupoSanguineo;
import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.repository.LicenciaRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LicenciaRepository repository;

    public DataInitializer(LicenciaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        repository.saveAll(List.of(
            crear("Juan Pérez",       "30111222", LocalDate.of(1990, 3, 15), "B", GrupoSanguineo.A,  FactorRH.POSITIVO, true,  5),
            crear("María García",     "28456789", LocalDate.of(1995, 7, 20), "A", GrupoSanguineo.B,  FactorRH.NEGATIVO, false, 5),
            crear("Carlos López",     "25789123", LocalDate.of(1988, 11, 5), "C", GrupoSanguineo.AB, FactorRH.POSITIVO, true,  4),
            crear("Ana Martínez",     "32654321", LocalDate.of(2000, 1, 30), "B", GrupoSanguineo.O,  FactorRH.NEGATIVO, false, 3),
            crear("Pedro Gómez",      "27333444", LocalDate.of(1985, 6, 10), "D", GrupoSanguineo.A,  FactorRH.NEGATIVO, true,  4),
            crear("Laura Fernández",  "35987654", LocalDate.of(1998, 9, 25), "B", GrupoSanguineo.O,  FactorRH.POSITIVO, true,  5),
            crear("Diego Rodríguez",  "22111333", LocalDate.of(1992, 4, 8),  "A", GrupoSanguineo.AB, FactorRH.NEGATIVO, false, 3)
        ));
    }

    private Licencia crear(String titular, String documento, LocalDate nacimiento,
                           String clase, GrupoSanguineo grupo, FactorRH factor,
                           boolean donante, int vigencia) {
        Licencia l = new Licencia();
        l.setTitular(titular);
        l.setNumeroDocumento(documento);
        l.setFechaNacimiento(nacimiento);
        l.setEdad(Period.between(nacimiento, LocalDate.now()).getYears());
        l.setClase(clase);
        l.setGrupoSanguineo(grupo);
        l.setFactorRH(factor);
        l.setDonanteOrganos(donante);
        l.setVigencia(vigencia);
        l.setFechaEmision(LocalDateTime.now());
        l.setUsuarioAdministrativo("SISTEMA");
        l.setCosto(0.0);
        l.setVigente(true);
        return l;
    }
}
