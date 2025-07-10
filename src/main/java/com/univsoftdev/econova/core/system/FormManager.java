package com.univsoftdev.econova.core.system;

import com.univsoftdev.econova.contabilidad.component.About;
import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import com.univsoftdev.econova.core.auth.Login;
import com.univsoftdev.econova.core.forms.FormDashboard;
import com.univsoftdev.econova.core.utils.UndoRedo;

import javax.swing.*;

public class FormManager {

    public static final UndoRedo<Form> FORMS = new UndoRedo<>();
    private static JFrame frame;
    private static MainForm mainForm;
    private static Login login;

    public static void install(JFrame f) {
        frame = f;
        install();
        logout();
    }

    private static void install() {
        FormSearch.getInstance().installKeyMap(getMainForm());
    }

    public static void showForm(Form form) {
        if (form != FORMS.getCurrent()) {
            FORMS.add(form);
            form.formCheck();
            form.formOpen();
            mainForm.setForm(form);
            mainForm.refresh();
        }
    }

    public static void undo() {
        if (FORMS.isUndoAble()) {
            Form form = FORMS.undo();
            form.formCheck();
            form.formOpen();
            mainForm.setForm(form);
            Drawer.setSelectedItemClass(form.getClass());
        }
    }

    public static void redo() {
        if (FORMS.isRedoAble()) {
            Form form = FORMS.redo();
            form.formCheck();
            form.formOpen();
            mainForm.setForm(form);
            Drawer.setSelectedItemClass(form.getClass());
        }
    }

    public static void refresh() {
        if (FORMS.getCurrent() != null) {
            FORMS.getCurrent().formRefresh();
            mainForm.refresh();
        }
    }

    public static void login() {
        Drawer.setVisible(true);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(getMainForm());
        Drawer.setSelectedItemClass(FormDashboard.class);
        frame.repaint();
        frame.revalidate();
        frame.getJMenuBar().setVisible(true);
    }

    public static void enableMenuBar() {
        SwingUtilities.invokeLater(() -> {
            JMenuBar menuBar = frame.getJMenuBar();
            if (menuBar != null) {
                menuBar.setEnabled(true);
                for (int i = 0; i < menuBar.getMenuCount(); i++) {
                    JMenu menu = menuBar.getMenu(i);
                    if (menu != null) {
                        menu.setEnabled(true);
                    }
                }
            }
        });
    }

    public static void logout() {
        Drawer.setVisible(false);
        frame.getContentPane().removeAll();
        Form logn = getLogin();
        logn.formCheck();
        frame.getContentPane().add(logn);
        FORMS.clear();
        frame.repaint();
        frame.revalidate();
    }

    public static JFrame getFrame() {
        return frame;
    }

    private static MainForm getMainForm() {
        if (mainForm == null) {
            mainForm = new MainForm();
        }
        return mainForm;
    }

    private static Login getLogin() {
        if (login == null) {
            login = new Login();
        }
        return login;
    }

    public static void showAbout() {
        ModalDialog.showModal(frame, new SimpleModalBorder(new About(), "Acerca de ..."),
                ModalDialog.createOption().setAnimationEnabled(false)
        );
    }
}
