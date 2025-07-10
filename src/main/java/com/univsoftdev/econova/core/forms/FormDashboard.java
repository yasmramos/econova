package com.univsoftdev.econova.core.forms;

import net.miginfocom.swing.MigLayout;
import com.univsoftdev.econova.core.system.Form;

import javax.swing.*;

public class FormDashboard extends Form {

    public FormDashboard() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel text = new JLabel("Dashboard");
        add(text);
    }
}
