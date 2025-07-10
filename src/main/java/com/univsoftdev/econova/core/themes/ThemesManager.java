package com.univsoftdev.econova.core.themes;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.LoggingFacade;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ThemesManager {

    final List<ThemesInfo> bundledThemes = new ArrayList<>();
    final List<ThemesInfo> coreThemes = new ArrayList<>();

    void loadThemes() {
        bundledThemes.clear();

        // create core themes
        coreThemes.add(new ThemesInfo("FlatLaf Light", null, false, null, null, null, null, FlatLightLaf.class.getName()));
        coreThemes.add(new ThemesInfo("FlatLaf Dark", null, true, null, null, null, null, FlatDarkLaf.class.getName()));
        coreThemes.add(new ThemesInfo("FlatLaf IntelliJ", null, false, null, null, null, null, FlatIntelliJLaf.class.getName()));
        coreThemes.add(new ThemesInfo("FlatLaf Darcula", null, true, null, null, null, null, FlatDarculaLaf.class.getName()));
        coreThemes.add(new ThemesInfo("FlatLaf macOS Light", null, false, null, null, null, null, FlatMacLightLaf.class.getName()));
        coreThemes.add(new ThemesInfo("FlatLaf macOS Dark", null, true, null, null, null, null, FlatMacDarkLaf.class.getName()));

        // Cargar ObjectMapper de Jackson
        ObjectMapper objectMapper = new ObjectMapper();

        // load themes.json
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("themes.json"), StandardCharsets.UTF_8)) {
            // Parseamos el JSON como Map<String, Map<String, String>>
            Map<String, Map<String, String>> json = objectMapper.readValue(reader, new TypeReference<>() {
            });

            // add themes info
            for (Map.Entry<String, Map<String, String>> entry : json.entrySet()) {
                String resourceName = entry.getKey();
                Map<String, String> value = entry.getValue();
                String name = value.get("name");
                boolean dark = Boolean.parseBoolean(value.get("dark"));
                String license = value.get("license");
                String licenseFile = value.get("licenseFile");
                String sourceCodeUrl = value.get("sourceCodeUrl");
                String sourceCodePath = value.get("sourceCodePath");

                bundledThemes.add(new ThemesInfo(name, resourceName, dark, license, licenseFile, sourceCodeUrl, sourceCodePath, null));
            }

        } catch (IOException e) {
            LoggingFacade.INSTANCE.logSevere(null, e);
        }
    }
}
