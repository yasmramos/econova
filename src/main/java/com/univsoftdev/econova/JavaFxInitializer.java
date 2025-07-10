package com.univsoftdev.econova;

import javafx.embed.swing.JFXPanel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaFxInitializer {

    public static void initializeToolkit() {
        try {
            new JFXPanel();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
