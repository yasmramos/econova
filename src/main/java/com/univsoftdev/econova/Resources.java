package com.univsoftdev.econova;

import java.io.InputStream;
import java.net.URL;

/**
 * Utilidad para acceder a recursos de la aplicación de forma centralizada.
 *
 * <p>
 * Esta clase proporciona métodos estáticos convenientes para obtener recursos
 * (archivos, imágenes, configuraciones, etc.) que están empaquetados dentro del
 * JAR de la aplicación o disponibles en el classpath.</p>
 *
 * <p>
 * Los recursos se buscan relativos a esta clase, lo que significa que las rutas
 * proporcionadas se resuelven desde el mismo paquete o utilizando rutas
 * absolutas que comienzan con "/".</p>
 *
 * <p>
 * <strong>Ejemplos de uso:</strong></p>
 * <pre>
 * {@code
 * // Obtener un recurso del mismo paquete
 * URL configUrl = Resources.getResource("config.xml");
 *
 * // Obtener un recurso de la raíz del classpath
 * InputStream templateStream = Resources.getResourceAsStream("/templates/report.tpl");
 *
 * // Obtener un recurso de un subpaquete
 * URL iconUrl = Resources.getResource("icons/app.png");
 * }
 * </pre>
 *
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 *
 * @see Class#getResource(String)
 * @see Class#getResourceAsStream(String)
 * @see ClassLoader
 */
public class Resources {

    /**
     * Obtiene la URL de un recurso.
     *
     * <p>
     * Busca el recurso utilizando el ClassLoader de esta clase. Si el nombre
     * comienza con "/", la búsqueda se realiza desde la raíz del classpath. De
     * lo contrario, se busca relativo al paquete de esta clase.</p>
     *
     * @param name Nombre del recurso a buscar (puede ser relativo o absoluto)
     * @return URL del recurso, o {@code null} si no se encuentra
     *
     * @see Class#getResource(String)
     */
    public static URL getResource(String name) {
        return Resources.class.getResource(name);
    }

    /**
     * Obtiene un InputStream para leer un recurso.
     *
     * <p>
     * Busca el recurso utilizando el ClassLoader de esta clase y devuelve un
     * InputStream para leer su contenido. Si el nombre comienza con "/", la
     * búsqueda se realiza desde la raíz del classpath. De lo contrario, se
     * busca relativo al paquete de esta clase.</p>
     *
     * <p>
     * <strong>Importante:</strong> El llamador es responsable de cerrar el
     * InputStream devuelto.</p>
     *
     * @param name Nombre del recurso a buscar (puede ser relativo o absoluto)
     * @return InputStream para leer el recurso, o {@code null} si no se
     * encuentra
     *
     * @see Class#getResourceAsStream(String)
     */
    public static InputStream getResourceAsStream(String name) {
        return Resources.class.getResourceAsStream(name);
    }
}
