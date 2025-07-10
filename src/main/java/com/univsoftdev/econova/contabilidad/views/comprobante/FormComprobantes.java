package com.univsoftdev.econova.contabilidad.views.comprobante;

import com.formdev.flatlaf.FlatClientProperties;
import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.service.AsientoService;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import com.univsoftdev.econova.core.utils.table.TableHeaderAlignment;
import io.avaje.inject.BeanScope;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.*;

/**
 * Vista de gestión de comprobantes contables.
 * Permite listar, filtrar, crear, duplicar, invertir, exportar e importar comprobantes.
 * Optimizada para robustez, multiplataforma y experiencia de usuario moderna.
 */
@Slf4j
public class FormComprobantes extends Form {

    private static final long serialVersionUID = -2243449267251361338L;
    private transient final BeanScope injector = AppContext.getInstance().getInjector();
    private transient final AsientoService asientoService;

    /**
     * Inicializa la vista y sus componentes.
     * Configura estilos, eventos y ajusta la tabla.
     */
    public FormComprobantes() {
        initComponents();
        ajustarTabla();
        asientoService = injector.get(AsientoService.class);
        configurarTabla();
        configurarEventos();
    }

    /** Ajusta el ancho de columnas de la tabla. */
    private void ajustarTabla() {
        TableColumnAdjuster adjuster = new TableColumnAdjuster(tableComprobantes);
        adjuster.adjustColumns();
    }

