package com.univsoftdev.econova.core.component;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import raven.modal.component.Modal;
import raven.modal.ModalDialog;

public class CustomModal extends Modal {

    public CustomModal() {
        initComponents();
    }

    private void close(ActionEvent e) {
        ModalDialog.closeModal(this.getId());
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panelTitle = new JPanel();
	this.buttonClose = new JButton();
	this.labelTitle = new JLabel();
	this.panelContent = new JPanel();

	//======== this ========
	setLayout(new BorderLayout());

	//======== panelTitle ========
	{
	    this.panelTitle.setBorder(new EmptyBorder(5, 5, 5, 5));
	    this.panelTitle.setBackground(new Color(0x3333ff));
	    this.panelTitle.setMinimumSize(new Dimension(33, 30));
	    this.panelTitle.setLayout(new BorderLayout());

	    //---- buttonClose ----
	    this.buttonClose.setText("X"); //NOI18N
	    this.buttonClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.buttonClose.addActionListener(e -> close(e));
	    this.panelTitle.add(this.buttonClose, BorderLayout.EAST);

	    //---- labelTitle ----
	    this.labelTitle.setText("text"); //NOI18N
	    this.panelTitle.add(this.labelTitle, BorderLayout.WEST);
	}
	add(this.panelTitle, BorderLayout.NORTH);

	//======== panelContent ========
	{
	    this.panelContent.setLayout(new BorderLayout());
	}
	add(this.panelContent, BorderLayout.CENTER);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panelTitle;
    private JButton buttonClose;
    private JLabel labelTitle;
    private JPanel panelContent;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
