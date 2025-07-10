package com.univsoftdev.econova.core;

/**
 *
 * @author UnivSoftDev
 */
public class DatabaseCredentials {

    private static String username;
    private static String password;

    public static void promptCredentials() {

    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }
}
