package com.univsoftdev.econova.security.argon2;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author CNA
 */
public class Argon2PasswordHasherTest {

    @Test
    public void testPasswordVerification() {
        Argon2PasswordHasher hasher = new Argon2PasswordHasher();
        Argon2CredentialsMatcher matcher = new Argon2CredentialsMatcher(hasher);

        char[] password = "admin123".toCharArray();
        String hash = hasher.hash(password);

        assertTrue(matcher.verifyDirectly(hash, "admin123".toCharArray()));
        assertFalse(matcher.verifyDirectly(hash, "wrongpass".toCharArray()));

        // Verificación con token Shiro
        UsernamePasswordToken token = new UsernamePasswordToken("user", "admin123".toCharArray());
        AuthenticationInfo info = new SimpleAuthenticationInfo("user", hash, "testRealm");
        assertTrue(matcher.doCredentialsMatch(token, info));
    }
}
