package com.univsoftdev.econova.contabilidad.views.clasificador;

import com.univsoftdev.econova.AppContext;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.service.PlanDeCuentasService;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.DialogUtils;
import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormClasificador extends Form {
    
    private static final long serialVersionUID = -7448497926761135593L;
    private static final Logger LOGGER = LoggerFactory.getLogger(FormClasificador.class);
    private final PlanDeCuentasService planDeCuentasService;
    
    public FormClasificador() {
        initComponents();
        planDeCuentasService = AppContext.getInstance().getInjector().get(PlanDeCuentasService.class);
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
                    var cuenta = (Cuenta) selectedNode.getUserObject();
                    textFieldClave.setText(cuenta.getCodigo());
                    textFieldDescripcion.setText(cuenta.getNombre());
                    textFieldNaturaleza.setText(cuenta.getNaturaleza().name());
                    textFieldTipo.setText(cuenta.getTipoCuenta().name());
                    if (cuenta.getSubCuentas().isEmpty()) {
                        textFieldApertura.setText("");
                    } else {
                        textFieldApertura.setText(cuenta.getTipoApertura().getDescripcion());
                    }
                    textFieldMoneda.setText(cuenta.getMoneda().getSymbol());
                }else{
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
                Cuenta cuenta = (Cuenta) selectedNode.getUserObject();
                //Si la cuenta tiene apertura , muestro dialogo para añadir mas subcuentas
                if (cuenta.isApertura()) {
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
        
    }
    
    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	panel1 = new JPanel();
	label1 = new JLabel();
	toolBar1 = new JToolBar();
	buttonImportar = new JButton();
	buttonExportar = new JButton();
	splitPane1 = new JSplitPane();
	scrollPane1 = new JScrollPane();
	tree1 = new JTree();
	tabbedPane1 = new JTabbedPane();
	panel2 = new JPanel();
	label2 = new JLabel();
	label3 = new JLabel();
	label4 = new JLabel();
	label5 = new JLabel();
	textFieldFormato = new JTextField();
	textFieldEstructura = new JTextField();
	textFieldLongitud = new JTextField();
	textFieldAnalisisEn = new JTextField();
	panel3 = new JPanel();
	label6 = new JLabel();
	label7 = new JLabel();
	label8 = new JLabel();
	label9 = new JLabel();
	label10 = new JLabel();
	textFieldClave = new JTextField();
	textFieldDescripcion = new JTextField();
	textFieldNaturaleza = new JTextField();
	textFieldApertura = new JTextField();
	textFieldMoneda = new JTextField();
	button1 = new JButton();
	label11 = new JLabel();
	textFieldTipo = new JTextField();
	panel4 = new JPanel();
	panel5 = new JPanel();
	toolBar2 = new JToolBar();
	eButton1 = new EButton();
	eButton2 = new EButton();
	eButton3 = new EButton();
	eButton4 = new EButton();
	eButton5 = new EButton();
	eButton7 = new EButton();
	label17 = new JLabel();
	comboBox1 = new JComboBox();
	scrollPane2 = new JScrollPane();
	table1 = new JTable();
	panel6 = new JPanel();
	panel7 = new JPanel();
	labelDebito = new JLabel();
	label13 = new JLabel();
	label14 = new JLabel();
	labelCredito = new JLabel();
	label16 = new JLabel();
	labelSaldoVariacion = new JLabel();
	panel8 = new JPanel();
	panel9 = new JPanel();
	popupMenu1 = new JPopupMenu();
	menuItemApertura = new JMenuItem();
	menuItemMostrarInactivas = new JMenuItem();
	menuItemImprimir = new JMenuItem();
	menuItemExportarAExcel = new JMenuItem();

	//======== this ========
	setLayout(new MigLayout(
	    "fill,hidemode 3",
	    // columns
	    "[fill]",
	    // rows
	    "[]"));

	//======== panel1 ========
	{
	    panel1.setPreferredSize(new Dimension(61, 25));

	    //---- label1 ----
	    label1.setText("CLASIFICADOR DE CUENTAS");
	    label1.setFont(new Font("Segoe UI", Font.BOLD, 18));

	    //======== toolBar1 ========
	    {
		toolBar1.setPreferredSize(new Dimension(104, 25));
		toolBar1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		//---- buttonImportar ----
		buttonImportar.setText("Importar");
		buttonImportar.addActionListener(e -> importarActionPerformed(e));
		toolBar1.add(buttonImportar);

		//---- buttonExportar ----
		buttonExportar.setText("Exportar");
		toolBar1.add(buttonExportar);
	    }

	    GroupLayout panel1Layout = new GroupLayout(panel1);
	    panel1.setLayout(panel1Layout);
	    panel1Layout.setHorizontalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(label1)
			.addContainerGap(576, Short.MAX_VALUE))
		    .addGroup(panel1Layout.createSequentialGroup()
			.addComponent(toolBar1, GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
			.addContainerGap())
	    );
	    panel1Layout.setVerticalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addComponent(label1)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(toolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	    );
	}
	add(panel1, "north,wmin pref,height pref");

	//======== splitPane1 ========
	{
	    splitPane1.setDividerLocation(300);

	    //======== scrollPane1 ========
	    {

		//---- tree1 ----
		tree1.setModel(new DefaultTreeModel(
		    new DefaultMutableTreeNode("Cuentas") {
			{
			}
		    }));
		tree1.setComponentPopupMenu(popupMenu1);
		tree1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		tree1.addTreeSelectionListener(e -> tree1ValueChanged(e));
		scrollPane1.setViewportView(tree1);
	    }
	    splitPane1.setLeftComponent(scrollPane1);

	    //======== tabbedPane1 ========
	    {

		//======== panel2 ========
		{

		    //---- label2 ----
		    label2.setText("Formato");

		    //---- label3 ----
		    label3.setText("Estructura");

		    //---- label4 ----
		    label4.setText("Longitud");

		    //---- label5 ----
		    label5.setText("An\u00e1lisis en");

		    //---- textFieldFormato ----
		    textFieldFormato.setEditable(false);
		    textFieldFormato.setText("CLASIFICADOR DE CUENTAS");

		    //---- textFieldEstructura ----
		    textFieldEstructura.setEditable(false);
		    textFieldEstructura.setText("CTA.SCTA.ANAL.Anal.CTRL");

		    //---- textFieldLongitud ----
		    textFieldLongitud.setEditable(false);
		    textFieldLongitud.setText("15");

		    //---- textFieldAnalisisEn ----
		    textFieldAnalisisEn.setEditable(false);
		    textFieldAnalisisEn.setText("CUALQUIER NIVEL");

		    //======== panel3 ========
		    {

			//---- label6 ----
			label6.setText("Clave");

			//---- label7 ----
			label7.setText("Descripci\u00f3n");

			//---- label8 ----
			label8.setText("Naturaleza");

			//---- label9 ----
			label9.setText("Apertura por");

			//---- label10 ----
			label10.setText("Moneda");

			//---- textFieldClave ----
			textFieldClave.setEditable(false);

			//---- textFieldDescripcion ----
			textFieldDescripcion.setEditable(false);

			//---- textFieldNaturaleza ----
			textFieldNaturaleza.setEditable(false);

			//---- textFieldApertura ----
			textFieldApertura.setEditable(false);

			//---- textFieldMoneda ----
			textFieldMoneda.setEditable(false);

			//---- button1 ----
			button1.setText("...");
			button1.setEnabled(false);

			//---- label11 ----
			label11.setText("Tipo");

			//---- textFieldTipo ----
			textFieldTipo.setEditable(false);
			textFieldTipo.setEnabled(false);

			GroupLayout panel3Layout = new GroupLayout(panel3);
			panel3.setLayout(panel3Layout);
			panel3Layout.setHorizontalGroup(
			    panel3Layout.createParallelGroup()
				.addGroup(panel3Layout.createSequentialGroup()
				    .addContainerGap()
				    .addGroup(panel3Layout.createParallelGroup()
					.addGroup(panel3Layout.createSequentialGroup()
					    .addGroup(panel3Layout.createParallelGroup()
						.addComponent(label6)
						.addComponent(label7)
						.addComponent(label8))
					    .addGap(47, 47, 47)
					    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(textFieldClave, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(textFieldDescripcion, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(textFieldNaturaleza, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)))
					.addGroup(panel3Layout.createSequentialGroup()
					    .addGroup(panel3Layout.createParallelGroup()
						.addComponent(label9)
						.addComponent(label10)
						.addComponent(label11))
					    .addGap(42, 42, 42)
					    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(textFieldApertura, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(textFieldMoneda, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(textFieldTipo, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
					    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(button1, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)))
				    .addContainerGap(15, Short.MAX_VALUE))
			);
			panel3Layout.setVerticalGroup(
			    panel3Layout.createParallelGroup()
				.addGroup(panel3Layout.createSequentialGroup()
				    .addGap(15, 15, 15)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(label6)
					.addComponent(textFieldClave, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(label7)
					.addComponent(textFieldDescripcion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(label8)
					.addComponent(textFieldNaturaleza, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(textFieldTipo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(label11))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(label9)
					.addComponent(textFieldApertura, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(button1))
				    .addGap(18, 18, 18)
				    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(label10)
					.addComponent(textFieldMoneda, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				    .addGap(19, 19, 19))
			);
		    }

		    GroupLayout panel2Layout = new GroupLayout(panel2);
		    panel2.setLayout(panel2Layout);
		    panel2Layout.setHorizontalGroup(
			panel2Layout.createParallelGroup()
			    .addGroup(panel2Layout.createSequentialGroup()
				.addGap(26, 26, 26)
				.addGroup(panel2Layout.createParallelGroup()
				    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
					.addGroup(GroupLayout.Alignment.LEADING, panel2Layout.createSequentialGroup()
					    .addComponent(label5)
					    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					    .addComponent(textFieldAnalisisEn, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE))
					.addGroup(panel2Layout.createSequentialGroup()
					    .addComponent(label4)
					    .addGap(64, 64, 64)
					    .addComponent(textFieldLongitud, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE))
					.addGroup(panel2Layout.createSequentialGroup()
					    .addGroup(panel2Layout.createParallelGroup()
						.addGroup(panel2Layout.createSequentialGroup()
						    .addComponent(label3)
						    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
						    .addComponent(label2)
						    .addGap(66, 66, 66)))
					    .addGroup(panel2Layout.createParallelGroup()
						.addComponent(textFieldFormato, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
						.addComponent(textFieldEstructura))))
				    .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(57, Short.MAX_VALUE))
		    );
		    panel2Layout.setVerticalGroup(
			panel2Layout.createParallelGroup()
			    .addGroup(panel2Layout.createSequentialGroup()
				.addGap(28, 28, 28)
				.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(label2)
				    .addComponent(textFieldFormato, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(label3)
				    .addComponent(textFieldEstructura, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(label4)
				    .addComponent(textFieldLongitud, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(label5)
				    .addComponent(textFieldAnalisisEn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(26, 26, 26)
				.addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		    );
		}
		tabbedPane1.addTab("Propiedades", panel2);

		//======== panel4 ========
		{
		    panel4.setLayout(new BorderLayout());

		    //======== panel5 ========
		    {
			panel5.setLayout(new BorderLayout());

			//======== toolBar2 ========
			{

			    //---- eButton1 ----
			    eButton1.setText("Periodo Actual");
			    toolBar2.add(eButton1);

			    //---- eButton2 ----
			    eButton2.setText("Rango");
			    toolBar2.add(eButton2);

			    //---- eButton3 ----
			    eButton3.setText("Todo");
			    toolBar2.add(eButton3);

			    //---- eButton4 ----
			    eButton4.setText("Abrir");
			    toolBar2.add(eButton4);

			    //---- eButton5 ----
			    eButton5.setText("Desglose");
			    toolBar2.add(eButton5);

			    //---- eButton7 ----
			    eButton7.setText("Imprimir");
			    toolBar2.add(eButton7);

			    //---- label17 ----
			    label17.setText("Unidad");
			    toolBar2.add(label17);
			    toolBar2.add(comboBox1);
			}
			panel5.add(toolBar2, BorderLayout.WEST);
		    }
		    panel4.add(panel5, BorderLayout.NORTH);

		    //======== scrollPane2 ========
		    {

			//---- table1 ----
			table1.setModel(new DefaultTableModel(
			    new Object[][] {
			    },
			    new String[] {
				"Fecha", "Nro", "Descripci\u00f3n", "Debe", "Haber", "Saldo", "SubSistema", "Unidad"
			    }
			));
			scrollPane2.setViewportView(table1);
		    }
		    panel4.add(scrollPane2, BorderLayout.CENTER);

		    //======== panel6 ========
		    {
			panel6.setLayout(new BorderLayout());

			//======== panel7 ========
			{
			    panel7.setBorder(new CompoundBorder(
				new TitledBorder("Totales"),
				new EmptyBorder(5, 5, 5, 5)));

			    //---- labelDebito ----
			    labelDebito.setText("0.00");

			    //---- label13 ----
			    label13.setText("Saldo/ Variaci\u00f3n");

			    //---- label14 ----
			    label14.setText("D\u00e9bito");

			    //---- labelCredito ----
			    labelCredito.setText("0.00");

			    //---- label16 ----
			    label16.setText("Cr\u00e9dito");

			    //---- labelSaldoVariacion ----
			    labelSaldoVariacion.setText("0.00");

			    GroupLayout panel7Layout = new GroupLayout(panel7);
			    panel7.setLayout(panel7Layout);
			    panel7Layout.setHorizontalGroup(
				panel7Layout.createParallelGroup()
				    .addGroup(panel7Layout.createSequentialGroup()
					.addGroup(panel7Layout.createParallelGroup()
					    .addComponent(label13, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
					    .addComponent(label16)
					    .addComponent(label14))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(panel7Layout.createParallelGroup()
					    .addComponent(labelSaldoVariacion)
					    .addComponent(labelCredito)
					    .addComponent(labelDebito, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
			    );
			    panel7Layout.setVerticalGroup(
				panel7Layout.createParallelGroup()
				    .addGroup(panel7Layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(panel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(labelDebito)
					    .addComponent(label14))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(panel7Layout.createParallelGroup()
					    .addComponent(labelCredito)
					    .addComponent(label16))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(panel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(label13)
					    .addComponent(labelSaldoVariacion))
					.addContainerGap())
			    );
			}
			panel6.add(panel7, BorderLayout.CENTER);
		    }
		    panel4.add(panel6, BorderLayout.SOUTH);
		}
		tabbedPane1.addTab("Historia", panel4);

		//======== panel8 ========
		{
		    panel8.setLayout(new BorderLayout());
		}
		tabbedPane1.addTab("Estado", panel8);

		//======== panel9 ========
		{
		    panel9.setLayout(new BorderLayout());
		}
		tabbedPane1.addTab("Desglose Por Monedas", panel9);
	    }
	    splitPane1.setRightComponent(tabbedPane1);
	}
	add(splitPane1, "dock center");

	//======== popupMenu1 ========
	{

	    //---- menuItemApertura ----
	    menuItemApertura.setText("Apertura");
	    menuItemApertura.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    menuItemApertura.addActionListener(e -> aperturaActionPerformed(e));
	    popupMenu1.add(menuItemApertura);

	    //---- menuItemMostrarInactivas ----
	    menuItemMostrarInactivas.setText("Mostrar Inactivas");
	    menuItemMostrarInactivas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    popupMenu1.add(menuItemMostrarInactivas);

	    //---- menuItemImprimir ----
	    menuItemImprimir.setText("Imprimir");
	    menuItemImprimir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    popupMenu1.add(menuItemImprimir);

	    //---- menuItemExportarAExcel ----
	    menuItemExportarAExcel.setText("Exportar a Excel");
	    menuItemExportarAExcel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    popupMenu1.add(menuItemExportarAExcel);
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
