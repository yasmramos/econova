package com.univsoftdev.econova.security.shiro;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAttemptCache {

    private static final Map<String, Integer> ATTEMPTS = new ConcurrentHashMap<>();
    private static final Map<String, Long> LOCKED_ACCOUNTS = new ConcurrentHashMap<>();
    public static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_MS = 30 * 60 * 1000; // 30 minutos

    /**
     * Registra un intento fallido de inicio de sesión para un usuario
     *
     * @param username Nombre de usuario (no debe ser null)
     * @return Número total de intentos fallidos después de este registro
     * @throws IllegalArgumentException si username es null
     */
    public static int recordFailedAttempt(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username no puede ser null");
        }
        return ATTEMPTS.compute(username, (k, v) -> v == null ? 1 : v + 1);
    }

    /**
     * Verifica si una cuenta está bloqueada temporalmente
     *
     * @param username Nombre de usuario (no debe ser null)
     * @return true si la cuenta está bloqueada, false en caso contrario
     * @throws IllegalArgumentException si username es null
     */
    public static boolean isAccountLocked(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username no puede ser null");
        }

        Integer attempts = ATTEMPTS.get(username);
        if (attempts == null || attempts < MAX_ATTEMPTS) {
            return false;
        }

        Long lockTime = LOCKED_ACCOUNTS.get(username);
        if (lockTime == null) {
            LOCKED_ACCOUNTS.put(username, System.currentTimeMillis());
            return true;
        }

        if (System.currentTimeMillis() - lockTime > LOCK_TIME_MS) {
            resetAttempts(username);
            return false;
        }

        return true;
    }

    /**
     * Resetea los intentos fallidos para un usuario
     *
     * @param username Nombre de usuario (puede ser null, en cuyo caso no se
     * hace nada)
     */
    public static void resetAttempts(String username) {
        if (username != null) {
            ATTEMPTS.remove(username);
            LOCKED_ACCOUNTS.remove(username);
        }
    }

    /**
     * Limpia los intentos fallidos (alias de resetAttempts para compatibilidad)
     *
     * @param username Nombre de usuario (puede ser null, en cuyo caso no se
     * hace nada)
     */
    public static void clearFailedAttempts(String username) {
        resetAttempts(username);
    }

    /**
     * Obtiene el número de intentos fallidos para un usuario
     *
     * @param username Nombre de usuario (no debe ser null)
     * @return Número de intentos fallidos (0 si username es null o no existe)
     */
    public static int getFailedAttempts(String username) {
        return username != null ? ATTEMPTS.getOrDefault(username, 0) : 0;
    }
}
