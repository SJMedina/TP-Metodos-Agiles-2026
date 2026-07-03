package com.example.tpmetodosagiles2026.service;

import com.example.tpmetodosagiles2026.model.Licencia;
import com.lowagie.text.Document;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.Image;
import java.awt.Color;
import java.net.URL;

import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class PdfService {

    private final LicenciaService licenciaService;
    private final LicenciaCostoService costoService;

    public PdfService(LicenciaService licenciaService, LicenciaCostoService costoService) {
        this.licenciaService = licenciaService;
        this.costoService = costoService;
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
        if(licencia.getObservaciones() == null || licencia.getObservaciones().isEmpty()) {
            licencia.setObservaciones("");
        }
        document.add(new Paragraph("Observaciones: \n" + licencia.getObservaciones(), fontNormal));
        
        document.close();

        return out.toByteArray();
    }

    private byte[] generarPdfComprobante(Licencia licencia) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A5, 36f, 36f, 30f, 30f);
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Font fontNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font fontLegal = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7, Font.NORMAL, Color.DARK_GRAY);

        String numeroComprobante = generarNumeroComprobante(licencia);

        document.add(construirEncabezado(fontTitulo, fontSubtitulo));
        document.add(new LineSeparator(1f, 100f, Color.BLACK, Element.ALIGN_CENTER, -2));
        document.add(new Paragraph(" "));

        Paragraph titulo = new Paragraph("COMPROBANTE DE PAGO", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph numero = new Paragraph("N° " + numeroComprobante, fontNegrita);
        numero.setAlignment(Element.ALIGN_CENTER);
        document.add(numero);
        document.add(new Paragraph(" "));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        document.add(new Paragraph("Fecha de emisión: " + licencia.getFechaEmision().format(formatter), fontNormal));
        document.add(new Paragraph("Titular: " + licencia.getTitular(), fontNormal));
        document.add(new Paragraph("Documento: " + licencia.getNumeroDocumento(), fontNormal));
        document.add(new Paragraph(" "));

        double costoTotal = licencia.getCosto();
        Double totalSegunFormula = calcularTotalSegunFormula(licencia.getClase(), licencia.getVigencia());
        boolean esTramiteEstandar = totalSegunFormula != null && Math.abs(totalSegunFormula - costoTotal) < 0.01;

        document.add(construirTablaConceptos(licencia, costoTotal, esTramiteEstandar, fontNegrita, fontNormal));

        Paragraph total = new Paragraph("TOTAL A ABONAR: $ " + formatearMonto(costoTotal), fontTotal);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
        document.add(new Paragraph(" "));

        LocalDate vencimiento = licencia.getFechaEmision().toLocalDate().plusDays(10);
        document.add(new Paragraph(
                "Válido para abonar hasta el " + vencimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                fontNormal));
        document.add(new Paragraph(" "));

        Barcode128 barcode128 = new Barcode128();
        barcode128.setCode(numeroComprobante);
        Image imagenCodigoBarras = barcode128.createImageWithBarcode(writer.getDirectContent(), null, null);
        imagenCodigoBarras.setAlignment(Element.ALIGN_CENTER);
        document.add(imagenCodigoBarras);
        document.add(new Paragraph(" "));

        Paragraph legal = new Paragraph(
                "Este comprobante es válido únicamente para efectuar el pago en Caja Municipal. " +
                        "Conserve este comprobante hasta la acreditación del pago.",
                fontLegal);
        legal.setAlignment(Element.ALIGN_CENTER);
        document.add(legal);

        document.close();

        return out.toByteArray();
    }

    private PdfPTable construirEncabezado(Font fontTitulo, Font fontSubtitulo) {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        try {
            header.setWidths(new float[]{1f, 3f});
        } catch (com.lowagie.text.DocumentException e) {
            // ancho por defecto si falla la asignacion de columnas
        }

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        try {
            URL logoUrl = getClass().getResource("/images/logosantafe.jpg");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(60f, 60f);
                logoCell.addElement(logo);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }
        header.addCell(logoCell);

        PdfPCell datosCell = new PdfPCell();
        datosCell.setBorder(Rectangle.NO_BORDER);
        datosCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        datosCell.addElement(new Paragraph("MUNICIPALIDAD DE SANTA FE", fontTitulo));
        datosCell.addElement(new Paragraph("Dirección de Tránsito y Transporte", fontSubtitulo));
        header.addCell(datosCell);

        return header;
    }

    private PdfPTable construirTablaConceptos(Licencia licencia, double costoTotal, boolean esTramiteEstandar,
            Font fontNegrita, Font fontNormal) {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        try {
            tabla.setWidths(new float[]{3f, 1f});
        } catch (com.lowagie.text.DocumentException e) {
            // ancho por defecto si falla la asignacion de columnas
        }

        PdfPCell headerConcepto = new PdfPCell(new Phrase("Concepto", fontNegrita));
        headerConcepto.setBackgroundColor(new Color(230, 230, 230));
        PdfPCell headerImporte = new PdfPCell(new Phrase("Importe", fontNegrita));
        headerImporte.setBackgroundColor(new Color(230, 230, 230));
        headerImporte.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(headerConcepto);
        tabla.addCell(headerImporte);

        if (esTramiteEstandar) {
            double gastosAdmin = LicenciaCostoService.GASTO_ADMINISTRATIVO;
            double costoBase = costoTotal - gastosAdmin;
            agregarFilaConcepto(tabla, "Emisión/Renovación Licencia Clase " + licencia.getClase(), costoBase, fontNormal);
            agregarFilaConcepto(tabla, "Gastos administrativos", gastosAdmin, fontNormal);
        } else {
            agregarFilaConcepto(tabla, "Emisión de copia de licencia", costoTotal, fontNormal);
        }

        return tabla;
    }

    private void agregarFilaConcepto(PdfPTable tabla, String concepto, double importe, Font font) {
        PdfPCell celdaConcepto = new PdfPCell(new Phrase(concepto, font));
        celdaConcepto.setBorder(Rectangle.BOX);
        tabla.addCell(celdaConcepto);

        PdfPCell celdaImporte = new PdfPCell(new Phrase("$ " + formatearMonto(importe), font));
        celdaImporte.setBorder(Rectangle.BOX);
        celdaImporte.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(celdaImporte);
    }

    private String generarNumeroComprobante(Licencia licencia) {
        int anio = licencia.getFechaEmision().getYear();
        long id = licencia.getId() != null ? licencia.getId() : 0L;
        return String.format(Locale.US, "%d-%06d-SF", anio, id);
    }

    private String formatearMonto(double monto) {
        return String.format(Locale.US, "%.2f", monto);
    }

    private Double calcularTotalSegunFormula(String clase, Integer vigencia) {
        try {
            return costoService.calcularCostoTotal(clase, vigencia);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}