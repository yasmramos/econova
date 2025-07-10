package com.univsoftdev.econova.contabilidad.component;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.util.ColorFunctions;

import javax.swing.*;
import java.awt.*;

public class AccentColorIcon extends FlatAbstractIcon {

    private final String colorKey;

    public AccentColorIcon(String colorKey) {
        super(16, 16, null);
        this.colorKey = colorKey;
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        Color cKey = UIManager.getColor(colorKey);
        if (cKey == null)
            cKey = Color.lightGray;
        else if (!c.isEnabled()) {
            cKey = FlatLaf.isLafDark()
                    ? ColorFunctions.shade(cKey, 0.5f)
                    : ColorFunctions.tint(cKey, 0.6f);
        }

        g.setColor(cKey);
        g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
        g.dispose();
    }
}
