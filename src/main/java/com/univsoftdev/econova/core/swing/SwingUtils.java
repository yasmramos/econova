package com.univsoftdev.econova.core.swing;

import javax.swing.text.JTextComponent;

public class SwingUtils {

    private SwingUtils() {
    }
    
    public static String getValue(JTextComponent textField) {
        return textField.getText().trim();
    }
}
