package com.univsoftdev.econova.contabilidad.views.comprobante;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.AppSession;
import com.univsoftdev.econova.Injector;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.SubSistema;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.service.ContabilidadService;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import io.avaje.inject.BeanScope;
import jakarta.validation.constraints.NotNull;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.*;

@Slf4j
public class DialogNuevoComprobante extends JDialog {

    private static final long serialVersionUID = 3697096490193996608L;
    private boolean guardado = false;
    private boolean validado = false;
    private Asiento asiento;
    private final JTable tableComprobantes;
    private final ContabilidadService contabilidadService;
    private final AppContext appContext;
    private final DefaultTableModel modelo;
    private NumberFormat formatoMoneda;
    private AppSession session;
    private int nro;
    private boolean persistido = false;

    public DialogNuevoComprobante(JTable tableComprobantes) {
        initComponents();

        formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "US"));
        formatoMoneda.setMinimumFractionDigits(2);
        formatoMoneda.setMaximumFractionDigits(2);

        this.appContext = Injector.get(AppContext.class);
        this.session = appContext.getSession();

        // Inyección de dependencias
        this.contabilidadService = Injector.get(ContabilidadService.class);

        String[] columnas = {"CTA", "SBCTA", "SCTRO", "ANAL", "EPIG", "Débito", "Crédito"};

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        table1.setModel(modelo);
        table1.setRowHeight(25);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setCellSelectionEnabled(true);

        this.tableComprobantes = tableComprobantes;
        configurarTabla();
        ComprobanteTableUtil.configurarEventos(table1, modelo, textFieldTotalDebito, textFieldTotalCredito);

        table1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int filaActual = table1.getSelectedRow();
                    int columnaActual = table1.getSelectedColumn();

                    // Verificar si estamos en la última fila y en la columna 6 (Crédito)
                    if (filaActual == table1.getRowCount() - 1 && columnaActual == 6) {

                        Object valorDebito = table1.getValueAt(filaActual, 5);
                        Object valorCredito = table1.getValueAt(filaActual, 6);

                        boolean debitoVacio = valorDebito == null || valorDebito.toString().trim().isEmpty();
                        boolean creditoVacio = valorCredito == null || valorCredito.toString().trim().isEmpty();

                        if (debitoVacio && creditoVacio) {
                            // Volver a la columna 0 de la misma fila
                            SwingUtilities.invokeLater(() -> {
                                table1.changeSelection(filaActual, 0, false, false);
                                table1.editCellAt(filaActual, 0);
                                Component comp = table1.getEditorComponent();
                                if (comp != null) {
                                    comp.requestFocusInWindow();
                                }
                            });
                        }
                    }
                }
            }
        });
        // Inicializar el objeto asiento
        initAsiento();
        this.setTitle(this.getTitle() + " Nro." + asiento.getNro());

        // Agregar tooltip dinámico
        table1.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int column = table1.columnAtPoint(e.getPoint());
                if (column == 5 || column == 6) {
                    table1.setToolTipText("Ingrese un número decimal (por ejemplo, 1234.56)");
                } else {
                    table1.setToolTipText(null);
                }
            }
        });

    }

    private void initAsiento() {
        if (this.asiento == null) {
            this.asiento = new Asiento();
        }

        nro = contabilidadService.obtenerSiguienteCodigoDeAsiento(appContext.getSession().getPeriodo());
        if (nro == 0) {
            nro = 1;
        }
    }

    private void configurarTabla() {
        // Configurar constantes para mejor mantenibilidad
        final int PRIMERAS_COLUMNAS = 5;
        final int COLUMNA_DEBITO = 5;
        final int COLUMNA_CREDITO = 6;

        // Cargar cuentas raíz para CTA con opción vacía
        ItemCombo[] itemsCTA = crearItemsCuentasConOpcionVacia();
        configurarEditorColumnaCuenta(itemsCTA);

        // Configurar renderers y editors comunes
        configurarRenderersColumnas(PRIMERAS_COLUMNAS);
        configurarEditoresMoneda(COLUMNA_DEBITO, COLUMNA_CREDITO);
        configurarRenderersMoneda(COLUMNA_DEBITO, COLUMNA_CREDITO);
    }

    private ItemCombo[] crearItemsCuentasConOpcionVacia() {
        java.util.List<Cuenta> cuentasRaiz = contabilidadService.findAllCuentas().stream()
                .filter(c -> c.getCuentaPadre() == null)
                .collect(Collectors.toList());

        ItemCombo[] items = new ItemCombo[cuentasRaiz.size() + 1];
        items[0] = new ItemCombo(new Cuenta("", "")); // Opción vacía

        for (int i = 0; i < cuentasRaiz.size(); i++) {
            items[i + 1] = new ItemCombo(cuentasRaiz.get(i));
        }

        return items;
    }

    private void configurarEditorColumnaCuenta(ItemCombo[] items) {
        TableCellEditor editor = new ComprobanteComboBoxCellEditor(items);
        table1.getColumnModel().getColumn(0).setCellEditor(editor);
    }

    private void configurarRenderersColumnas(int hastaColumna) {
        CodigoSoloRenderer renderer = new CodigoSoloRenderer();
        for (int i = 0; i < hastaColumna; i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void configurarEditoresMoneda(int colDebito, int colCredito) {
        TableCellEditor editorMoneda = new EditorMoneda();
        table1.getColumnModel().getColumn(colDebito).setCellEditor(editorMoneda);
        table1.getColumnModel().getColumn(colCredito).setCellEditor(editorMoneda);
    }

    private void configurarRenderersMoneda(int colDebito, int colCredito) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);

        table1.getColumnModel().getColumn(colDebito).setCellRenderer(renderer);
        table1.getColumnModel().getColumn(colCredito).setCellRenderer(renderer);
    }

    public void exportToPDF(@NotNull JTable table, String filePath) {
        try (Document document = new Document()) {
            // Configura el escritor para guardar en la ruta filePath
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Crea una table1 PDF con tantas columnas como el JTable
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());

            // Agrega el encabezado
            for (int i = 0; i < table.getColumnCount(); i++) {
                pdfTable.addCell(new Phrase(table.getColumnName(i)));
            }

            // Agrega las filas de datos
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object value = table.getValueAt(row, col);
                    pdfTable.addCell(value != null ? value.toString() : "");
                }
            }

            // Añade la table1 al documento
            document.add(pdfTable);
        } catch (DocumentException | IOException e) {
            log.error(e.getMessage());
        }
    }

    private void btnNuevoActionPerformed(ActionEvent e) {
        ComprobanteTableUtil.agregarNuevaFila(table1, modelo);
    }

    private void btnEliminarActionPerformed(ActionEvent e) {
        var model = (DefaultTableModel) table1.getModel();
        int[] filasSeleccionadas = table1.getSelectedRows();

        if (filasSeleccionadas.length == 0) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una fila para eliminar.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmación del usuario
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar las filas seleccionadas?",
                "Confirmación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Eliminar filas en orden inverso
            for (int i = filasSeleccionadas.length - 1; i >= 0; i--) {
                model.removeRow(filasSeleccionadas[i]);
            }

            JOptionPane.showMessageDialog(this, "Las filas seleccionadas han sido eliminadas correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void validarAsiento() {
        boolean errors = false;

        if (txtDescripcion.getText().trim().isEmpty()) {
            errors = true;
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        if (datePickerSwing1.getSelectedDate() == null) {
            errors = true;
            throw new IllegalArgumentException("Debe seleccionar una fecha válida.");
        }

        if (asiento == null) {
            errors = true;
            JOptionPane.showMessageDialog(this, "El asiento no está inicializado y no se puede guardar.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            log.error("Intento de guardar asiento no inicializado.");
            return;
        }

        boolean validateAsiento = contabilidadService.validateAsiento(asiento);
        if (!validateAsiento) {
            errors = true;
            JOptionPane.showMessageDialog(this, "Error en el Asiento descuadre en la sume de los débitos/créditos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        validarTablaAntesDeProcesar();
        if (!errors) {
            asiento.setEstadoAsiento(EstadoAsiento.OK);
        } else {
            asiento.setEstadoAsiento(EstadoAsiento.ERROR);
        }
    }

    private void thisWindowClosing(WindowEvent e) {
        validarAsiento();
        if (!guardado) {
            var guardarCambios = false;

            if (table1.getRowCount() > 0 || !txtDescripcion.getText().isEmpty()) {
                int confirmGuardarCambios = JOptionPane.showConfirmDialog(null, "¿Desea guardar los cambios?", "Confirmación", JOptionPane.YES_NO_OPTION);
                if (confirmGuardarCambios == JOptionPane.YES_OPTION) {
                    guardarCambios = true;
                }
            }

            try {
                if (guardarCambios) {
                    if (!persistido) {
                        contabilidadService.save(asiento);
                        var model = (DefaultTableModel) this.tableComprobantes.getModel();
                        model.addRow(new Object[]{
                            asiento.getNro(),
                            asiento.getDescripcion(),
                            asiento.getEstadoAsiento(),
                            asiento.getFecha(),
                            SubSistema.CONTABILIDAD.getDescripcion(),
                            asiento.getWhoModified().getFullName(),
                            asiento.getUnidad().getCodigo()
                        });
                    }
                }
            } catch (Exception ex) {
                log.error("No se pudo guardar el Asiento Contable", ex);
            }
            this.dispose();
            int nCompConfirm = JOptionPane.showConfirmDialog(null, "¿Desea crear otro comprobante?", "Confirmación", JOptionPane.YES_NO_OPTION);
            if (nCompConfirm == JOptionPane.YES_OPTION) {
                new DialogNuevoComprobante(null).setVisible(true);
            }
        }

        TableColumnAdjuster adjuster = new TableColumnAdjuster(tableComprobantes);
        adjuster.adjustColumns();
    }

    private void guardarActionPerformed(ActionEvent e) {
        guardar();
        persistido = true;
    }

    private void guardar() {
        if (!validarConfiguracionSesion()) {
            return;
        }
        validarAsiento();

        guardado = true;
        if (!validado) {
            guardarAsiento(EstadoAsiento.ERROR);
        } else {
            guardarAsiento(EstadoAsiento.OK);
        }
    }

    private void imprimirActionPerformed(ActionEvent e) {
    }

    private void verRegAnexoAlPaseActionPerformed(ActionEvent e) {
    }

    private void validarActionPerformed(ActionEvent e) {
        try {
            validado = true;
            guardado = true;
            validarAsiento();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private void btnGuardarNuevo(ActionEvent e) {
        if (persistido == false) {
            guardar();
        }
        new DialogNuevoComprobante(tableComprobantes).setVisible(true);
        this.dispose();
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this2 = new JPanel();
	label1 = new JLabel();
	datePickerSwing1 = new DatePickerSwing();
	toolBar1 = new JToolBar();
	btnNuevo = new JButton();
	btnEliminar = new JButton();
	buttonValidar = new JButton();
	buttonVerRegAnexoAlPase = new JButton();
	buttonGuardar = new JButton();
	buttonImprimir = new JButton();
	scrollPane1 = new JScrollPane();
	txtDescripcion = new JTextArea();
	panel1 = new JPanel();
	panel2 = new JPanel();
	textFieldTotalCredito = new JTextField();
	textFieldTotalDebito = new JTextField();
	labelValidacion = new JLabel();
	tabla = new JScrollPane();
	table1 = new JTable();
	btnGuardarNuevo = new JButton();
	btnAceptar = new JButton();
	btnCancelar = new JButton();
	label2 = new JLabel();

	//======== this ========
	setTitle("Comprobante");
	setModal(true);
	addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		thisWindowClosing(e);
	    }
	});
	var contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== this2 ========
	{
	    this2.setBorder(new EmptyBorder(5, 5, 5, 5));

	    //---- label1 ----
	    label1.setText("Descripci\u00f3n");

	    //======== toolBar1 ========
	    {
		toolBar1.setPreferredSize(new Dimension(44, 25));
		toolBar1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		//---- btnNuevo ----
		btnNuevo.setIcon(new ImageIcon(getClass().getResource("/com/univsoftdev/core/icons/plus.png")));
		btnNuevo.setPreferredSize(new Dimension(20, 25));
		btnNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNuevo.addActionListener(e -> btnNuevoActionPerformed(e));
		toolBar1.add(btnNuevo);

		//---- btnEliminar ----
		btnEliminar.setIcon(new ImageIcon(getClass().getResource("/com/univsoftdev/core/icons/minus.png")));
		btnEliminar.setPreferredSize(new Dimension(20, 25));
		btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnEliminar.addActionListener(e -> btnEliminarActionPerformed(e));
		toolBar1.add(btnEliminar);

		//---- buttonValidar ----
		buttonValidar.setText("V\u00e1lidar");
		buttonValidar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonValidar.addActionListener(e -> validarActionPerformed(e));
		toolBar1.add(buttonValidar);

		//---- buttonVerRegAnexoAlPase ----
		buttonVerRegAnexoAlPase.setText("Ver Registro Anexo al Pase");
		buttonVerRegAnexoAlPase.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonVerRegAnexoAlPase.addActionListener(e -> verRegAnexoAlPaseActionPerformed(e));
		toolBar1.add(buttonVerRegAnexoAlPase);

		//---- buttonGuardar ----
		buttonGuardar.setText("Guardar");
		buttonGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonGuardar.addActionListener(e -> guardarActionPerformed(e));
		toolBar1.add(buttonGuardar);

		//---- buttonImprimir ----
		buttonImprimir.setText("Imprimir");
		buttonImprimir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonImprimir.addActionListener(e -> imprimirActionPerformed(e));
		toolBar1.add(buttonImprimir);
	    }

	    //======== scrollPane1 ========
	    {
		scrollPane1.setViewportView(txtDescripcion);
	    }

	    //======== panel1 ========
	    {
		panel1.setLayout(new MigLayout(
		    "fill,insets 0,hidemode 3",
		    // columns
		    "[fill]",
		    // rows
		    "[fill]"));

		//======== panel2 ========
		{

		    //---- textFieldTotalCredito ----
		    textFieldTotalCredito.setMinimumSize(new Dimension(84, 22));
		    textFieldTotalCredito.setBackground(new Color(0xf2f2f2));

		    //---- textFieldTotalDebito ----
		    textFieldTotalDebito.setMinimumSize(new Dimension(84, 22));
		    textFieldTotalDebito.setBackground(new Color(0xf2f2f2));

		    //---- labelValidacion ----
		    labelValidacion.setText("      ");

		    GroupLayout panel2Layout = new GroupLayout(panel2);
		    panel2.setLayout(panel2Layout);
		    panel2Layout.setHorizontalGroup(
			panel2Layout.createParallelGroup()
			    .addGroup(panel2Layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(labelValidacion, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
				.addGap(33, 33, 33)
				.addComponent(textFieldTotalDebito, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(textFieldTotalCredito, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
				.addContainerGap())
		    );
		    panel2Layout.setVerticalGroup(
			panel2Layout.createParallelGroup()
			    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(labelValidacion)
				.addComponent(textFieldTotalCredito, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(textFieldTotalDebito, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    );
		}
		panel1.add(panel2, "cell 0 1");

		//======== tabla ========
		{

		    //---- table1 ----
		    table1.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
			    "CTA", "SBCTA", "SCTRO", "ANAL", "EPIG", "D\u00e9bito", "Cr\u00e9dito"
			}
		    ));
		    table1.setShowHorizontalLines(true);
		    table1.setShowVerticalLines(true);
		    table1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		    tabla.setViewportView(table1);
		}
		panel1.add(tabla, "north");
	    }

	    //---- btnGuardarNuevo ----
	    btnGuardarNuevo.setText("Guardar & Nuevo");
	    btnGuardarNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    btnGuardarNuevo.addActionListener(e -> btnGuardarNuevo(e));

	    //---- btnAceptar ----
	    btnAceptar.setText("Guardar & Cerrar");
	    btnAceptar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	    //---- btnCancelar ----
	    btnCancelar.setText("Cancelar");
	    btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	    //---- label2 ----
	    label2.setText("Fecha");

	    GroupLayout this2Layout = new GroupLayout(this2);
	    this2.setLayout(this2Layout);
	    this2Layout.setHorizontalGroup(
		this2Layout.createParallelGroup()
		    .addGroup(GroupLayout.Alignment.TRAILING, this2Layout.createSequentialGroup()
			.addGap(18, 18, 18)
			.addGroup(this2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
			    .addGroup(this2Layout.createSequentialGroup()
				.addComponent(btnCancelar)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnAceptar)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(btnGuardarNuevo))
			    .addComponent(scrollPane1)
			    .addGroup(this2Layout.createSequentialGroup()
				.addComponent(label1)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(label2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(datePickerSwing1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			    .addGroup(this2Layout.createSequentialGroup()
				.addGap(0, 0, Short.MAX_VALUE)
				.addComponent(toolBar1, GroupLayout.PREFERRED_SIZE, 357, GroupLayout.PREFERRED_SIZE)))
			.addGap(18, 18, 18))
	    );
	    this2Layout.setVerticalGroup(
		this2Layout.createParallelGroup()
		    .addGroup(this2Layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(this2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
			    .addGroup(this2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(label1, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
				.addComponent(label2))
			    .addComponent(datePickerSwing1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(toolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(panel1, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
			.addGap(18, 18, 18)
			.addGroup(this2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(btnGuardarNuevo)
			    .addComponent(btnAceptar)
			    .addComponent(btnCancelar))
			.addContainerGap(15, Short.MAX_VALUE))
	    );
	}
	contentPane.add(this2, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(null);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel this2;
    private JLabel label1;
    private DatePickerSwing datePickerSwing1;
    private JToolBar toolBar1;
    private JButton btnNuevo;
    private JButton btnEliminar;
    private JButton buttonValidar;
    private JButton buttonVerRegAnexoAlPase;
    private JButton buttonGuardar;
    private JButton buttonImprimir;
    private JScrollPane scrollPane1;
    private JTextArea txtDescripcion;
    private JPanel panel1;
    private JPanel panel2;
    private JTextField textFieldTotalCredito;
    private JTextField textFieldTotalDebito;
    private JLabel labelValidacion;
    private JScrollPane tabla;
    private JTable table1;
    private JButton btnGuardarNuevo;
    private JButton btnAceptar;
    private JButton btnCancelar;
    private JLabel label2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void guardarAsiento(EstadoAsiento estadoAsiento) {
        try {
            // Configurar el asiento con todos los campos obligatorios
            asiento.setNro(nro);
            asiento.setSubSistema(SubSistema.CONTABILIDAD);
            asiento.setDescripcion(txtDescripcion.getText());
            asiento.setFecha(datePickerSwing1.getSelectedDate());
            asiento.setEstadoAsiento(estadoAsiento);

            // Campos de auditoría y relaciones
            asiento.setUnidad(Objects.requireNonNull(session.getUnidad(),
                    "No se ha configurado la unidad organizativa en la sesión"));
            asiento.setPeriodo(Objects.requireNonNull(session.getPeriodo(),
                    "No se ha configurado el período contable en la sesión"));
            asiento.setUsuario(Objects.requireNonNull(session.getUser(),
                    "No hay usuario autenticado en la sesión"));

            // Procesar transacciones
            final var asientoProcessorFactory = Injector.get(AsientoProcessorFactory.class);
            AsientoProcessor processor = asientoProcessorFactory.create(asiento);
            processor.procesarTabla((DefaultTableModel) table1.getModel());

            if (!processor.getErrores().isEmpty()) {
                String mensajeError = "Errores encontrados:\n\n"
                        + String.join("\n", processor.getErrores());

                JOptionPane.showMessageDialog(this,
                        mensajeError,
                        "Errores en el Asiento",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar que el asiento esté cuadrado
            if (!asiento.estaCuadrado()) {
                labelValidacion.setForeground(Color.RED);
                labelValidacion.setText("El asiento no está cuadrado.");
                throw new IllegalStateException("El asiento no está cuadrado");
            }

            // Guardar el asiento
            validarAsiento();
            contabilidadService.save(asiento);
            persistido = true;
            labelValidacion.setForeground(Color.GREEN);
            labelValidacion.setText("Asiento guardado correctamente");
            JOptionPane.showMessageDialog(this, "Asiento guardado con éxito",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | IllegalArgumentException | IllegalStateException e) {
            log.error("Error al guardar el asiento", e);
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el asiento: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarConfiguracionSesion() {
        StringBuilder errores = new StringBuilder();

        if (session.getUnidad() == null) {
            errores.append("- No se ha configurado la unidad organizativa\n");
        }
        if (session.getPeriodo() == null) {
            errores.append("- No se ha configurado el período contable\n");
        }
        if (session.getUser() == null) {
            errores.append("- No hay usuario autenticado\n");
        }

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Error de configuración:\n" + errores.toString(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean validarTablaAntesDeProcesar() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        boolean valido = true;

        for (int i = 0; i < model.getRowCount(); i++) {
            // Verificar que cada fila tenga al menos cuenta y débito/crédito
            if (model.getValueAt(i, 0) == null
                    || (model.getValueAt(i, 5) == null && model.getValueAt(i, 6) == null)) {
                table1.setRowSelectionInterval(i, i);
                table1.scrollRectToVisible(table1.getCellRect(i, 0, true));
                valido = false;
                break;
            }
        }

        if (!valido) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos requeridos en las filas resaltadas",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
        }

        return valido;
    }
}
