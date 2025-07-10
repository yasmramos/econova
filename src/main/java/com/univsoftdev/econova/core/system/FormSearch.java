package com.univsoftdev.econova.core.system;

import com.univsoftdev.econova.contabilidad.forms.FormDashboardContabilidad;
import com.univsoftdev.econova.core.forms.FormSlidePane;
import com.univsoftdev.econova.core.forms.FormSetting;
import com.univsoftdev.econova.core.forms.FormModal;
import com.univsoftdev.econova.core.forms.FormResponsiveLayout;
import com.univsoftdev.econova.core.forms.FormDateTime;
import com.univsoftdev.econova.core.forms.FormToast;
import com.univsoftdev.econova.core.forms.FormInput;
import com.univsoftdev.econova.core.forms.FormTable;
import raven.modal.ModalDialog;
import com.univsoftdev.econova.core.component.EmptyModalBorder;
import com.univsoftdev.econova.core.component.FormSearchPanel;
import com.univsoftdev.econova.core.utils.SystemForm;
import com.univsoftdev.econova.finanzas.views.FormFinanzas;
import raven.modal.option.Location;
import raven.modal.option.Option;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class FormSearch {

    private static FormSearch instance;
    public static final String ID = "search";
    private final Map<SystemForm, Class<? extends Form>> formsMap;
    private FormSearchPanel searchPanel;

    public static FormSearch getInstance() {
        if (instance == null) {
            instance = new FormSearch();
        }
        return instance;
    }

    private FormSearch() {
        formsMap = new HashMap<>();
        for (Class<? extends Form> cls : getClassForms()) {
            if (cls.isAnnotationPresent(SystemForm.class)) {
                SystemForm f = cls.getAnnotation(SystemForm.class);
                formsMap.put(f, cls);
            }
        }
    }

    private Class<? extends Form>[] getClassForms() {
        return new Class[]{
                FormDashboardContabilidad.class,
                FormFinanzas.class,
                FormSlidePane.class,
                FormDateTime.class,
                FormModal.class,
                FormToast.class,
                FormInput.class,
                FormTable.class,
                FormResponsiveLayout.class,
                FormSetting.class
        };
    }

    public void installKeyMap(JComponent component) {
        ActionListener key = e -> showSearch();
        component.registerKeyboardAction(key, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void showSearch() {
        if (ModalDialog.isIdExist(ID)) {
            return;
        }
        Option option = ModalDialog.createOption();
        option.setAnimationEnabled(false);
        option.getLayoutOption().setMargin(20, 10, 10, 10).setLocation(Location.CENTER, Location.TOP);
        ModalDialog.showModal(FormManager.getFrame(), new EmptyModalBorder(getSearchPanel(), (controller, action) -> {
            if (action == EmptyModalBorder.OPENED) {
                searchPanel.searchGrabFocus();
            }
        }), option, ID);
    }

    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            searchPanel = new FormSearchPanel(formsMap);
        }
        searchPanel.formCheck();
        searchPanel.clearSearch();
        return searchPanel;
    }
}
