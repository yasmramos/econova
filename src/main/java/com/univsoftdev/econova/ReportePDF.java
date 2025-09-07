package com.univsoftdev.econova;

import org.xhtmlrenderer.pdf.ITextRenderer;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportePDF {

    public static void generarPDF(String templatePath, Map<String, Object> datos, String outputPath) {
        try {
            // 1. Configurar Freemarker
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
            cfg.setClassForTemplateLoading(ReportePDF.class, "/");
            cfg.setDefaultEncoding("UTF-8");

            // 2. Cargar plantilla
            Template template = cfg.getTemplate(templatePath);

            // 3. Procesar plantilla a HTML
            StringWriter htmlOut = new StringWriter();
            template.process(datos, htmlOut);
            String html = htmlOut.toString();

            // 4. Convertir HTML a PDF con Flying Saucer
            try (OutputStream os = new FileOutputStream(outputPath)) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(os);
            }

            log.info("PDF generado en: " + outputPath);

        } catch (IOException | TemplateException e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Datos dinámicos
        Map<String, Object> datos = new HashMap<>();
        datos.put("fecha", "2023-10-05");

        // Lista de productos (ejemplo)
        datos.put("productos", List.of(
                new Producto("Laptop", 10, 1200.00),
                new Producto("Mouse", 50, 25.50)
        ));

        // Generar PDF
        generarPDF("/reporte_template.ftl", datos, "reporte.pdf");
    }

    // Clase auxiliar para productos
    @Getter
    @Setter
    private static class Producto {

        private String nombre;
        private int cantidad;
        private double precio;

        Producto(String nombre, int cantidad, double precio) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precio = precio;
        }
        
    }
}
