package com.univsoftdev.econova.seguridad;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.util.Arrays;

public class Argon2PasswordHasher implements PasswordHasher {

    // Using stronger parameters (consider adjusting based on your performance/security needs)
    private static final int ITERATIONS = 3;
    private static final int MEMORY = 65536; // 64MB
    private static final int PARALLELISM = 2;

    private final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public String hash(char[] password) {
        try {
            return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password);
        } finally {
            // Clear the password array immediately after use
            Arrays.fill(password, '\0');
        }
    }

    public boolean verify(String hash, char[] rawPassword) {
        try {
            return argon2.verify(hash, rawPassword);
        } finally {
            // Clear the password array immediately after use
            Arrays.fill(rawPassword, '\0');
        }
    }

    // Additional helper method for String passwords (less secure)
    @Override
    public String hash(String password) {
        char[] chars = password.toCharArray();
        try {
            return hash(chars);
        } finally {
            Arrays.fill(chars, '\0');
        }
    }

    // Additional helper method for String passwords (less secure)
    @Override
    public boolean verify(String hash, String rawPassword) {
        char[] chars = rawPassword.toCharArray();
        try {
            return verify(hash, chars);
        } finally {
            Arrays.fill(chars, '\0');
        }
    }
}
