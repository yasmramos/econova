package com.univsoftdev.econova.contabilidad.forms;

import com.univsoftdev.econova.core.system.Form;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.Serial;

public class FormDashboard extends Form {

    @Serial
    private static final long serialVersionUID = 4499649909338788110L;

    public FormDashboard() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel text = new JLabel("Dashboard");
        add(text);
    }
}
