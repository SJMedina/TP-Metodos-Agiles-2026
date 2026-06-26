package com.example.tpmetodosagiles2026.service;

import com.example.tpmetodosagiles2026.model.Licencia;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Image;
import java.net.URL;

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
        
        Rectangle tamañoTarjeta = new Rectangle(242.65f, 153.01f);
        
        //seteo de margenes para centrar bien texto
        Document document = new Document(tamañoTarjeta, 85f, 10f, 30f, 10f);
        
        PdfWriter writer = PdfWriter.getInstance(document, out);

        document.open();

        //imagen
        try {
            URL imageUrl = getClass().getResource("/images/plantilla_licencia.jpg");
            if (imageUrl != null) {
                Image fondo = Image.getInstance(imageUrl);
                
                fondo.scaleAbsolute(tamañoTarjeta.getWidth(), tamañoTarjeta.getHeight());
                fondo.setAbsolutePosition(0, 0);
                
                writer.getDirectContentUnder().addImage(fondo);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen de fondo: " + e.getMessage());
        }

        // datos
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font fontDestacada = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);

        //texto ajustado segun margenes de arriba
        document.add(new Paragraph("Fecha de emisión: " + licencia.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontNormal));
        document.add(new Paragraph("Titular: " + licencia.getTitular(), fontNormal));
        document.add(new Paragraph("Documento: " + licencia.getNumeroDocumento(), fontNormal));
        
        Paragraph clase = new Paragraph("Clase: " + licencia.getClase(), fontDestacada);
        document.add(clase);
        
        document.add(new Paragraph("Vigencia: " + licencia.getVigencia() + " años", fontNormal));
        document.add(new Paragraph("Observaciones: \n" + licencia.getObservaciones(), fontNormal));
        
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