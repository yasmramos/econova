package com.univsoftdev.econova.license;

import java.util.Arrays;
import java.util.Random;

public class TimeIntegrity {

    private static long EXPECTED_MAX_TIME;
    private static long EXPECTED_MIN_TIME;

    public static void verifyTimeConsistency() {
        long start = System.nanoTime();

        // Realizar operaciones que tomen tiempo conocido
        performTimeConsumingOperation();

        long end = System.nanoTime();
        long elapsed = end - start;

        // Si el tiempo transcurrido es anómalo, posible manipulación
        if (elapsed < EXPECTED_MIN_TIME || elapsed > EXPECTED_MAX_TIME) {
            throw new SecurityException("Anomalía temporal detectada");
        }
    }

    private static void performTimeConsumingOperation() {
        // Operación que tome un tiempo predecible
        byte[] data = new byte[1000000];
        new Random().nextBytes(data);
        Arrays.sort(data);
    }
}
