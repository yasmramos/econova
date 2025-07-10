package com.univsoftdev.econova.contabilidad.forms;

import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.SystemForm;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@SystemForm(name = "Form Input", description = "input form not yet update")
public class FormInput extends Form {

    private static final long serialVersionUID = 8059848630095439344L;

    public FormInput() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel text = new JLabel("Input");
        add(text);
    }
}
