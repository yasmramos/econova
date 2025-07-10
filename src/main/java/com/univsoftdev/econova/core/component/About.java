package com.univsoftdev.econova.core.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.LoggingFacade;
import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.Econova;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class About extends JPanel {

    private static final long serialVersionUID = 157332321538670149L;

    public About() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx,wrap,insets 5 30 5 30,width 400", "[fill,330::]", ""));

        JTextPane title = createText("Econova");
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +5");

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

    @NotNull
    private JTextPane createText(String text) {
        JTextPane textPane = new JTextPane();
        textPane.setBorder(BorderFactory.createEmptyBorder());
        textPane.setText(text);
        textPane.setEditable(false);
        textPane.setCaret(new DefaultCaret() {
            @Override
            public void paint(Graphics g) {
            }
        });
        return textPane;
    }

    @NotNull
    private String getDescriptionText() {
        String text = "This is a demo project for the Modal Dialog library, " +
                "built using FlatLaf Look and Feel and MigLayout library.<br>" +
                "For source code, visit the <a href=\"https://github.com/DJ-Raven/swing-modal-dialog/\">GitHub Project.</a>";

        return text;
    }

    @NotNull
    private String getSystemInformationText() {

        return "<b>Econova Version: </b>%s<br/>" +
                "<b>Java: </b>%s<br/>" +
                "<b>System: </b>%s<br/>";
    }

    @NotNull
    private JComponent createSystemInformation() {
        JPanel panel = new JPanel(new MigLayout("wrap"));
        panel.setBorder(new TitledBorder("System Information"));
        JTextPane textPane = createText("");
        textPane.setContentType("text/html");
        String version = AppContext.getInstance().getVersion().toString();
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
