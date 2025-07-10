package com.univsoftdev.econova.config.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.system.Form;

public class FormConfig extends Form {

    private static final long serialVersionUID = 2272610229260855261L;

    public FormConfig() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.splitPane1 = new JSplitPane();
	this.panel2 = new JPanel();
	this.scrollPane1 = new JScrollPane();
	this.list1 = new JList<>();
	this.panel3 = new JPanel();
	this.panel4 = new JPanel();
	this.eButton1 = new EButton();
	this.eButton2 = new EButton();

	//======== this ========
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
	    this.panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    this.label1.setText("Configuraci\u00f3n"); //NOI18N
	    this.panel1.add(this.label1, BorderLayout.WEST);
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== splitPane1 ========
	{
	    this.splitPane1.setDividerLocation(208);
	    this.splitPane1.setBorder(new EmptyBorder(5, 5, 5, 5));

	    //======== panel2 ========
	    {
		this.panel2.setLayout(new BorderLayout());

		//======== scrollPane1 ========
		{

		    //---- list1 ----
		    this.list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    this.list1.setVisibleRowCount(10);
		    this.list1.setModel(new AbstractListModel<String>() {
			String[] values = {
			    "General" //NOI18N
			};
			@Override
			public int getSize() { return this.values.length; }
			@Override
			public String getElementAt(int i) { return this.values[i]; }
		    });
		    this.scrollPane1.setViewportView(this.list1);
		}
		this.panel2.add(this.scrollPane1, BorderLayout.CENTER);
	    }
	    this.splitPane1.setLeftComponent(this.panel2);
	}
	add(this.splitPane1, BorderLayout.CENTER);

	//======== panel3 ========
	{
	    this.panel3.setLayout(new BorderLayout());

	    //======== panel4 ========
	    {
		this.panel4.setBorder(new EmptyBorder(5, 5, 5, 5));

		//---- eButton1 ----
		this.eButton1.setText("text"); //NOI18N

		//---- eButton2 ----
		this.eButton2.setText("text"); //NOI18N

		GroupLayout panel4Layout = new GroupLayout(this.panel4);
		panel4.setLayout(panel4Layout);
		panel4Layout.setHorizontalGroup(
		    panel4Layout.createParallelGroup()
			.addGroup(panel4Layout.createSequentialGroup()
			    .addComponent(this.eButton2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(this.eButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel4Layout.setVerticalGroup(
		    panel4Layout.createParallelGroup()
			.addGroup(panel4Layout.createSequentialGroup()
			    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(this.eButton2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.eButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addGap(0, 0, Short.MAX_VALUE))
		);
	    }
	    this.panel3.add(this.panel4, BorderLayout.EAST);
	}
	add(this.panel3, BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel label1;
    private JSplitPane splitPane1;
    private JPanel panel2;
    private JScrollPane scrollPane1;
    private JList<String> list1;
    private JPanel panel3;
    private JPanel panel4;
    private EButton eButton1;
    private EButton eButton2;
    // End of variables declaration//GEN-END:variables
}
