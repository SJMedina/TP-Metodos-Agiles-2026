package com.example.tpmetodosagiles2026.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tpmetodosagiles2026.dto.EmitirLicenciaDTO;
import com.example.tpmetodosagiles2026.dto.RenovarLicenciaDTO;
import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.service.LicenciaService;
import com.example.tpmetodosagiles2026.service.PdfService;

@RestController
@RequestMapping("/api/licencias")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080" })
public class LicenciaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenciaController.class);
    private final LicenciaService service;
    private final PdfService pdfService;



    public LicenciaController(LicenciaService service, PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ResponseEntity<?> emitir(@RequestBody EmitirLicenciaDTO dto) {
        LOGGER.info(
                "Recibida solicitud de emisión: titular='{}', documento='{}', clase='{}', fechaNacimiento='{}', vigencia='{}'",
                dto.getTitular(), dto.getNumeroDocumento(), dto.getClase(), dto.getFechaNacimiento(),
                dto.getVigencia());
        try {
            Licencia licencia = service.emitir(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(licencia);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Validación inválida al emitir licencia: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/renovar")
    public ResponseEntity<?> renovar(@RequestBody RenovarLicenciaDTO dto) {
        LOGGER.info("Recibida solicitud de renovación: id='{}', vigencia='{}'",
                dto.getId(), dto.getVigencia());
        try {
            Licencia licencia = service.renovar(dto);
            return ResponseEntity.ok(licencia);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Validación inválida al renovar licencia: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<Licencia>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{documento}")
    public ResponseEntity<List<Licencia>> listarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(service.listarPorDocumento(documento));
    }

    @GetMapping("/documento/{documento}/clase/{clase}")
    public ResponseEntity<?> obtenerPorDocumentoYClase(
            @PathVariable String documento,
            @PathVariable String clase) {
        List<Licencia> licencias = service.listarPorDocumento(documento);
        Licencia licencia = licencias.stream()
                .filter(l -> l.getClase().equalsIgnoreCase(clase))
                .findFirst()
                .orElse(null);

        if (licencia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(licencia);
    }

    @GetMapping(value = "/{id}/imprimir", produces = "application/zip")
    public ResponseEntity<byte[]> imprimirLicencia(@PathVariable Long id) {
        byte[] zipBytes = pdfService.generarZipImpresion(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"tramite_licencia_" + id +".zip\"")
                .body(zipBytes);
    }


}

