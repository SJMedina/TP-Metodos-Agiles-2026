package com.example.tpmetodosagiles2026.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.tpmetodosagiles2026.model.FactorRH;
import com.example.tpmetodosagiles2026.model.GrupoSanguineo;
import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.model.SuperUsuario;
import com.example.tpmetodosagiles2026.repository.LicenciaRepository;
import com.example.tpmetodosagiles2026.repository.SuperUsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LicenciaRepository licenciaRepository;
    private final SuperUsuarioRepository superUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(LicenciaRepository licenciaRepository,
                           SuperUsuarioRepository superUsuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.licenciaRepository = licenciaRepository;
        this.superUsuarioRepository = superUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!superUsuarioRepository.existsById("superadmin")) {
            superUsuarioRepository.save(
                new SuperUsuario("superadmin", passwordEncoder.encode("super1234"))
            );
        }

        if (licenciaRepository.count() > 0) return;

        licenciaRepository.saveAll(List.of(
            crear("Juan Perez",       "30111222", LocalDate.of(1990, 3, 15), "B", GrupoSanguineo.A,  FactorRH.POSITIVO, true,  5),
            crear("Maria Garcia",     "28456789", LocalDate.of(1995, 7, 20), "A", GrupoSanguineo.B,  FactorRH.NEGATIVO, false, 5),
            crear("Carlos Lopez",     "25789123", LocalDate.of(1988, 11, 5), "C", GrupoSanguineo.AB, FactorRH.POSITIVO, true,  4),
            crear("Ana Martinez",     "32654321", LocalDate.of(2000, 1, 30), "B", GrupoSanguineo.O,  FactorRH.NEGATIVO, false, 3),
            crear("Pedro Gomez",      "27333444", LocalDate.of(1985, 6, 10), "D", GrupoSanguineo.A,  FactorRH.NEGATIVO, true,  4),
            crear("Laura Fernandez",  "35987654", LocalDate.of(1998, 9, 25), "B", GrupoSanguineo.O,  FactorRH.POSITIVO, true,  5),
            crear("Diego Rodriguez",  "22111333", LocalDate.of(1992, 4, 8),  "A", GrupoSanguineo.AB, FactorRH.NEGATIVO, false, 3),
            crearConEmision("Roberto Silva",  "40100200", LocalDate.of(1993, 5, 12), "B", GrupoSanguineo.A, FactorRH.POSITIVO, false, 1, LocalDateTime.of(2025, 8,  1, 10, 0)),
            crearConEmision("Sofia Torres",   "41200300", LocalDate.of(1997, 2, 28), "A", GrupoSanguineo.O, FactorRH.NEGATIVO, true,  3, LocalDateTime.of(2023, 9, 15, 10, 0)),
            crearConEmision("Marcos Herrera", "39300400", LocalDate.of(1986, 8, 20), "B", GrupoSanguineo.B, FactorRH.POSITIVO, true,  5, LocalDateTime.of(2021, 11, 1, 10, 0))
        ));
    }

    private Licencia crear(String titular, String documento, LocalDate nacimiento,
                           String clase, GrupoSanguineo grupo, FactorRH factor,
                           boolean donante, int vigencia) {
        return crearConEmision(titular, documento, nacimiento, clase, grupo, factor, donante, vigencia, LocalDateTime.now());
    }

    private Licencia crearConEmision(String titular, String documento, LocalDate nacimiento,
                                     String clase, GrupoSanguineo grupo, FactorRH factor,
                                     boolean donante, int vigencia, LocalDateTime fechaEmision) {
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
        l.setFechaEmision(fechaEmision);
        l.setUsuarioAdministrativo("SISTEMA");
        l.setCosto(0.0);
        l.setVigente(true);
        return l;
    }
}
