package com.univsoftdev.econova.contabilidad;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class BalanceComprobacionPDFGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceComprobacionPDFGenerator.class);

    public static void generarPDF(List<LineaBalance> items,
            String filePath, String logoPath, String empresa,
            String unidad, String textoAdicional, String moneda,
            String fecha, String periodo, String usuario) {
        try {
            // Configuración del documento
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 54);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));

            // Configurar eventos para encabezado y pie de página
            writer.setPageEvent(new HeaderFooter(logoPath, empresa, unidad, textoAdicional, moneda, fecha, periodo, usuario));

            document.open();

            //Titulo Principal 
            Font fontTitle = new Font(Font.HELVETICA, 14, Font.BOLD);
            Paragraph title = new Paragraph("Balance de Comprobación de Saldos", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);

            //Agregar Espació Vertical para separar el emcabezado del título.
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" ", new Font(Font.HELVETICA, 20))); //Espacio de 20pt
            document.add(title);
            document.add(new Paragraph(" "));

            // Fuente para el contenido
            Font fontHeader = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font fontData = new Font(Font.HELVETICA, 8);
            Font fontFooter = new Font(Font.HELVETICA, 8, Font.ITALIC);

            // Crear tabla con 6 columnas
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 4f, 1.5f, 1.5f, 1.5f, 1.5f});

            // Encabezados de la tabla (mergeado)
            PdfPCell headerPeriodo = new PdfPCell(new Phrase("Período", fontHeader));
            headerPeriodo.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerPeriodo.setColspan(2);
            headerPeriodo.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell headerAcumulado = new PdfPCell(new Phrase("Acumulado", fontHeader));
            headerAcumulado.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerAcumulado.setColspan(2);
            headerAcumulado.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell pdfPCell = new PdfPCell(new Phrase("CTA-SBCT-SCT-ANA-EPIG", fontHeader));
            pdfPCell.setRowspan(1);
            PdfPCell pdfPCell1 = new PdfPCell(new Phrase("Descripción", fontHeader));
            pdfPCell1.setRowspan(1);

            table.addCell(pdfPCell);
            table.addCell(pdfPCell1);
            table.addCell(headerPeriodo);
            table.addCell(headerAcumulado);

            // Subencabezados
            table.addCell(new PdfPCell(new Phrase("", fontHeader)));
            table.addCell(new PdfPCell(new Phrase("", fontHeader)));
            table.addCell(new PdfPCell(new Phrase("Débitos", fontHeader)));
            table.addCell(new PdfPCell(new Phrase("Créditos", fontHeader)));
            table.addCell(new PdfPCell(new Phrase("Débitos", fontHeader)));
            table.addCell(new PdfPCell(new Phrase("Créditos", fontHeader)));

            BigDecimal totalDebitosAcumulados = BigDecimal.ZERO;
            BigDecimal totalCreditosAcumulados = BigDecimal.ZERO;

            // Agregar datos
            for (LineaBalance item : items) {
                table.addCell(new PdfPCell(new Phrase(item.getCodigo(), fontData)));
                table.addCell(new PdfPCell(new Phrase(item.getDescripcion(), fontData)));
                table.addCell(new PdfPCell(new Phrase(formatCurrency(item.getDebitoPeriodo()), fontData)));
                table.addCell(new PdfPCell(new Phrase(formatCurrency(item.getCreditoPeriodo()), fontData)));
                table.addCell(new PdfPCell(new Phrase(formatCurrency(item.getDebitoAcumulado()), fontData)));
                table.addCell(new PdfPCell(new Phrase(formatCurrency(item.getCreditoAcumulado()), fontData)));
            }

            // Agregar fila de totales acumulados
            Font fontTotal = new Font(Font.HELVETICA, 10, Font.BOLD);
            PdfPCell totalCell = new PdfPCell(new Phrase("Totales:", fontTotal));
            totalCell.setColspan(4); // Merge las primeras 4 columnas
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(totalCell);

            table.addCell(new PdfPCell(new Phrase(formatCurrency(totalDebitosAcumulados), fontTotal)));
            table.addCell(new PdfPCell(new Phrase(formatCurrency(totalCreditosAcumulados), fontTotal)));

            document.add(table);
            document.close();

        } catch (DocumentException | FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
    }

    // Formatear valores como moneda (ejemplo para CUP)
    private static String formatCurrency(BigDecimal value) {
        return value.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "$";
    }

    // Clase para encabezado y pie de página
    static class HeaderFooter extends PdfPageEventHelper {

        private final String logoPath;
        private final String empresa;
        private final String unidad;
        private final String textoAdicional;
        private final String moneda;
        private final String fecha;
        private final String periodo;
        private final String usuario;

        public HeaderFooter(String logoPath, String empresa, String unidad, String textoAdicional, String moneda, String fecha, String periodo, String usuario) {
            this.logoPath = logoPath;
            this.empresa = empresa;
            this.unidad = unidad;
            this.textoAdicional = textoAdicional;
            this.moneda = moneda;
            this.fecha = fecha;
            this.periodo = periodo;
            this.usuario = usuario;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {

                PdfContentByte cb = writer.getDirectContent();
                Font fontHeader = new Font(Font.HELVETICA, 10);
                Font fontFooter = new Font(Font.HELVETICA, 10, Font.ITALIC);

                //Crear Tabla para encabezado
                PdfPTable headerTable = new PdfPTable(2);
                headerTable.setWidthPercentage(100);
                headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                //Columna Izquierda (logo + datos de la empresa)
                PdfPTable leftColumn = new PdfPTable(2);
                leftColumn.setWidths(new float[]{1, 3});
                leftColumn.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                //Agregar logo si existe
                if (logoPath != null && !logoPath.isEmpty()) {
                    Image logo = Image.getInstance(logoPath);
                    logo.scaleToFit(50, 50); // Ancho y alto máximos

                    //Ajustar compresión (solo para JPEG)
                    if (logoPath.toLowerCase().endsWith(".jpg") || logoPath.toLowerCase().endsWith(".jpeg")) {
                        logo.setCompressionLevel(1); // Reduce la calidad para achicar el tamaño
                    }

                    PdfPCell logoCell = new PdfPCell(logo);
                    logoCell.setBorder(Rectangle.NO_BORDER);
                    logoCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrado horizontal
                    logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centrado vertical
                    leftColumn.addCell(logoCell);
                }

                //Datos de la empresa
                PdfPTable empresaData = new PdfPTable(1);
                empresaData.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                empresaData.addCell(new Phrase(empresa, fontHeader));
                empresaData.addCell(new Phrase(unidad, fontHeader));
                empresaData.addCell(new Phrase(textoAdicional, fontHeader));
                empresaData.addCell(new Phrase("Moneda: " + moneda, fontHeader));
                leftColumn.addCell(empresaData);

                //Columna derecha
                PdfPTable rightColumn = new PdfPTable(1);
                rightColumn.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                rightColumn.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                rightColumn.addCell(new Phrase("Fecha: " + fecha, fontHeader));
                rightColumn.addCell(new Phrase("Período: " + periodo, fontHeader));
                rightColumn.addCell(new Phrase("Usuario: " + usuario, fontHeader));
                leftColumn.addCell(rightColumn);

                //Combinar Columnasen headerTable
                headerTable.addCell(leftColumn);
                headerTable.addCell(rightColumn);

                //Calcular ancho absoluto
                float pageWidth = document.getPageSize().getWidth();
                float totalWidth = pageWidth - document.leftMargin() - document.rightMargin();
                headerTable.setTotalWidth(totalWidth);

                // Ajustar posición Y para evitar superposición
                float yPosition = document.getPageSize().getHeight() - document.topMargin() + 10; //10px

                //Agregar headerTable al documento
                headerTable.writeSelectedRows(0, -1, document.leftMargin(), yPosition, cb);

                // Crear tabla para el pie de página
                PdfPTable footerTable = new PdfPTable(2);
                footerTable.setWidthPercentage(100); // Ancho relativo al documento
                footerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                // Calcular ancho absoluto de la tabla
                float pageWidth1 = document.getPageSize().getWidth();
                float totalWidth1 = pageWidth1 - document.leftMargin() - document.rightMargin();
                footerTable.setTotalWidth(totalWidth1); // Forzar ancho absoluto

                // Celda izquierda: "Contador:"
                PdfPCell leftCell = new PdfPCell(new Phrase("Contador: ", fontFooter));
                leftCell.setBorder(Rectangle.NO_BORDER);
                leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                footerTable.addCell(leftCell);
                
                PdfPCell centerCell = new PdfPCell(new Phrase("Director: ", fontFooter));
                centerCell.setBorder(Rectangle.NO_BORDER);
                centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                footerTable.addCell(centerCell);

                // Celda derecha:  número de página

                PdfPCell rightCell = new PdfPCell(new Phrase("Página " + writer.getPageNumber(), fontFooter));
                rightCell.setBorder(Rectangle.NO_BORDER);
                rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                footerTable.addCell(rightCell);

                // Posicionar la tabla en el pie de página
                footerTable.writeSelectedRows(
                        0, -1,
                        document.leftMargin(), // Alineación izquierda
                        document.bottomMargin() + 10, // Margen inferior + espacio
                        cb
                );
            } catch (DocumentException | IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
