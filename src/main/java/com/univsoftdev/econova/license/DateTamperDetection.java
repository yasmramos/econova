package com.univsoftdev.econova.license;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public class DateTamperDetection {

    private static final String LAST_CHECK_FILE = "last_time_check.dat";

    public static boolean isDateTampered(Date lastKnownDate) {
        Date currentDate = new Date();

        // Detectar si la fecha retrocedió
        if (currentDate.before(lastKnownDate)) {
            return true;
        }

        // Guardar la última fecha verificada
        saveLastDate(currentDate);
        return false;
    }

    private static void saveLastDate(Date date) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(LAST_CHECK_FILE))) {
            oos.writeObject(date);
        } catch (IOException e) {
            // Manejar error
        }
    }

    private static Date loadLastDate() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(LAST_CHECK_FILE))) {
            return (Date) ois.readObject();
        } catch (Exception e) {
            return new Date(0); // Fecha por defecto
        }
    }
}
