package com.univsoftdev.econova.contabilidad.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.LoggingFacade;
import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.Injector;
import com.univsoftdev.econova.core.config.AppConfig;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class About extends JPanel {

    private static final long serialVersionUID = 1L;

    public About() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx,wrap,insets 5 30 5 30,width 600", "[fill,630::]", ""));

        JTextPane title = createText("Econova");
        title.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold +5");

        JTextPane description = createText("");
        description.setContentType("text/html");
        description.setText(getDescriptionText());
        description.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                showUrl(e.getURL());
            }
        });

        add(title);
        add(description);
        add(createSystemInformation());
    }

    private JTextPane createText(String text) {
        JTextPane textPane = new JTextPane();
        textPane.setBorder(BorderFactory.createEmptyBorder());
        textPane.setText(text);
        textPane.setEditable(false);
        textPane.setCaret(new DefaultCaret() {
            private static final long serialVersionUID = 6648696811787552105L;
            @Override
            public void paint(Graphics g) {
            }
        });
        return textPane;
    }

    private String getDescriptionText() {
        return "Econova es un sistema contable integral diseñado para gestionar y registrar las transacciones "
                + "financieras de una empresa de manera eficiente.<br> Está compuesto por módulos que trabajan en conjunto "
                + "para facilitar la gestión financiera y la organización de la información contable. <br>"
                + "Econova permite el registro de actividades diarias, la clasificación de movimientos contables, y la generación de "
                + "informes financieros precisos. Además, apoya la toma de deciciones, al proporcionar una visión clara del estado " 
                + "financiero de la empresa, ayudando a controlar operacionesy a predecir flujos de efectivo.";
    }

    private String getSystemInformationText() {
        return "<b>Econova Version: </b>%s<br/>"
                + "<b>Java: </b>%s<br/>"
                + "<b>System: </b>%s<br/>";
    }

    private JComponent createSystemInformation() {
        JPanel panel = new JPanel(new MigLayout("wrap"));
        panel.setBorder(new TitledBorder("Información del Sistema"));
        JTextPane textPane = createText("");
        textPane.setContentType("text/html");
        
        AppConfig appContext = Injector.get(AppConfig.class);
        String version = appContext.getAppVersion();
        String java = System.getProperty("java.vendor") + " - v" + System.getProperty("java.version");
        String system = System.getProperty("os.name") + " " + System.getProperty("os.arch") + " - v" + System.getProperty("os.version");
        String text = String.format(getSystemInformationText(),
                version,
                java,
                system);
        textPane.setText(text);
        panel.add(textPane);
        return panel;
    }

    private void showUrl(URL url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(url.toURI());
                } catch (IOException | URISyntaxException e) {
                    LoggingFacade.INSTANCE.logSevere("Error browse url", e);
                }
            }
        }
    }
}
