package com.univsoftdev.econova.core.component;

import java.awt.*;
import javax.swing.*;

public class ModernCard extends JPanel {

    private JLabel lblTitle;
    private JLabel lblDescription;
    private JLabel lblIcon;
    private Color hoverColor = new Color(245, 245, 245);
    private Color defaultColor = Color.WHITE;
    private Color accentColor = new Color(245, 245, 245);
    
    public ModernCard() {
        initComponents();
        setupEffects();
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off

	//======== this ========
	setLayout(new BorderLayout());
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void setupEffects() {
        
    }
}
