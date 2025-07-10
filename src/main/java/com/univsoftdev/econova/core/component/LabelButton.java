package com.univsoftdev.econova.core.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class LabelButton extends JLabel {

    private static final long serialVersionUID = 7549913336281901398L;

    public LabelButton(String text) {
        super("<html><a href=\"#\">" + text + "</a></html>");
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(true);
    }

    public LabelButton() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(true);
    }

    public void addOnClick(Consumer<?> event) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    requestFocus();
                    event.accept(null);
                }
            }
        });
    }
}
