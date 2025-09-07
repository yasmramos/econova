package com.univsoftdev.econova.security.keystore.model;

import com.univsoftdev.econova.core.UserContext;
import com.univsoftdev.econova.core.model.BaseModel;
import com.univsoftdev.econova.ebean.config.MyTenantSchemaProvider;
import io.ebean.DB;
import io.ebean.Model;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * Entidad JPA/Ebean que representa un backup del keystore almacenado en la base
 * de datos.
 *
 * <p>
 * Esta clase proporciona persistencia para el keystore de la aplicación,
 * permitiendo su recuperación en caso de pérdida de los archivos locales.
 * Implementa un patrón de singleton donde solo existe una entrada con ID
 * "default".</p>
 *
 * <p>
 * <strong>Características:</strong></p>
 * <ul>
 * <li>Almacenamiento como Large Object (LOB) para datos binarios</li>
 * <li>Soporte multi-tenant mediante {@link MyTenantSchemaProvider}</li>
 * <li>Versionado automático para concurrencia optimista</li>
 * <li>Auditoría automática de fechas de creación y modificación</li>
 * <li>Integración con Ebean ORM</li>
 * </ul>
 *
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 *
 * @see KeystoreManager
 * @see MyTenantSchemaProvider
 * @see Model
 */
@Entity
@Table(name = "keystore_backup")
public class KeystoreBackup extends BaseModel {

    /**
     * Datos binarios del keystore serializado. Se almacena como Large Object
     * (LOB) debido a su tamaño variable.
     */
    @Lob
    @Column(name = "keystore_data", nullable = false)
    private byte[] keystoreData;

    /**
     * Marca de tiempo de la última actualización del backup.
     */
    @Column(name = "last_updated", nullable = false)
    private Timestamp lastUpdated;

    /**
     * Busca el backup actual (único) en la base de datos.
     *
     * <p>
     * Este método implementa el patrón singleton para el backup del keystore,
     * buscando siempre la entrada con ID "1L".</p>
     *
     * @return Optional con el backup encontrado, o empty si no existe
     *
     * @see DB#find(Class)
     */
    public static Optional<KeystoreBackup> findCurrent() {
        String currentTenant = UserContext.get().getDefaultSchema();
        return DB.find(KeystoreBackup.class)
                .where().eq("id", 1L)
                .and().eq("tenantId", currentTenant)
                .findOneOrEmpty();
    }

    /**
     * Actualiza o crea el backup del keystore en la base de datos.
     *
     * <p>
     * Este método:
     * <ol>
     * <li>Busca el backup existente o crea uno nuevo</li>
     * <li>Actualiza los datos del keystore</li>
     * <li>Establece la fecha de última actualización</li>
     * <li>Guarda los cambios en la base de datos</li>
     * </ol>
     * </p>
     *
     * @param data Array de bytes con los datos serializados del keystore
     * @throws RuntimeException si ocurre un error durante la persistencia
     *
     * @see #findCurrent()
     * @see Model#save()
     */
    public static void updateBackup(byte[] data) {
        KeystoreBackup backup = findCurrent().orElse(new KeystoreBackup());
        backup.setId(1L);
        backup.setKeystoreData(data);
        backup.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        
        DB.save(backup);
    }

    /**
     * Obtiene los datos binarios del keystore almacenado.
     *
     * @return Array de bytes con los datos del keystore, o null si no hay datos
     */
    public byte[] getKeystoreData() {
        return keystoreData;
    }

    /**
     * Establece los datos binarios del keystore.
     *
     * @param keystoreData Array de bytes con los datos del keystore (puede ser
     * null)
     */
    public void setKeystoreData(byte[] keystoreData) {
        this.keystoreData = keystoreData;
    }

    /**
     * Obtiene la fecha y hora de la última actualización.
     *
     * @return Timestamp con la fecha de última actualización
     */
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Establece la fecha y hora de la última actualización.
     *
     * @param lastUpdated Timestamp con la fecha de última actualización
     */
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
