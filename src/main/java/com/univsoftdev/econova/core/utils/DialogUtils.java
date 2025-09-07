package com.univsoftdev.econova.core.utils;

import com.univsoftdev.econova.core.simple.CustomModalBorder;
import com.univsoftdev.econova.core.simple.SimpleMessageModal;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;
import raven.modal.component.SimpleModalBorder;
import raven.modal.listener.ModalCallback;
import raven.modal.option.BorderOption;
import raven.modal.option.ModalBorderOption;
import raven.modal.option.Option;

public class DialogUtils {

    private DialogUtils() {
    }

    public static void showDialog(JDialog dialog) {
        dialog.setVisible(true);
    }

    public static JDialog createDialog(JPanel panel, String title, boolean modal) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setModal(modal);
        dialog.add(panel);
        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.getRootPane().putClientProperty("JDialog.borderColor", new Color(242, 117, 7));
        dialog.getRootPane().putClientProperty("JDialog.borderWidth", 2);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        return dialog;
    }

    public static void showModalDialog(Component parent, Modal modal, String title) {
        Option option = ModalDialog.getDefaultOption();
        option.setBackgroundClickType(Option.BackgroundClickType.BLOCK);
        BorderOption borderOption = option.getBorderOption();
        borderOption.setBorderWidth(1);
        borderOption.setBorderColor(new Color(0, 122, 204));
        if (modal.getId() == null) {
            modal.setId(UUID.randomUUID().toString());
        }
        var mbo = new ModalBorderOption();
        mbo.setPadding(ModalBorderOption.PaddingType.SMALL);
        ModalDialog.showModal(parent, new SimpleModalBorder(modal, title, mbo), option, modal.getId());
    }

    public static void showModalDialog(Component parent, Modal modal, String title, ModalCallback callback) {
        Option option = ModalDialog.getDefaultOption();
        option.setBackgroundClickType(Option.BackgroundClickType.BLOCK);
        BorderOption borderOption = option.getBorderOption();
        borderOption.setBorderWidth(1);
        borderOption.setBorderColor(new Color(0, 122, 204));
        if (modal.getId() == null) {
            modal.setId(UUID.randomUUID().toString());
        }
        var mbo = new ModalBorderOption();
        mbo.setPadding(ModalBorderOption.PaddingType.SMALL);
        ModalDialog.showModal(parent, new SimpleModalBorder(modal, title, mbo, SimpleModalBorder.YES_NO_OPTION, callback), option, modal.getId());
    }

    public static void showModalDialog1(Component parent, Modal modal, String title) {
        Option option = ModalDialog.getDefaultOption();
        option.setBackgroundClickType(Option.BackgroundClickType.BLOCK);
        BorderOption borderOption = option.getBorderOption();
        borderOption.setBorderWidth(1);
        borderOption.setBorderColor(new Color(0, 122, 204));
        if (modal.getId() == null) {
            modal.setId(UUID.randomUUID().toString());
        }
        var mbo = new ModalBorderOption();
        mbo.setPadding(ModalBorderOption.PaddingType.SMALL);
        ModalDialog.showModal(parent, new CustomModalBorder(modal, title, mbo), option, modal.getId());
    }

    public static void showMessage(Component parent, Modal modal, String message, String title, SimpleMessageModal.Type type) {
        Option option = ModalDialog.getDefaultOption();
        if (modal.getId() == null) {
            modal.setId(UUID.randomUUID().toString());
        }
        ModalDialog.showModal(parent, new SimpleMessageModal(type, message, title, SimpleModalBorder.DEFAULT_OPTION, null), option);
    }

    public static void showConfirmDialog(Component component, String message, String title, SimpleMessageModal.Type type, int border) {
        showMessage(component, message, title, type, border);
    }

    public static void showMessage(Component parent, String message, String title, SimpleMessageModal.Type type, ModalCallback callback) {
        Option option = ModalDialog.getDefaultOption();
        ModalDialog.showModal(parent, new SimpleMessageModal(type, message, title, ModalBorder.DEFAULT_OPTION.getValue(), callback), option);
    }

    public static void showMessage(Component parent, String message, String title, SimpleMessageModal.Type type, int border, ModalCallback callback) {
        Option option = ModalDialog.getDefaultOption();
        ModalDialog.showModal(parent, new SimpleMessageModal(type, message, title, border, callback), option);
    }

    public static void showMessage(Component parent, String message, String title, SimpleMessageModal.Type type, int modalBorder) {
        Option option = ModalDialog.getDefaultOption();
        ModalDialog.showModal(parent, new SimpleMessageModal(type, message, title, modalBorder, null), option);
    }

    public static void showMessageDialog(Component parent, String message, String title, SimpleMessageModal.Type type, ModalCallback callback) {
        Option option = ModalDialog.getDefaultOption();
        ModalDialog.showModal(parent, new SimpleMessageModal(type, message, title, SimpleModalBorder.DEFAULT_OPTION, callback), option);
    }

    public static void showMessageDialog(Component parent, String message, String title, SimpleMessageModal.Type type) {
        Option option = ModalDialog.getDefaultOption();
        ModalDialog.showModal(parent, new SimpleMessageModal(type, message, title, SimpleModalBorder.DEFAULT_OPTION, null), option);
    }

    public static void showMessageDialog(Component parent, String message, String title, SimpleMessageModal.Type type, int border, ModalCallback callback) {
        Option option = ModalDialog.getDefaultOption();
        ModalDialog.showModal(parent, new SimpleMessageModal(type, message, title, border, callback), option);
    }

    public static void showMessageDialog(Component parent, String message, String title) {
        Option option = ModalDialog.getDefaultOption();
        ModalDialog.showModal(parent, new SimpleMessageModal(SimpleMessageModal.Type.INFO, message, title, SimpleModalBorder.OK_OPTION, null), option);
    }

    public static void showInfoDialog(Component parent, String message, String title, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.INFO, callback);
    }

    public static void showDefaultDialog(Component parent, String message, String title, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.DEFAULT, callback);
    }

    public static void showMessageDialog(Component parent, String message, String title, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.DEFAULT, callback);
    }

    public static void showDefaultDialog(Component parent, Modal modal, String message, String title) {
        showMessage(parent, modal, message, title, SimpleMessageModal.Type.DEFAULT);
    }

    public static void showErrorDialog(Component parent, String message, String title, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.ERROR, callback);
    }

    public static void showWarningDialog(Component parent, String message, String title, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.WARNING, callback);
    }

    public static void showSuccesDialog(Component parent, String message, String title, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.SUCCESS, callback);
    }

    public static void showInfoDialog(Component parent, String message, String title, int border, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.INFO, border, callback);
    }

    public static void showDefaultDialog(Component parent, String message, String title, int border, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.DEFAULT, border, callback);
    }

    public static void showErrorDialog(Component parent, String message, String title, int border, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.ERROR, border, callback);
    }

    public static void showWarningDialog(Component parent, String message, String title, int border, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.WARNING, border, callback);
    }

    public static void showSuccesDialog(Component parent, String message, String title, int border, ModalCallback callback) {
        showMessage(parent, message, title, SimpleMessageModal.Type.SUCCESS, border, callback);
    }

    public static void showInfoDialog(Component parent, Modal modal, String message, String title) {
        showMessage(parent, modal, message, title, SimpleMessageModal.Type.INFO);
    }

    public static void showErrorDialog(Component parent, Modal modal, String message, String title) {
        showMessage(parent, modal, message, title, SimpleMessageModal.Type.ERROR);
    }

    public static void showWarningDialog(Component parent, Modal modal, String message, String title) {
        showMessage(parent, modal, message, title, SimpleMessageModal.Type.WARNING);
    }

    public static void showSuccessDialog(Component parent, Modal modal, String message, String title) {
        showMessage(parent, modal, message, title, SimpleMessageModal.Type.SUCCESS);
    }

    public static void showInfoDialog(Component parent, String message, String title, int modalBorder) {
        showMessage(parent, message, title, SimpleMessageModal.Type.INFO, modalBorder);
    }

    public static void showDefaultDialog(Component parent, String message, String title, int border) {
        showMessage(parent, message, title, SimpleMessageModal.Type.DEFAULT, border);
    }

    public static void showErrorDialog(Component parent, String message, String title, int border) {
        showMessage(parent, message, title, SimpleMessageModal.Type.ERROR, border);
    }

    public static void showWarningDialog(Component parent, String message, String title, int border) {
        showMessage(parent, message, title, SimpleMessageModal.Type.WARNING, border);
    }

    public static void showSuccessDialog(Component parent, String message, String title, int border) {
        showMessage(parent, message, title, SimpleMessageModal.Type.SUCCESS, border);
    }

    public static void updateDialogContent(JDialog dialog, JPanel newPanel) {
        dialog.getContentPane().removeAll();
        dialog.add(newPanel);
        dialog.revalidate();
        dialog.repaint();
    }

}
