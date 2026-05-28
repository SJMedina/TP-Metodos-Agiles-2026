package com.example.tpmetodosagiles2026.controller;

import com.example.tpmetodosagiles2026.service.LicenciaCostoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/licencias")
@CrossOrigin(origins = "http://localhost:4200")
public class LicenciaCostoController {

    @Autowired
    private LicenciaCostoService costoService;

    @GetMapping("/costo")
    public double obtenerCosto(@RequestParam String clase, @RequestParam int vigencia) {
        return costoService.calcularCostoTotal(clase, vigencia);
    }
}