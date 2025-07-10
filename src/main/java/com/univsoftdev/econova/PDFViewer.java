package com.univsoftdev.econova;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.pdfbox.Loader;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PDFViewer extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;
    private PDDocument document;
    private PDFRenderer renderer;
    private int paginaActual = 0;
    private int totalPaginas = 0;
    private String filePath;
    private JLabel pageLabel;
    private JButton btnAnterior;
    private JButton btnSiguiente;
    private double zoom = 1.0;
    private final double ZOOM_STEP = 0.1;
    private JButton btnZoomIn;
    private JButton btnZoomOut;
    private JTextField pageField;
    private JProgressBar progressBar;

    public PDFViewer(String filePath) {
        this.filePath = filePath;
        initComponents();
        inicializarNavegacion();
        cargarDocumento();
        mostrarPagina(paginaActual);
        inicializarAtajos();
    }

    private void inicializarNavegacion() {
        btnAnterior = new JButton("⟨");
        btnSiguiente = new JButton("⟩");
        btnZoomIn = new JButton("+");
        btnZoomOut = new JButton("−");
        pageLabel = new JLabel();
        pageField = new JTextField(3);
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        btnAnterior.setToolTipText("Página anterior (←)");
        btnSiguiente.setToolTipText("Página siguiente (→)");
        btnZoomIn.setToolTipText("Acercar (Ctrl +)");
        btnZoomOut.setToolTipText("Alejar (Ctrl -)");
        pageField.setHorizontalAlignment(JTextField.CENTER);
        pageField.setText("1");
        pageField.addActionListener(e -> irAPagina());
        btnAnterior.addActionListener(e -> mostrarPagina(paginaActual - 1));
        btnSiguiente.addActionListener(e -> mostrarPagina(paginaActual + 1));
        btnZoomIn.addActionListener(e -> cambiarZoom(zoom + ZOOM_STEP));
        btnZoomOut.addActionListener(e -> cambiarZoom(zoom - ZOOM_STEP));
        toolBar1.removeAll();
        toolBar1.add(btnAnterior);
        toolBar1.add(pageField);
        toolBar1.add(pageLabel);
        toolBar1.add(btnSiguiente);
        toolBar1.addSeparator();
        toolBar1.add(btnZoomOut);
        toolBar1.add(btnZoomIn);
        toolBar1.addSeparator();
        toolBar1.add(button1);
        toolBar1.addSeparator();
        toolBar1.add(progressBar);
    }

    private void cargarDocumento() {
        try {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            byte[] pdfBytes = Files.readAllBytes(Paths.get(filePath));
            document = Loader.loadPDF(pdfBytes);
            renderer = new PDFRenderer(document);
            totalPaginas = document.getNumberOfPages();
            progressBar.setVisible(false);
        } catch (IOException ex) {
            label1.setText("No se pudo cargar el PDF: " + ex.getMessage());
            progressBar.setVisible(false);
        }
    }

    private void mostrarPagina(int pagina) {
        if (document == null || renderer == null) return;
        if (pagina < 0 || pagina >= totalPaginas) return;
        try {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            BufferedImage image = renderer.renderImageWithDPI(pagina, (int)(150 * zoom));
            ImageIcon icon = new ImageIcon(image);
            // Ajuste automático al panel
            int panelW = panel1.getWidth();
            int panelH = panel1.getHeight();
            if (panelW > 0 && panelH > 0) {
                Image scaled = icon.getImage().getScaledInstance(panelW, panelH, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaled);
            }
            label1.setIcon(icon);
            label1.setText("");
            paginaActual = pagina;
            pageField.setText(String.valueOf(paginaActual + 1));
            actualizarControlesNavegacion();
            progressBar.setVisible(false);
        } catch (IOException ex) {
            label1.setText("Error al mostrar página: " + ex.getMessage());
            progressBar.setVisible(false);
        }
    }

    private void cambiarZoom(double nuevoZoom) {
        if (nuevoZoom < 0.2) nuevoZoom = 0.2;
        if (nuevoZoom > 3.0) nuevoZoom = 3.0;
        zoom = nuevoZoom;
        mostrarPagina(paginaActual);
    }

    private void irAPagina() {
        try {
            int num = Integer.parseInt(pageField.getText().trim()) - 1;
            if (num >= 0 && num < totalPaginas) {
                mostrarPagina(num);
            }
        } catch (NumberFormatException ignored) {}
    }

    private void actualizarControlesNavegacion() {
        pageLabel.setText("de " + totalPaginas);
        btnAnterior.setEnabled(paginaActual > 0);
        btnSiguiente.setEnabled(paginaActual < totalPaginas - 1);
        btnZoomIn.setEnabled(zoom < 3.0);
        btnZoomOut.setEnabled(zoom > 0.2);
    }

    private void inicializarAtajos() {
        // Navegación con flechas y zoom con Ctrl +/−
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "anterior");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "siguiente");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control PLUS"), "zoomIn");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control EQUALS"), "zoomIn");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control MINUS"), "zoomOut");
        getRootPane().getActionMap().put("anterior", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { mostrarPagina(paginaActual - 1); }
        });
        getRootPane().getActionMap().put("siguiente", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { mostrarPagina(paginaActual + 1); }
        });
        getRootPane().getActionMap().put("zoomIn", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { cambiarZoom(zoom + ZOOM_STEP); }
        });
        getRootPane().getActionMap().put("zoomOut", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { cambiarZoom(zoom - ZOOM_STEP); }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            if (document != null) document.close();
        } catch (IOException ignored) {}
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
	this.toolBar1 = new JToolBar();
	this.button1 = new JButton();
	this.panel1 = new JPanel();
	this.label1 = new JLabel();

	//======== this ========
	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	var contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== toolBar1 ========
	{

	    //---- button1 ----
	    this.button1.setText("Imprimir"); //NOI18N
	    this.toolBar1.add(this.button1);
	}
	contentPane.add(this.toolBar1, BorderLayout.NORTH);

	//======== panel1 ========
	{
	    this.panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
	    this.panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    this.label1.setBorder(new EmptyBorder(5, 5, 5, 5));
	    this.panel1.add(this.label1, BorderLayout.CENTER);
	}
	contentPane.add(this.panel1, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(() -> {
//            new PDFViewer().setVisible(true);
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JToolBar toolBar1;
    private JButton button1;
    private JPanel panel1;
    private JLabel label1;
    // End of variables declaration//GEN-END:variables
}
