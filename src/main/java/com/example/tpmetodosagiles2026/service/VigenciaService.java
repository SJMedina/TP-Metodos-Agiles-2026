package com.example.tpmetodosagiles2026.service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class VigenciaService {

    public int determinarAnios(int edad, boolean primeraLicencia) {
        if (edad < 21) {
            return primeraLicencia ? 1 : 3;
        }
        if (edad <= 46) {
            return 5;
        }
        if (edad <= 60) {
            return 4;
        }
        if (edad <= 70) {
            return 3;
        }
        return 1;
    }

    public LocalDate calcularVencimiento(LocalDate fechaNacimiento, boolean primeraLicencia, LocalDate fechaInicio) {
        if (fechaNacimiento == null || fechaInicio == null) {
            throw new IllegalArgumentException("fechaNacimiento y fechaInicio son requeridos");
        }

        int edad = calcularEdad(fechaNacimiento, fechaInicio);
        int anios = determinarAnios(edad, primeraLicencia);

        int anioVencimiento = fechaInicio.getYear() + anios;

        LocalDate vencimiento = crearDiaValido(anioVencimiento, fechaNacimiento.getMonthValue(), fechaNacimiento.getDayOfMonth());

        while (vencimiento.isBefore(fechaInicio)) {
            anioVencimiento++;
            vencimiento = crearDiaValido(anioVencimiento, fechaNacimiento.getMonthValue(), fechaNacimiento.getDayOfMonth());
        }

        return vencimiento;
    }

    private LocalDate crearDiaValido(int anio, int mes, int dia) {
        try {
            return LocalDate.of(anio, mes, dia);
        } catch (DateTimeException e) {
            YearMonth ym = YearMonth.of(anio, mes);
            return ym.atEndOfMonth();
        }
    }

    public int calcularEdad(@NonNull LocalDate fechaNacimiento, @NonNull LocalDate enFecha) {
        int edad = enFecha.getYear() - fechaNacimiento.getYear();
        if (enFecha.getMonthValue() < fechaNacimiento.getMonthValue() ||
                (enFecha.getMonthValue() == fechaNacimiento.getMonthValue() && enFecha.getDayOfMonth() < fechaNacimiento.getDayOfMonth())) {
            edad--;
        }
        return edad;
    }
}

