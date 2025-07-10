package com.univsoftdev.econova.config.view;

import javax.swing.tree.*;
import com.univsoftdev.econova.core.system.Form;
import java.awt.*;
import javax.swing.*;

public class FormAreas extends Form  {

    private static final long serialVersionUID = 1175200431558458341L;
    public FormAreas() {
	initComponents();
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.splitPane1 = new JSplitPane();
	this.scrollPane1 = new JScrollPane();
	this.tree1 = new JTree();
	this.tabbedPane1 = new JTabbedPane();
	this.panel2 = new JPanel();

	//======== this ========
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    this.label1.setText("\u00c1reas"); //NOI18N
	    this.panel1.add(this.label1, BorderLayout.WEST);
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== splitPane1 ========
	{
	    this.splitPane1.setDividerLocation(207);

	    //======== scrollPane1 ========
	    {

		//---- tree1 ----
		this.tree1.setModel(new DefaultTreeModel(
		    new DefaultMutableTreeNode("\u00c1reas") { //NOI18N
			{
			}
		    }));
		this.scrollPane1.setViewportView(this.tree1);
	    }
	    this.splitPane1.setLeftComponent(this.scrollPane1);

	    //======== tabbedPane1 ========
	    {

		//======== panel2 ========
		{
		    this.panel2.setLayout(new BorderLayout());
		}
		this.tabbedPane1.addTab("Propiedades", this.panel2); //NOI18N
	    }
	    this.splitPane1.setRightComponent(this.tabbedPane1);
	}
	add(this.splitPane1, BorderLayout.CENTER);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JSplitPane splitPane1;
    private JScrollPane scrollPane1;
    private JTree tree1;
    private JTabbedPane tabbedPane1;
    private JPanel panel2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
