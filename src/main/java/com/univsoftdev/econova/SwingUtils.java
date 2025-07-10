package com.univsoftdev.econova;

import javax.swing.text.JTextComponent;

public class SwingUtils {
    
    public static String getValue(JTextComponent textField) {
        return textField.getText().trim();
    }
}
