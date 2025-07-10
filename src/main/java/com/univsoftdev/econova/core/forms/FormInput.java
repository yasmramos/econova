package com.univsoftdev.econova.core.forms;

import net.miginfocom.swing.MigLayout;
import com.univsoftdev.econova.core.system.Form;

import javax.swing.*;

public class FormInput extends Form {

    public FormInput() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel text = new JLabel("Input");
        add(text);
    }
}
