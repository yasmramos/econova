package com.univsoftdev.econova.license;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class TimeVerification {

    public static Date getNetworkTime() throws IOException {
        try {
            URL url = new URL("http://worldtimeapi.org/api/ip");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Leer respuesta y parsear timestamp
            // Implementar parsing de JSON response
            return new Date(); // Fallback si no hay conexión
        } catch (Exception e) {
            return new Date(); // Fallback local
        }
    }
}
