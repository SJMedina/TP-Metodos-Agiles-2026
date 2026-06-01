package com.example.tpmetodosagiles2026.service;

import com.example.tpmetodosagiles2026.model.Licencia;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfService {

    private final LicenciaService licenciaService;

    public PdfService(LicenciaService licenciaService) {
        this.licenciaService = licenciaService;
    }

    public byte[] generarZipImpresion(Long idLicencia) {
        Licencia licencia = licenciaService.buscarPorId(idLicencia);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            //licencia
            zos.putNextEntry(new ZipEntry("Licencia_" + licencia.getTitular() +"_" +  licencia.getNumeroDocumento()  +".pdf"));
            zos.write(generarPdfLicencia(licencia));
            zos.closeEntry();

            //comprobante de pago
            zos.putNextEntry(new ZipEntry("ComprobantePago_" + licencia.getTitular() + "_"+ licencia.getNumeroDocumento() + ".pdf"));
            zos.write(generarPdfComprobante(licencia));
            zos.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("Error al empaquetar los PDFs", e);
        }

        return baos.toByteArray();
    }

    private byte[] generarPdfLicencia(Licencia licencia) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();
        document.add(new Paragraph("LICENCIA DE CONDUCIR MUNICIPAL"));
        document.add(new Paragraph("Fecha de emision: " + licencia.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph("Titular: " + licencia.getTitular()));
        document.add(new Paragraph("Documento: " + licencia.getNumeroDocumento()));
        document.add(new Paragraph("Clase: " + licencia.getClase()));
        document.add(new Paragraph("Vigencia: " + licencia.getVigencia() + " años"));
        document.add(new Paragraph("Observaciones: " + licencia.getObservaciones()));
        document.close();

        return out.toByteArray();
    }

    private byte[] generarPdfComprobante(Licencia licencia) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        double costoTotal = licencia.getCosto();
        double gastosAdmin = 8.0; 
        double costoBase = costoTotal - gastosAdmin;

        document.open();
        document.add(new Paragraph("COMPROBANTE DE PAGO - CAJA MUNICIPAL"));
        document.add(new Paragraph("Fecha de emision: " + licencia.getFechaEmision().format(formatter)));
        document.add(new Paragraph("Titular: " + licencia.getTitular()));
        document.add(new Paragraph("Documento: " + licencia.getNumeroDocumento()));
        document.add(new Paragraph("--------------------------------------------------"));
        document.add(new Paragraph("Costo base de emision (Clase " + licencia.getClase() + "): $ " + costoBase));
        document.add(new Paragraph("Gastos Administrativos: $ " + gastosAdmin));
        document.add(new Paragraph("--------------------------------------------------"));
        document.add(new Paragraph("TOTAL A ABONAR: $ " + costoTotal));
        document.close();

        return out.toByteArray();
    }
}