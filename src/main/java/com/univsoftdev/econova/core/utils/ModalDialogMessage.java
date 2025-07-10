package com.univsoftdev.econova.core.utils;

import java.awt.*;
import raven.modal.component.Modal;

public class ModalDialogMessage extends Modal {

    private static final long serialVersionUID = 8961218947403940471L;
    private String message;

    public ModalDialogMessage() {
        initComponents();
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off

	//======== this ========
	setLayout(new BorderLayout());
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
