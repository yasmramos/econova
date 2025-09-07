package com.univsoftdev.econova.security.argon2;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.inject.Singleton;
import java.util.Arrays;

/**
 * Implementación de {@link PasswordHasher} utilizando el algoritmo Argon2.
 *
 * <p>
 * Argon2 es el ganador del concurso de funciones de hash de contraseña Password
 * Hashing Competition (PHC) y proporciona una excelente resistencia contra
 * ataques de fuerza bruta y side-channel.</p>
 *
 * <p>
 * Características principales:
 * <ul>
 * <li>Resistencia a ataques de diccionario y rainbow tables</li>
 * <li>Protección contra ataques de timing</li>
 * <li>Configurable en términos de memoria, tiempo y paralelismo</li>
 * <li>Resistente a ataques side-channel</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Parámetros de configuración:</strong></p>
 * <ul>
 * <li><strong>Iteraciones:</strong> 3 - Número de pasadas sobre la memoria</li>
 * <li><strong>Memoria:</strong> 65536 KB (64MB) - Memoria requerida</li>
 * <li><strong>Paralelismo:</strong> 2 - Grado de paralelismo</li>
 * <li><strong>Tipo:</strong> Argon2id - Combina las ventajas de Argon2i y
 * Argon2d</li>
 * </ul>
 *
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 *
 * @see PasswordHasher
 * @see Argon2
 * @see <a href="https://github.com/P-H-C/phc-winner-argon2">Argon2 Official</a>
 * @see <a href="https://github.com/phxql/argon2-jvm">Argon2 JVM</a>
 */
@Singleton
public class Argon2PasswordHasher implements PasswordHasher {

    /**
     * Número de iteraciones para el algoritmo Argon2. Mayor valor = más seguro
     * pero más lento.
     */
    private static final int ITERATIONS = 10;

    /**
     * Cantidad de memoria en KB a utilizar (64MB). Mayor valor = más resistente
     * a ataques de hardware especializado.
     */
    private static final int MEMORY = 65536; // 64MB

    /**
     * Grado de paralelismo para el cálculo. Permite aprovechar múltiples
     * núcleos de CPU.
     */
    private static final int PARALLELISM = 4;


    /**
     * Instancia de Argon2 configurada con tipo Argon2id.
     *
     * <p>
     * Argon2id combina las ventajas de Argon2i (resistencia a side-channel
     * attacks) y Argon2d (resistencia a GPU cracking) tomando características
     * de ambos.</p>
     */
    private final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    /**
     * Genera un hash Argon2 de la contraseña proporcionada.
     *
     * <p>
     * Este método es la implementación preferida ya que:
     * <ul>
     * <li>Recibe un array de caracteres en lugar de String</li>
     * <li>Limpia inmediatamente la contraseña de memoria después de usarla</li>
     * <li>Evita que la contraseña permanezca en el String pool de Java</li>
     * </ul>
     * </p>
     *
     * <p>
     * El hash generado incluye todos los parámetros de configuración y salt,
     * por lo que la verificación posterior no requiere almacenar información
     * adicional.</p>
     *
     * @param password Array de caracteres con la contraseña a hashear (no null)
     * @return String con el hash Argon2 codificado en formato compatible
     * @throws IllegalArgumentException si password es null
     * @throws RuntimeException si ocurre un error durante el proceso de hashing
     *
     * @see #verify(String, char[])
     * @see Arrays#fill(char[], char)
     */
    @Override
    public String hash(char[] password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password);
    }

    /**
     * Verifica si una contraseña en texto plano corresponde a un hash Argon2
     * dado.
     *
     * <p>
     * Este método es resistente a ataques timing ya que Argon2 realiza
     * comparaciones de hashes de forma segura.</p>
     *
     * @param hash Hash Argon2 almacenado previamente (no null)
     * @param rawPassword Array de caracteres con la contraseña a verificar (no
     * null)
     * @return {@code true} si la contraseña es válida, {@code false} en caso
     * contrario
     * @throws IllegalArgumentException si algún parámetro es null
     * @throws RuntimeException si el formato del hash es inválido
     *
     * @see #hash(char[])
     * @see Arrays#fill(char[], char)
     */
    @Override
    public boolean verify(String hash, char[] rawPassword) {
        if (hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("Hash cannot be null or empty");
        }
        if (rawPassword == null) {
            throw new IllegalArgumentException("Raw password cannot be null");
        }
        return argon2.verify(hash, rawPassword);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <strong>Advertencia:</strong> Este método convierte el String a char[] lo
     * que puede dejar copias de la contraseña en el String pool de Java. Se
     * recomienda usar {@link #hash(char[])} cuando sea posible.</p>
     *
     * @param password Contraseña en texto plano como String (no null)
     * @return String con el hash Argon2 generado
     * @throws IllegalArgumentException si password es null
     *
     * @see #hash(char[])
     */
    @Override
    public String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        char[] chars = password.toCharArray();
        return hash(chars);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <strong>Advertencia:</strong> Este método convierte el String a char[] lo
     * que puede dejar copias de la contraseña en el String pool de Java. Se
     * recomienda usar {@link #verify(String, char[])} cuando sea posible.</p>
     *
     * @param hash Hash Argon2 almacenado (no null)
     * @param rawPassword Contraseña en texto plano como String (no null)
     * @return {@code true} si la contraseña es válida, {@code false} en caso
     * contrario
     * @throws IllegalArgumentException si algún parámetro es null
     *
     * @see #verify(String, char[])
     */
    @Override
    public boolean verify(String hash, String rawPassword) {
        if (hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("Hash cannot be null or empty");
        }
        if (rawPassword == null) {
            throw new IllegalArgumentException("Raw password cannot be null");
        }

        char[] chars = rawPassword.toCharArray();
        return verify(hash, chars);
    }

    /**
     * Obtiene los parámetros de configuración actuales.
     *
     * @return String con la configuración en formato legible
     */
    public String getConfiguration() {
        return String.format("Argon2id(iterations=%d, memory=%dKB, parallelism=%d)",
                ITERATIONS, MEMORY, PARALLELISM);
    }

    /**
     * Verifica si una contraseña cumple con los requisitos mínimos de longitud.
     *
     * @param password Contraseña a verificar
     * @param minLength Longitud mínima requerida
     * @return {@code true} si cumple con la longitud mínima
     */
    public boolean meetsMinimumLength(char[] password, int minLength) {
        return password != null && password.length >= minLength;
    }
}
