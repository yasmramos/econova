package com.univsoftdev.econova.core.system;

import java.awt.*;
import javax.swing.*;

/**
 * @author UnivSoftDev
 */
public class Form extends JPanel {

    private static final long serialVersionUID = 7998267622355069731L;
    private transient LookAndFeel oldTheme = UIManager.getLookAndFeel();

    public Form() {
        initComponents();
        this.init();
    }

    private void init() {
    }

    public void formInit() {
    }

    public void formOpen() {
    }

    public void formRefresh() {
    }

    public final void formCheck() {
        if (oldTheme != UIManager.getLookAndFeel()) {
            oldTheme = UIManager.getLookAndFeel();
            SwingUtilities.updateComponentTreeUI(this);
        }
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off

	//======== this ========
	setLayout(null);

	setPreferredSize(new Dimension(400, 300));
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
