package com.univsoftdev.econova.security;

import java.security.SecureRandom;
import java.util.Random;

public class SecurityUtils {

    private static final Random RANDOM = new SecureRandom();

    public static void delayRandom(int minMs, int maxMs) {
        try {
            Thread.sleep(RANDOM.nextInt(maxMs - minMs + 1) + minMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
