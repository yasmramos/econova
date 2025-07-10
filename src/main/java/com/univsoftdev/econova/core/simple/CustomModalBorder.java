package com.univsoftdev.econova.core.simple;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import raven.modal.component.SimpleModalBorder;
import raven.modal.listener.ModalCallback;
import raven.modal.option.ModalBorderOption;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;

/**
 * Esta clase extiende SimpleModalBorder para crear un diálogo estilo web.
 *
 * @author Raven
 */
public class CustomModalBorder extends SimpleModalBorder {
    
    private Option[] optionsType = new Option[]{};

    // Constructor principal
    public CustomModalBorder(Component component, String title) {
        super(component, title);
    }

    // Constructor con opciones personalizadas
    public CustomModalBorder(Component component, String title, ModalBorderOption option) {
        super(component, title, option);
    }

    // Constructor con tipo de opción
    public CustomModalBorder(Component component, String title, int optionType, ModalCallback callback) {
        super(component, title, optionType, callback);
    }

    // Constructor con botones personalizados
    public CustomModalBorder(Component component, String title, Option[] optionsType, ModalCallback callback) {
        super(component, title, optionsType, callback);
    }

    /**
     * Sobrescribe el método installComponent para personalizar el diseño.
     */
    @Override
    public void installComponent() {
        // Configurar layout principal
//        String insets = String.format("insets %d 0 %d 0", option.getPadding().top, option.getPadding().bottom);
//        setLayout(new MigLayout("wrap,fillx," + insets, "[fill]", "[][fill,grow][]"));

        // Crear cabecera personalizada
        header = createHeader();
        add(header);

        // Agregar contenido
        if (option.isUseScroll()) {
            JScrollPane scrollPane = createContentScroll();
            scrollPane.setViewportView(component);
            add(scrollPane);
        } else {
            add(component);
        }

        // Agregar botones de opción
        Component optionButton = createOptionButton(getOptionsType());
        if (optionButton != null) {
            add(optionButton);
        }
    }

    /**
     * Crea la cabecera personalizada.
     *
     * @return
     */
    @Override
    protected JComponent createHeader() {
        String insets = String.format("insets 0 %d 0 %d", option.getPadding().left, option.getPadding().right);
        new MigLayout("novisualpadding,fill," + insets);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 122, 204));

        // Título
        panel.add(createTitleComponent(title), "push");

        // Botón de cierre
        panel.add(createActionTitleComponent());
        
        return panel;
    }

    /**
     * Crea el componente de título.
     *
     * @param title
     * @return
     */
    @Override
    protected JComponent createTitleComponent(String title) {
        JLabel label = new JLabel(title);
        label.setForeground(Color.white);
        return label;
    }

    /**
     * Crea el componente de acción (botón de cierre).
     *
     * @return
     */
    @Override
    protected JComponent createActionTitleComponent() {
        JButton buttonClose = new JButton(new FlatSVGIcon("raven/modal/icon/close.svg", 0.4f));
        buttonClose.setFocusable(false);
        buttonClose.setForeground(Color.white);
        buttonClose.addActionListener(e -> doAction(CLOSE_OPTION));
        buttonClose.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:999;"
                + "margin:5,5,5,5;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "background:null;");
        return buttonClose;
    }
    
    public Option[] getOptionsType() {
        return optionsType;
    }
    
    public void setOptionsType(Option[] optionsType) {
        this.optionsType = optionsType;
    }
    
}
