package com.univsoftdev.econova.contabilidad;

import java.io.File;
import java.io.InputStream;

public class Reports {

    public static final String BALANCE = "balance";
    public static final String DIR = "reports";
    private static final String EXT = ".jrxml";

    public static InputStream getReport(String report) {
        return Reports.class.getClassLoader().getResourceAsStream(DIR + File.pathSeparator + report + EXT);
    }

}