    /** Configura estilos y renderizado de la tabla. */
    private void configurarTabla() {
        tableComprobantes.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tableComprobantes) {
            @Override
            protected int getAlignment(int column) {
                return column == 0 ? SwingConstants.CENTER : SwingConstants.LEADING;
            }
        });
        tableComprobantes.setSelectionBackground(new Color(123, 207, 255));
        tableComprobantes.setSelectionForeground(new Color(51, 51, 51));
        tableComprobantes.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:20;hoverBackground:null;pressedBackground:null;separatorColor:$TableHeader.background;");
        tableComprobantes.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30;showHorizontalLines:true;intercellSpacing:0,1;cellFocusColor:$TableHeader.hoverBackground;selectionBackground:$TableHeader.hoverBackground;selectionInactiveBackground:$TableHeader.hoverBackground;selectionForeground:$Table.foreground;");
    }

    /** Configura listeners y eventos de la vista. */
    private void configurarEventos() {
        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                btnMostrarComprobantesPeriodo.doClick();
            }
        });
    }

    /**
     * Verifica si una fecha está entre dos límites (inclusive).
     */
    public static boolean estaEntreFechas(LocalDate fecha, LocalDate inicio, LocalDate fin) {
        if (fecha == null || inicio == null || fin == null) return false;
        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }

    /**
     * Crea un nuevo comprobante.
     */
    private void nuevoComprobante(ActionEvent e) {
        new DialogNuevoComprobante(tableComprobantes).setVisible(true);
        ajustarTabla();
    }

    /**
     * Valida el asiento seleccionado.
     */
    private void validar(ActionEvent e) {
        var asiento = getSelectedAsiento();
        if (asiento != null && asiento.getEstadoAsiento() != EstadoAsiento.VALIDADO) {
            if (asientoService.validateAsiento(asiento)) {
                asiento.setEstadoAsiento(EstadoAsiento.VALIDADO);
            } else {
                asiento.setEstadoAsiento(EstadoAsiento.ERROR);
            }
        }
    }

    /**
     * Marca el asiento como terminado.
     */
    private void terminar(ActionEvent e) {
        var asiento = getSelectedAsiento();
        if (asiento != null && asiento.getEstadoAsiento() != EstadoAsiento.TERMINADO) {
            asiento.setEstadoAsiento(EstadoAsiento.TERMINADO);
        }
    }

    /**
     * Obtiene el asiento seleccionado en la tabla.
     */
    private Asiento getSelectedAsiento() {
        int selectedRow = tableComprobantes.getSelectedRow();
        if (selectedRow < 0) return null;
        int valueAt = (int) tableComprobantes.getValueAt(selectedRow, 0);
        var asiento = asientoService.findByNro(valueAt);
        try {
            return asiento.orElseThrow(() -> new AsientoNotFoundException("No se ha encontrado el Asiento contable."));
        } catch (AsientoNotFoundException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    /**
     * Asienta todos los comprobantes del periodo.
     */
    private void asentarCompPeriodo(ActionEvent e) {
        asientoService.asentarAsientos();
    }

    /**
     * Termina todos los comprobantes del periodo (implementación ejemplo).
     */
    private void termCompPeriodo(ActionEvent e) {
        asientoService.terminarAsientos();
        mostrarComprobantesPeriodo();
    }

    /**
     * Elimina el asiento seleccionado, con confirmación.
     */
    private void eliminar(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(
                tableComprobantes,
                "¿Está seguro que desea eliminar el asiento contable?",
                "Confirmación", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.OK_OPTION) {
            var asiento = getSelectedAsiento();
            if (asiento != null && asiento.getEstadoAsiento() != EstadoAsiento.CONFIRMADO) {
                asientoService.delete(asiento);
                mostrarComprobantesPeriodo();
            }
        }
    }

    /**
     * Invierte los saldos de la unidad (implementación ejemplo).
     */
    private void invSaldosUnidad(ActionEvent e) {
        asientoService.invertirSaldosUnidad();
        mostrarComprobantesPeriodo();
    }

    /**
     * Invierte todos los comprobantes asentados (implementación ejemplo).
     */
    private void invTodosLosComp(ActionEvent e) {
        asientoService.invertirTodosAsentados();
        mostrarComprobantesPeriodo();
    }

    /**
     * Importa comprobantes desde archivo (implementación ejemplo).
     */
    private void importar(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            asientoService.importarDesdeArchivo(chooser.getSelectedFile());
            mostrarComprobantesPeriodo();
        }
    }

    /**
     * Exporta el asiento seleccionado a XML.
     */
    private void exportar(ActionEvent e) {
        try {
            JAXBContext context = JAXBContext.newInstance(Asiento.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            var fch = new JFileChooser();
            int showSaveDialog = fch.showSaveDialog(null);
            if (showSaveDialog == JFileChooser.APPROVE_OPTION) {
                marshaller.marshal(getSelectedAsiento(), Files.newBufferedWriter(Paths.get(fch.getSelectedFile().getAbsolutePath())));
            }
        } catch (JAXBException | IOException ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * Invierte el asiento seleccionado (implementación ejemplo).
     */
    private void invertir(ActionEvent e) {
        var asiento = getSelectedAsiento();
        if (asiento != null) {
            asientoService.invertirAsiento(asiento);
            mostrarComprobantesPeriodo();
        }
    }

    /**
     * Duplica el asiento seleccionado (implementación ejemplo).
     */
    private void duplicar(ActionEvent e) {
        var asiento = getSelectedAsiento();
        if (asiento != null) {
            asientoService.duplicarAsiento(asiento);
            mostrarComprobantesPeriodo();
        }
    }

    /**
     * Busca comprobantes (implementación ejemplo).
     */
    private void buscar(ActionEvent e) {
        String query = JOptionPane.showInputDialog(this, "Buscar por descripción o número:");
        if (query != null && !query.isBlank()) {
            var resultados = asientoService.buscar(query);
            updateTableView(resultados);
        }
    }

    /**
     * Crea un nuevo comprobante a partir del seleccionado (implementación ejemplo).
     */
    private void nuevoAPartirDe(ActionEvent e) {
        var asiento = getSelectedAsiento();
        if (asiento != null) {
            asientoService.nuevoAPartirDe(asiento);
            mostrarComprobantesPeriodo();
        }
    }

    /**
     * Muestra los comprobantes del periodo actual.
     */
    private void btnMostrarComprobantesPeriodo(ActionEvent e) {
        mostrarComprobantesPeriodo();
    }

    /**
     * Filtra y muestra los comprobantes del periodo.
     */
    private void mostrarComprobantesPeriodo() {
        Periodo periodo = AppContext.getInstance().getSession().getPeriodo();
        java.util.List<Asiento> listaDeAsientos = asientoService.findAll().stream()
                .filter(a -> estaEntreFechas(a.getFecha(), periodo.getFechaInicio(), periodo.getFechaFin()))
                .toList();
        updateTableView(listaDeAsientos);
    }

    /**
     * Actualiza la tabla con la lista de asientos.
     */
    void updateTableView(java.util.List<Asiento> listaDeAsientos) {
        var model = (DefaultTableModel) this.tableComprobantes.getModel();
        model.setRowCount(0); // Limpia la tabla antes de agregar
        for (Asiento asiento : listaDeAsientos) {
            model.addRow(new Object[]{
                asiento.getNro(),
                asiento.getDescripcion(),
                asiento.getEstadoAsiento().getDescripcion(),
                asiento.getFecha(),
                asiento.getSubSistema().getDescripcion(),
                asiento.getWhoModified(),
                asiento.getUnidad().getCodigo()
            });
        }
        updateLabelCount();
    }

    /**
     * Muestra todos los comprobantes.
     */
    private void btnMostrarTodos(ActionEvent e) {
        java.util.List<Asiento> listaDeAsientos = asientoService.findAll();
        updateTableView(listaDeAsientos);
    }

    /**
     * Actualiza el contador de comprobantes.
     */
    void updateLabelCount() {
        label2.setText(String.valueOf(tableComprobantes.getRowCount()));
    }

    /**
     * Marca el asiento como confirmado.
     */
    private void asentar(ActionEvent e) {
        var asiento = getSelectedAsiento();
        if (asiento != null && asiento.getEstadoAsiento() != EstadoAsiento.CONFIRMADO) {
            asiento.setEstadoAsiento(EstadoAsiento.CONFIRMADO);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
	jScrollPane1 = new JScrollPane();
	tableComprobantes = new JTable();
	panel2 = new JPanel();
	label2 = new JLabel();
	panel1 = new JPanel();
	label1 = new JLabel();
	toolBar1 = new JToolBar();
	btnMostrarComprobantesPeriodo = new JButton();
	eButton1 = new EButton();
	btnMostrarTodos = new EButton();
	eButton3 = new EButton();
	popupMenu1 = new JPopupMenu();
	menuItemNuevoComprobante = new JMenuItem();
	menuItemNuevoAPartirDe = new JMenuItem();
	menuItemBuscar = new JMenuItem();
	menuItemDuplicar = new JMenuItem();
	menuItemInvertir = new JMenuItem();
	menuItemExportar = new JMenuItem();
	menuItemImportar = new JMenuItem();
	menuItemInvTodosLosComp = new JMenuItem();
	menuItemInvSaldosUnidad = new JMenuItem();
	menuItemValidar = new JMenuItem();
	menuItemTerminar = new JMenuItem();
	menuItemAsentar = new JMenuItem();
	menuItemImprimir = new JMenuItem();
	menuItemAsentarCompPeriodo = new JMenuItem();
	menuItemTermCompPeriodo = new JMenuItem();
	menuItemEliminar = new JMenuItem();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new MigLayout(
	    "fill,insets 0,hidemode 3,gap 5 5",
	    // columns
	    "[fill]",
	    // rows
	    "[fill]" +
	    "[]"));

	//======== jScrollPane1 ========
	{
	    jScrollPane1.setComponentPopupMenu(popupMenu1);

	    //---- tableComprobantes ----
	    tableComprobantes.setModel(new DefaultTableModel(
		new Object[][] {
		},
		new String[] {
		    "Nro.", "Descripci\u00f3n", "Estado", "Fecha", "Subsistema", "Usuario", "Unidad"
		}
	    ) {
		boolean[] columnEditable = new boolean[] {
		    false, false, false, false, false, false, false
		};
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
		    return columnEditable[columnIndex];
		}
	    });
	    {
		TableColumnModel cm = tableComprobantes.getColumnModel();
		cm.getColumn(1).setPreferredWidth(500);
	    }
	    tableComprobantes.setFillsViewportHeight(true);
	    tableComprobantes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    tableComprobantes.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	    tableComprobantes.setComponentPopupMenu(popupMenu1);
	    tableComprobantes.setAutoCreateRowSorter(true);
	    jScrollPane1.setViewportView(tableComprobantes);
	}
	add(jScrollPane1, "cell 0 0 1 2");

	//======== panel2 ========
	{
	    panel2.setPreferredSize(new Dimension(955, 25));
	    panel2.setLayout(new BorderLayout());

	    //---- label2 ----
	    label2.setText("text");
	    panel2.add(label2, BorderLayout.CENTER);
	}
	add(panel2, "south");

	//======== panel1 ========
	{
	    panel1.setPreferredSize(new Dimension(452, 30));

	    //---- label1 ----
	    label1.setText("COMPROBANTEDS DE OPERACIONES");
	    label1.setFont(new Font("Segoe UI", Font.BOLD, 18));

	    GroupLayout panel1Layout = new GroupLayout(panel1);
	    panel1.setLayout(panel1Layout);
	    panel1Layout.setHorizontalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(label1)
			.addContainerGap(629, Short.MAX_VALUE))
	    );
	    panel1Layout.setVerticalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(label1)
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	    );
	}
	add(panel1, "north,wmin pref,hmin pref");

	//======== toolBar1 ========
	{

	    //---- btnMostrarComprobantesPeriodo ----
	    btnMostrarComprobantesPeriodo.setText("Mostrar Los del Per\u00edodo Actual");
	    btnMostrarComprobantesPeriodo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    btnMostrarComprobantesPeriodo.setSelected(true);
	    btnMostrarComprobantesPeriodo.addActionListener(e -> btnMostrarComprobantesPeriodo(e));
	    toolBar1.add(btnMostrarComprobantesPeriodo);

	    //---- eButton1 ----
	    eButton1.setText("[...]");
	    toolBar1.add(eButton1);

	    //---- btnMostrarTodos ----
	    btnMostrarTodos.setText("Mostrar Todos");
	    btnMostrarTodos.addActionListener(e -> btnMostrarTodos(e));
	    toolBar1.add(btnMostrarTodos);

	    //---- eButton3 ----
	    eButton3.setText("Imprimir");
	    toolBar1.add(eButton3);
	}
	add(toolBar1, "north");

	//======== popupMenu1 ========
	{

	    //---- menuItemNuevoComprobante ----
	    menuItemNuevoComprobante.setText("Nuevo");
	    menuItemNuevoComprobante.addActionListener(e -> nuevoComprobante(e));
	    popupMenu1.add(menuItemNuevoComprobante);

	    //---- menuItemNuevoAPartirDe ----
	    menuItemNuevoAPartirDe.setText("Nuevo a partir de ...");
	    menuItemNuevoAPartirDe.addActionListener(e -> nuevoAPartirDe(e));
	    popupMenu1.add(menuItemNuevoAPartirDe);

	    //---- menuItemBuscar ----
	    menuItemBuscar.setText("Buscar");
	    menuItemBuscar.addActionListener(e -> buscar(e));
	    popupMenu1.add(menuItemBuscar);
	    popupMenu1.addSeparator();

	    //---- menuItemDuplicar ----
	    menuItemDuplicar.setText("Duplicar");
	    menuItemDuplicar.setEnabled(false);
	    menuItemDuplicar.addActionListener(e -> duplicar(e));
	    popupMenu1.add(menuItemDuplicar);

	    //---- menuItemInvertir ----
	    menuItemInvertir.setText("Invertir");
	    menuItemInvertir.setEnabled(false);
	    menuItemInvertir.addActionListener(e -> invertir(e));
	    popupMenu1.add(menuItemInvertir);

	    //---- menuItemExportar ----
	    menuItemExportar.setText("Exportar");
	    menuItemExportar.setEnabled(false);
	    menuItemExportar.addActionListener(e -> exportar(e));
	    popupMenu1.add(menuItemExportar);

	    //---- menuItemImportar ----
	    menuItemImportar.setText("Importar");
	    menuItemImportar.addActionListener(e -> importar(e));
	    popupMenu1.add(menuItemImportar);
	    popupMenu1.addSeparator();

	    //---- menuItemInvTodosLosComp ----
	    menuItemInvTodosLosComp.setText("Invertir Todos Los Comprobantes Asentados");
	    menuItemInvTodosLosComp.setEnabled(false);
	    menuItemInvTodosLosComp.addActionListener(e -> invTodosLosComp(e));
	    popupMenu1.add(menuItemInvTodosLosComp);

	    //---- menuItemInvSaldosUnidad ----
	    menuItemInvSaldosUnidad.setText("Invertir Saldos de la Unidad");
	    menuItemInvSaldosUnidad.setEnabled(false);
	    menuItemInvSaldosUnidad.addActionListener(e -> invSaldosUnidad(e));
	    popupMenu1.add(menuItemInvSaldosUnidad);
	    popupMenu1.addSeparator();

	    //---- menuItemValidar ----
	    menuItemValidar.setText("Validar");
	    menuItemValidar.setEnabled(false);
	    menuItemValidar.addActionListener(e -> validar(e));
	    popupMenu1.add(menuItemValidar);

	    //---- menuItemTerminar ----
	    menuItemTerminar.setText("Terminar");
	    menuItemTerminar.setEnabled(false);
	    menuItemTerminar.addActionListener(e -> terminar(e));
	    popupMenu1.add(menuItemTerminar);

	    //---- menuItemAsentar ----
	    menuItemAsentar.setText("Asentar");
	    menuItemAsentar.setEnabled(false);
	    menuItemAsentar.addActionListener(e -> asentar(e));
	    popupMenu1.add(menuItemAsentar);

	    //---- menuItemImprimir ----
	    menuItemImprimir.setText("Imprimir");
	    menuItemImprimir.setEnabled(false);
	    popupMenu1.add(menuItemImprimir);
	    popupMenu1.addSeparator();

	    //---- menuItemAsentarCompPeriodo ----
	    menuItemAsentarCompPeriodo.setText("Asentar Comprobantes del Per\u00edodo");
	    menuItemAsentarCompPeriodo.setEnabled(false);
	    menuItemAsentarCompPeriodo.addActionListener(e -> asentarCompPeriodo(e));
	    popupMenu1.add(menuItemAsentarCompPeriodo);

	    //---- menuItemTermCompPeriodo ----
	    menuItemTermCompPeriodo.setText("Terminar Comprobantes del Per\u00edodo");
	    menuItemTermCompPeriodo.setEnabled(false);
	    menuItemTermCompPeriodo.addActionListener(e -> termCompPeriodo(e));
	    popupMenu1.add(menuItemTermCompPeriodo);
	    popupMenu1.addSeparator();

	    //---- menuItemEliminar ----
	    menuItemEliminar.setText("Eliminar");
	    menuItemEliminar.setEnabled(false);
	    menuItemEliminar.addActionListener(e -> eliminar(e));
	    popupMenu1.add(menuItemEliminar);
	}
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jScrollPane1;
    private JTable tableComprobantes;
    private JPanel panel2;
    private JLabel label2;
    private JPanel panel1;
    private JLabel label1;
    private JToolBar toolBar1;
    private JButton btnMostrarComprobantesPeriodo;
    private EButton eButton1;
    private EButton btnMostrarTodos;
    private EButton eButton3;
    private JPopupMenu popupMenu1;
    private JMenuItem menuItemNuevoComprobante;
    private JMenuItem menuItemNuevoAPartirDe;
    private JMenuItem menuItemBuscar;
    private JMenuItem menuItemDuplicar;
    private JMenuItem menuItemInvertir;
    private JMenuItem menuItemExportar;
    private JMenuItem menuItemImportar;
    private JMenuItem menuItemInvTodosLosComp;
    private JMenuItem menuItemInvSaldosUnidad;
    private JMenuItem menuItemValidar;
    private JMenuItem menuItemTerminar;
    private JMenuItem menuItemAsentar;
    private JMenuItem menuItemImprimir;
    private JMenuItem menuItemAsentarCompPeriodo;
    private JMenuItem menuItemTermCompPeriodo;
    private JMenuItem menuItemEliminar;
    // End of variables declaration//GEN-END:variables
}
