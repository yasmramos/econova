package com.univsoftdev.econova.component;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.univsoftdev.econova.core.component.*;
import raven.modal.component.Modal;

public class BaseDialog extends Modal implements java.beans.Customizer {

    private static final long serialVersionUID = 1L;
    
    private Object bean;

    public BaseDialog() {
        setBackground(new Color(255, 255, 255, 192));
        initComponents();
    }
    
    @Override
    public void setObject(Object bean) {
        this.bean = bean;
    }

    private void initComponents() {//GEN-BEGIN:initComponents
	panel1 = new JPanel();
	label1 = new JLabel();
	labelButton1 = new LabelButton();
	contentPanel = new JPanel();

	//======== this ========
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
	    panel1.setBackground(new Color(0x007acc));
	    panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    label1.setText("Dialogo Modal");
	    label1.setForeground(Color.white);
	    label1.setFont(new Font("Segoe UI", Font.BOLD, 14));
	    panel1.add(label1, BorderLayout.WEST);

	    //---- labelButton1 ----
	    labelButton1.setText("X");
	    labelButton1.setFont(new Font("Segoe UI", Font.BOLD, 18));
	    labelButton1.setForeground(Color.white);
	    labelButton1.setBorder(new EmptyBorder(5, 5, 5, 5));
	    panel1.add(labelButton1, BorderLayout.EAST);
	}
	add(panel1, BorderLayout.NORTH);

	//======== contentPanel ========
	{
	    contentPanel.setLayout(new BorderLayout());
	}
	add(contentPanel, BorderLayout.CENTER);
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel label1;
    private LabelButton labelButton1;
    private JPanel contentPanel;
    // End of variables declaration//GEN-END:variables

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(JPanel contentPanel) {
        this.contentPanel = contentPanel;
        repaint();
        revalidate();
    }

}
