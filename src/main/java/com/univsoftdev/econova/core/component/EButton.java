package com.univsoftdev.econova.core.component;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Cursor;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class EButton extends JButton {

    private static final long serialVersionUID = 2835566267444773166L;

    public EButton() {
        init();
    }

    public EButton(Icon icon) {
        super(icon);
        init();
    }

    public EButton(String text) {
        super(text);
        init();
    }

    public EButton(Action a) {
        super(a);
        init();
    }

    public EButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    private void init() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        putClientProperty(FlatClientProperties.STYLE, ""
                + "margin:4,10,4,10;"
                + "arc:12;");
    }

}
