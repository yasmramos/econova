package com.univsoftdev.econova.contabilidad.component;

import net.miginfocom.swing.MigLayout;
import raven.modal.component.Modal;
import raven.modal.listener.ModalCallback;
import raven.modal.listener.ModalController;

import java.awt.*;

public class EmptyModalBorder extends Modal {

    public static final int OPENED = 20;
    private static final long serialVersionUID = 1L;
    private final transient ModalCallback modalCallback;

    public EmptyModalBorder(Component component) {
        this(component, null);
    }

    public EmptyModalBorder(Component component, ModalCallback modalCallback) {
        this.modalCallback = modalCallback;
        setLayout(new MigLayout("fill,insets 8 0 8 0", "[fill]", "[fill]"));
        add(component);
    }

    @Override
    protected void modalOpened() {
        if (modalCallback != null) {
            modalCallback.action(createController(), OPENED);
        }
    }

    private ModalController createController() {
        return new ModalController(this) {
            private static final long serialVersionUID = 5648936212082073025L;

            @Override
            public void close() {
                getController().closeModal();
            }
        };
    }
}
