package com.univsoftdev.econova.contabilidad.views.clasificador;

import com.univsoftdev.econova.core.Injector;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.service.PlanDeCuentasService;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.DialogUtils;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import net.miginfocom.swing.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormClasificador extends Form {

    private static final long serialVersionUID = -7448497926761135593L;
    private static final Logger LOGGER = LoggerFactory.getLogger(FormClasificador.class);
    private final PlanDeCuentasService planDeCuentasService;

    public FormClasificador() {
        initComponents();
        planDeCuentasService = Injector.get(PlanDeCuentasService.class);
        updateTree();
    }

    @Override
    public void formRefresh() {
        updateTree();
    }

    private void tree1ValueChanged(TreeSelectionEvent e) {
        try {
            TreePath selectedPath = e.getPath();
            if (selectedPath != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                DefaultTreeModel treeModel = (DefaultTreeModel) tree1.getModel();
                DefaultMutableTreeNode rootPrincipal = (DefaultMutableTreeNode) treeModel.getRoot();

                if (selectedNode != rootPrincipal) {
                    var cuenta = (Account) selectedNode.getUserObject();
                    textFieldClave.setText(cuenta.getCode());
                    textFieldDescripcion.setText(cuenta.getName());
                    textFieldNaturaleza.setText(cuenta.getNatureOfAccount().name());
                    textFieldTipo.setText(cuenta.getAccountType().name());
                    if (cuenta.getSubAccounts().isEmpty()) {
                        textFieldApertura.setText("");
                    } else {
                        textFieldApertura.setText(cuenta.getTypeOfOpening().getDescription());
                    }
                    textFieldMoneda.setText(cuenta.getCurrency().getSymbol());
                } else {
                    textFieldClave.setText("");
                    textFieldDescripcion.setText("");
                    textFieldNaturaleza.setText("");
                    textFieldTipo.setText("");
                    textFieldMoneda.setText("");
                    textFieldApertura.setText("");
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    private void aperturaActionPerformed(ActionEvent e) {

        TreePath selectedPath = tree1.getSelectionPath();

        if (selectedPath != null) {

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            DefaultTreeModel treeModel = (DefaultTreeModel) tree1.getModel();
            DefaultMutableTreeNode rootPrincipal = (DefaultMutableTreeNode) treeModel.getRoot();

            //Si es el nodo principal muestro la opcion de añadir nuevas cuentas
            if (selectedNode == rootPrincipal) {
                DialogUtils.showModalDialog(this, new FormApertura(tree1), "Cuentas de la Apertura");
            } else {
                Account cuenta = (Account) selectedNode.getUserObject();
                //Si la cuenta tiene apertura , muestro dialogo para añadir mas subcuentas
                if (cuenta.isOpening()) {
                    DialogUtils.showModalDialog(this, new FormAperturaCuenta(tree1, planDeCuentasService, cuenta), "Aperturas de la Cuenta");
                } else {
                    //Si la cuenta no tiene apertura muestro el dialogo para seleccionar el tipo de apertura
                    DialogUtils.showModalDialog(this, new FormTipoApertura(tree1, planDeCuentasService, cuenta), "Tipo de Apertura");
                }
            }

            updateTree();
        }
    }

    private void importarActionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getAbsolutePath().endsWith("csv");
            }

            @Override
            public String getDescription() {
                return "Archivos separados por coma (CSV)";
            }
        });
        chooser.setApproveButtonText("Importar");
        int showOpenDialog = chooser.showOpenDialog(panel1);
        if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                CSVParser parse = CSVParser.parse(selectedFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
                java.util.List<CSVRecord> records = parse.getRecords();
                records.stream().forEach(r -> System.out.println(r.get(0)));
            } catch (IOException ex) {
                System.getLogger(FormClasificador.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
    }

    public void processLine(String line) {

    }

    private void exportar(ActionEvent e) {
	
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.toolBar1 = new JToolBar();
	this.buttonImportar = new JButton();
	this.buttonExportar = new JButton();
	this.splitPane1 = new JSplitPane();
	this.scrollPane1 = new JScrollPane();
	this.tree1 = new JTree();
	this.tabbedPane1 = new JTabbedPane();
	this.panel2 = new JPanel();
	this.label2 = new JLabel();
	this.label3 = new JLabel();
	this.label4 = new JLabel();
	this.label5 = new JLabel();
	this.textFieldFormato = new JTextField();
	this.textFieldEstructura = new JTextField();
	this.textFieldLongitud = new JTextField();
	this.textFieldAnalisisEn = new JTextField();
	this.panel3 = new JPanel();
	this.label6 = new JLabel();
	this.label7 = new JLabel();
	this.label8 = new JLabel();
	this.label9 = new JLabel();
	this.label10 = new JLabel();
	this.textFieldClave = new JTextField();
	this.textFieldDescripcion = new JTextField();
	this.textFieldNaturaleza = new JTextField();
	this.textFieldApertura = new JTextField();
	this.textFieldMoneda = new JTextField();
	this.button1 = new JButton();
	this.label11 = new JLabel();
	this.textFieldTipo = new JTextField();
	this.panel4 = new JPanel();
	this.panel5 = new JPanel();
	this.toolBar2 = new JToolBar();
	this.eButton1 = new EButton();
	this.eButton2 = new EButton();
	this.eButton3 = new EButton();
	this.eButton4 = new EButton();
	this.eButton5 = new EButton();
	this.eButton7 = new EButton();
	this.label17 = new JLabel();
	this.comboBox1 = new JComboBox();
	this.scrollPane2 = new JScrollPane();
	this.table1 = new JTable();
	this.panel6 = new JPanel();
	this.panel7 = new JPanel();
	this.labelDebito = new JLabel();
	this.label13 = new JLabel();
	this.label14 = new JLabel();
	this.labelCredito = new JLabel();
	this.label16 = new JLabel();
	this.labelSaldoVariacion = new JLabel();
	this.panel8 = new JPanel();
	this.panel9 = new JPanel();
	this.popupMenu1 = new JPopupMenu();
	this.menuItemApertura = new JMenuItem();
	this.menuItemMostrarInactivas = new JMenuItem();
	this.menuItemImprimir = new JMenuItem();
	this.menuItemExportarAExcel = new JMenuItem();

	//======== this ========
	setLayout(new MigLayout(
	    "fill,hidemode 3", //NOI18N
	    // columns
	    "[fill]", //NOI18N
	    // rows
	    "[]")); //NOI18N

	//======== panel1 ========
	{
	    this.panel1.setPreferredSize(new Dimension(61, 25));

	    //---- label1 ----
	    this.label1.setText("CLASIFICADOR DE CUENTAS"); //NOI18N
	    this.label1.setFont(new Font("Segoe UI", Font.BOLD, 18)); //NOI18N

	    //======== toolBar1 ========
	    {
		this.toolBar1.setPreferredSize(new Dimension(104, 25));
		this.toolBar1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		//---- buttonImportar ----
		this.buttonImportar.setText("Importar"); //NOI18N
		this.buttonImportar.addActionListener(e -> importarActionPerformed(e));
		this.toolBar1.add(this.buttonImportar);

		//---- buttonExportar ----
		this.buttonExportar.setText("Exportar"); //NOI18N
		this.buttonExportar.addActionListener(e -> exportar(e));
		this.toolBar1.add(this.buttonExportar);
	    }

	    GroupLayout panel1Layout = new GroupLayout(this.panel1);
	    panel1.setLayout(panel1Layout);
	    panel1Layout.setHorizontalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(this.label1)
			.addContainerGap(577, Short.MAX_VALUE))
		    .addGroup(panel1Layout.createSequentialGroup()
			.addComponent(this.toolBar1, GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
			.addContainerGap())
	    );
	    panel1Layout.setVerticalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addComponent(this.label1)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(this.toolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	    );
	}
	add(this.panel1, "north,wmin pref,height pref"); //NOI18N

	//======== splitPane1 ========
	{
	    this.splitPane1.setDividerLocation(300);

	    //======== scrollPane1 ========
	    {

		//---- tree1 ----
		this.tree1.setModel(new DefaultTreeModel(
		    new DefaultMutableTreeNode("Cuentas") { //NOI18N
			{
			}
		    }));
		this.tree1.setComponentPopupMenu(this.popupMenu1);
		this.tree1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.tree1.addTreeSelectionListener(e -> tree1ValueChanged(e));
		this.scrollPane1.setViewportView(this.tree1);
	    }
	    this.splitPane1.setLeftComponent(this.scrollPane1);

	    //======== tabbedPane1 ========
	    {

		//======== panel2 ========
		{

		    //---- label2 ----
		    this.label2.setText("Formato"); //NOI18N

		    //---- label3 ----
		    this.label3.setText("Estructura"); //NOI18N

		    //---- label4 ----
		    this.label4.setText("Longitud"); //NOI18N

		    //---- label5 ----
		    this.label5.setText("An\u00e1lisis en"); //NOI18N

		    //---- textFieldFormato ----
		    this.textFieldFormato.setEditable(false);
		    this.textFieldFormato.setText("CLASIFICADOR DE CUENTAS"); //NOI18N

		    //---- textFieldEstructura ----
		    this.textFieldEstructura.setEditable(false);
		    this.textFieldEstructura.setText("CTA.SCTA.ANAL.Anal.CTRL"); //NOI18N

		    //---- textFieldLongitud ----
		    this.textFieldLongitud.setEditable(false);
		    this.textFieldLongitud.setText("15"); //NOI18N

		    //---- textFieldAnalisisEn ----
		    this.textFieldAnalisisEn.setEditable(false);
		    this.textFieldAnalisisEn.setText("CUALQUIER NIVEL"); //NOI18N

		    //======== panel3 ========
		    {

			//---- label6 ----
			this.label6.setText("Clave"); //NOI18N

			//---- label7 ----
			this.label7.setText("Descripci\u00f3n"); //NOI18N

			//---- label8 ----
			this.label8.setText("Naturaleza"); //NOI18N

			//---- label9 ----
			this.label9.setText("Apertura por"); //NOI18N

			//---- label10 ----
			this.label10.setText("Moneda"); //NOI18N

			//---- textFieldClave ----
			this.textFieldClave.setEditable(false);

			//---- textFieldDescripcion ----
			this.textFieldDescripcion.setEditable(false);

			//---- textFieldNaturaleza ----
			this.textFieldNaturaleza.setEditable(false);

			//---- textFieldApertura ----
			this.textFieldApertura.setEditable(false);

			//---- textFieldMoneda ----
			this.textFieldMoneda.setEditable(false);

			//---- button1 ----
			this.button1.setText("..."); //NOI18N
			this.button1.setEnabled(false);

			//---- label11 ----
			this.label11.setText("Tipo"); //NOI18N

			//---- textFieldTipo ----
			this.textFieldTipo.setEditable(false);
			this.textFieldTipo.setEnabled(false);

			GroupLayout panel3Layout = new GroupLayout(this.panel3);
			panel3.setLayout(panel3Layout);
			panel3Layout.setHorizontalGroup(
			    panel3Layout.createParallelGroup()
				.addGroup(panel3Layout.createSequentialGroup()
				    .addContainerGap()
				    .addGroup(panel3Layout.createParallelGroup()
					.addGroup(panel3Layout.createSequentialGroup()
					    .addGroup(panel3Layout.createParallelGroup()
						.addComponent(this.label6)
						.addComponent(this.label7)
						.addComponent(this.label8))
					    .addGap(47, 47, 47)
					    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(this.textFieldClave, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(this.textFieldDescripcion, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(this.textFieldNaturaleza, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)))
					.addGroup(panel3Layout.createSequentialGroup()
					    .addGroup(panel3Layout.createParallelGroup()
						.addComponent(this.label9)
						.addComponent(this.label10)
						.addComponent(this.label11))
					    .addGap(42, 42, 42)
					    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(this.textFieldApertura, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(this.textFieldMoneda, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(this.textFieldTipo, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
					    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(this.button1, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)))
				    .addContainerGap(15, Short.MAX_VALUE))
			);
			panel3Layout.setVerticalGroup(
			    panel3Layout.createParallelGroup()
				.addGroup(panel3Layout.createSequentialGroup()
				    .addGap(15, 15, 15)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(this.label6)
					.addComponent(this.textFieldClave, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(this.label7)
					.addComponent(this.textFieldDescripcion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(this.label8)
					.addComponent(this.textFieldNaturaleza, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(this.textFieldTipo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(this.label11))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(this.label9)
					.addComponent(this.textFieldApertura, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(this.button1))
				    .addGap(18, 18, 18)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(this.label10)
					.addComponent(this.textFieldMoneda, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				    .addGap(19, 19, 19))
			);
		    }

		    GroupLayout panel2Layout = new GroupLayout(this.panel2);
		    panel2.setLayout(panel2Layout);
		    panel2Layout.setHorizontalGroup(
			panel2Layout.createParallelGroup()
			    .addGroup(panel2Layout.createSequentialGroup()
				.addGap(26, 26, 26)
				.addGroup(panel2Layout.createParallelGroup()
				    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
					.addGroup(GroupLayout.Alignment.LEADING, panel2Layout.createSequentialGroup()
					    .addComponent(this.label5)
					    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					    .addComponent(this.textFieldAnalisisEn, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE))
					.addGroup(panel2Layout.createSequentialGroup()
					    .addComponent(this.label4)
					    .addGap(64, 64, 64)
					    .addComponent(this.textFieldLongitud, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE))
					.addGroup(panel2Layout.createSequentialGroup()
					    .addGroup(panel2Layout.createParallelGroup()
						.addGroup(panel2Layout.createSequentialGroup()
						    .addComponent(this.label3)
						    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
						    .addComponent(this.label2)
						    .addGap(66, 66, 66)))
					    .addGroup(panel2Layout.createParallelGroup()
						.addComponent(this.textFieldFormato, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.textFieldEstructura))))
				    .addComponent(this.panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(72, Short.MAX_VALUE))
		    );
		    panel2Layout.setVerticalGroup(
			panel2Layout.createParallelGroup()
			    .addGroup(panel2Layout.createSequentialGroup()
				.addGap(28, 28, 28)
				.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(this.label2)
				    .addComponent(this.textFieldFormato, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(this.label3)
				    .addComponent(this.textFieldEstructura, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(this.label4)
				    .addComponent(this.textFieldLongitud, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(this.label5)
				    .addComponent(this.textFieldAnalisisEn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(26, 26, 26)
				.addComponent(this.panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		    );
		}
		this.tabbedPane1.addTab("Propiedades", this.panel2); //NOI18N

		//======== panel4 ========
		{
		    this.panel4.setLayout(new BorderLayout());

		    //======== panel5 ========
		    {
			this.panel5.setLayout(new BorderLayout());

			//======== toolBar2 ========
			{

			    //---- eButton1 ----
			    this.eButton1.setText("Periodo Actual"); //NOI18N
			    this.toolBar2.add(this.eButton1);

			    //---- eButton2 ----
			    this.eButton2.setText("Rango"); //NOI18N
			    this.toolBar2.add(this.eButton2);

			    //---- eButton3 ----
			    this.eButton3.setText("Todo"); //NOI18N
			    this.toolBar2.add(this.eButton3);

			    //---- eButton4 ----
			    this.eButton4.setText("Abrir"); //NOI18N
			    this.toolBar2.add(this.eButton4);

			    //---- eButton5 ----
			    this.eButton5.setText("Desglose"); //NOI18N
			    this.toolBar2.add(this.eButton5);

			    //---- eButton7 ----
			    this.eButton7.setText("Imprimir"); //NOI18N
			    this.toolBar2.add(this.eButton7);

			    //---- label17 ----
			    this.label17.setText("Unidad"); //NOI18N
			    this.toolBar2.add(this.label17);
			    this.toolBar2.add(this.comboBox1);
			}
			this.panel5.add(this.toolBar2, BorderLayout.WEST);
		    }
		    this.panel4.add(this.panel5, BorderLayout.NORTH);

		    //======== scrollPane2 ========
		    {

			//---- table1 ----
			this.table1.setModel(new DefaultTableModel(
			    new Object[][] {
			    },
			    new String[] {
				"Fecha", "Nro", "Descripci\u00f3n", "Debe", "Haber", "Saldo", "SubSistema", "Unidad" //NOI18N
			    }
			));
			this.scrollPane2.setViewportView(this.table1);
		    }
		    this.panel4.add(this.scrollPane2, BorderLayout.CENTER);

		    //======== panel6 ========
		    {
			this.panel6.setLayout(new BorderLayout());

			//======== panel7 ========
			{
			    this.panel7.setBorder(new CompoundBorder(
				new TitledBorder("Totales"), //NOI18N
				new EmptyBorder(5, 5, 5, 5)));

			    //---- labelDebito ----
			    this.labelDebito.setText("0.00"); //NOI18N

			    //---- label13 ----
			    this.label13.setText("Saldo/ Variaci\u00f3n"); //NOI18N

			    //---- label14 ----
			    this.label14.setText("D\u00e9bito"); //NOI18N

			    //---- labelCredito ----
			    this.labelCredito.setText("0.00"); //NOI18N

			    //---- label16 ----
			    this.label16.setText("Cr\u00e9dito"); //NOI18N

			    //---- labelSaldoVariacion ----
			    this.labelSaldoVariacion.setText("0.00"); //NOI18N

			    GroupLayout panel7Layout = new GroupLayout(this.panel7);
			    panel7.setLayout(panel7Layout);
			    panel7Layout.setHorizontalGroup(
				panel7Layout.createParallelGroup()
				    .addGroup(panel7Layout.createSequentialGroup()
					.addGroup(panel7Layout.createParallelGroup()
					    .addComponent(this.label13, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
					    .addComponent(this.label16)
					    .addComponent(this.label14))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(panel7Layout.createParallelGroup()
					    .addComponent(this.labelSaldoVariacion)
					    .addComponent(this.labelCredito)
					    .addComponent(this.labelDebito, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
			    );
			    panel7Layout.setVerticalGroup(
				panel7Layout.createParallelGroup()
				    .addGroup(panel7Layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(panel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(this.labelDebito)
					    .addComponent(this.label14))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(panel7Layout.createParallelGroup()
					    .addComponent(this.labelCredito)
					    .addComponent(this.label16))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(panel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(this.label13)
					    .addComponent(this.labelSaldoVariacion))
					.addContainerGap())
			    );
			}
			this.panel6.add(this.panel7, BorderLayout.CENTER);
		    }
		    this.panel4.add(this.panel6, BorderLayout.SOUTH);
		}
		this.tabbedPane1.addTab("Historia", this.panel4); //NOI18N

		//======== panel8 ========
		{
		    this.panel8.setLayout(new BorderLayout());
		}
		this.tabbedPane1.addTab("Estado", this.panel8); //NOI18N

		//======== panel9 ========
		{
		    this.panel9.setLayout(new BorderLayout());
		}
		this.tabbedPane1.addTab("Desglose Por Monedas", this.panel9); //NOI18N
	    }
	    this.splitPane1.setRightComponent(this.tabbedPane1);
	}
	add(this.splitPane1, "dock center"); //NOI18N

	//======== popupMenu1 ========
	{

	    //---- menuItemApertura ----
	    this.menuItemApertura.setText("Apertura"); //NOI18N
	    this.menuItemApertura.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.menuItemApertura.addActionListener(e -> aperturaActionPerformed(e));
	    this.popupMenu1.add(this.menuItemApertura);

	    //---- menuItemMostrarInactivas ----
	    this.menuItemMostrarInactivas.setText("Mostrar Inactivas"); //NOI18N
	    this.menuItemMostrarInactivas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.popupMenu1.add(this.menuItemMostrarInactivas);

	    //---- menuItemImprimir ----
	    this.menuItemImprimir.setText("Imprimir"); //NOI18N
	    this.menuItemImprimir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.popupMenu1.add(this.menuItemImprimir);

	    //---- menuItemExportarAExcel ----
	    this.menuItemExportarAExcel.setText("Exportar a Excel"); //NOI18N
	    this.menuItemExportarAExcel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.popupMenu1.add(this.menuItemExportarAExcel);
	}
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JToolBar toolBar1;
    private JButton buttonImportar;
    private JButton buttonExportar;
    private JSplitPane splitPane1;
    private JScrollPane scrollPane1;
    private JTree tree1;
    private JTabbedPane tabbedPane1;
    private JPanel panel2;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JTextField textFieldFormato;
    private JTextField textFieldEstructura;
    private JTextField textFieldLongitud;
    private JTextField textFieldAnalisisEn;
    private JPanel panel3;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JLabel label10;
    private JTextField textFieldClave;
    private JTextField textFieldDescripcion;
    private JTextField textFieldNaturaleza;
    private JTextField textFieldApertura;
    private JTextField textFieldMoneda;
    private JButton button1;
    private JLabel label11;
    private JTextField textFieldTipo;
    private JPanel panel4;
    private JPanel panel5;
    private JToolBar toolBar2;
    private EButton eButton1;
    private EButton eButton2;
    private EButton eButton3;
    private EButton eButton4;
    private EButton eButton5;
    private EButton eButton7;
    private JLabel label17;
    private JComboBox comboBox1;
    private JScrollPane scrollPane2;
    private JTable table1;
    private JPanel panel6;
    private JPanel panel7;
    private JLabel labelDebito;
    private JLabel label13;
    private JLabel label14;
    private JLabel labelCredito;
    private JLabel label16;
    private JLabel labelSaldoVariacion;
    private JPanel panel8;
    private JPanel panel9;
    private JPopupMenu popupMenu1;
    private JMenuItem menuItemApertura;
    private JMenuItem menuItemMostrarInactivas;
    private JMenuItem menuItemImprimir;
    private JMenuItem menuItemExportarAExcel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void updateTree() {
        tree1.setModel(planDeCuentasService.crearModelPlanDeCuentas());
    }
}
