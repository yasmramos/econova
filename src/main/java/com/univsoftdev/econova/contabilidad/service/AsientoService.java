package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.core.Service;
import io.ebean.Database;
import jakarta.inject.Singleton;
import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.config.model.User;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la gestión de asientos contables. Proporciona operaciones
 * específicas para validar, buscar y modificar el estado de los asientos.
 */
@Slf4j
@Singleton
public class AsientoService extends Service<Asiento> {

    /**
     * Constructor que inicializa el servicio con una instancia de la base de
     * datos.
     *
     * @param database Instancia de la base de datos Ebean
     */
    @Inject
    public AsientoService(Database database) {
        super(database, Asiento.class);
    }

    public void asentarAsientos() {
        var asientos = findAll();
        for (Asiento asiento : asientos) {
            if (asiento.getEstadoAsiento() != EstadoAsiento.CONFIRMADO && asiento.getEstadoAsiento() != EstadoAsiento.ERROR) {
                asiento.setEstadoAsiento(EstadoAsiento.CONFIRMADO);
            }
        }
    }

    /**
     * Valida si las transacciones de un asiento están correctamente cuadradas.
     *
     * @param asiento Asiento contable a validar
     * @return `true` si las transacciones están cuadradas, `false` en caso
     * contrario
     */
    public boolean validateAsiento(Asiento asiento) {
        if (asiento == null || asiento.getTransacciones() == null) {
            return false;
        }

        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;

        for (Transaccion transaccion : asiento.getTransacciones()) {
            if (transaccion == null || transaccion.getTipo() == null || transaccion.getMonto() == null) {
                continue;
            }

            switch (transaccion.getTipo()) {
                case DEBITO ->
                    totalDebitos = totalDebitos.add(transaccion.getMonto());
                case CREDITO ->
                    totalCreditos = totalCreditos.add(transaccion.getMonto());
                default -> {
                }
            }
        }

        return totalDebitos.compareTo(totalCreditos) == 0;
    }

    /**
     * Busca un asiento por su número.
     *
     * Utiliza el atributo único `nro` para localizar un asiento específico.
     *
     * @param nro Número del asiento
     * @return Un Optional con el asiento encontrado, o vacío si no se encuentra
     */
    public Optional<Asiento> findByNro(int nro) {
        return findBy("nro", nro);
    }

    /**
     * Cambia el estado de un asiento contable y registra información de
     * auditoría.
     *
     * @param asiento Asiento a modificar
     * @param estadoAsiento Nuevo estado del asiento
     * @param usuarioActual User que realizó el cambio
     */
    public void setEstadoAsiento(Asiento asiento, EstadoAsiento estadoAsiento, User usuarioActual) {
        // Registrar el estado previo
        EstadoAsiento estadoAnterior = asiento.getEstadoAsiento();

        // Actualizar el estado del asiento
        asiento.setEstadoAsiento(estadoAsiento);

        // Registrar información de auditoría
        asiento.setWhoModified(usuarioActual);
        asiento.setWhenModified(Instant.now());

        // Persistir los cambios en la base de datos
        database.update(asiento);

        // Opción: registrar en un sistema de logs si es necesario
        log.info("Estado del asiento #{} cambiado de {} a {} por {}", asiento.getNro(), estadoAnterior, estadoAsiento, usuarioActual);
    }

    public void setEstadoAsiento(Asiento asiento, EstadoAsiento estadoAsiento) {
        setEstadoAsiento(asiento, estadoAsiento, null);
    }

    @Transactional
    public Integer obtenerSiguienteCodigo(Periodo periodo) {
        // Validar que el período no sea nulo
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo no puede ser nulo.");
        }

        // Obtener el número consecutivo más alto en el período
        Integer maxNro = database.find(Asiento.class)
                .where().eq("periodo.id", periodo.getId())
                .orderBy("nro DESC")
                .setMaxRows(1)
                .findOneOrEmpty()
                .map(Asiento::getNro)
                .orElse(0);

        // Retornar el siguiente número
        return maxNro + 1;
    }

    @Transactional
    public void deleteAsiento(Long asientoId) {
        // Obtener el asiento a eliminar
        Asiento asiento = findById(asientoId);
        if (asiento == null) {
            throw new IllegalArgumentException("El asiento no existe.");
        }

        // Obtener el período asociado
        Periodo periodo = asiento.getPeriodo();

        // Eliminar el asiento
        database.delete(asiento);

        // Renumerar los asientos del período
        List<Asiento> asientos = database.find(Asiento.class)
                .where().eq("periodo.id", periodo.getId())
                .orderBy("nro ASC")
                .findList();

        int nuevoNumero = 1;
        for (Asiento a : asientos) {
            a.setNro(nuevoNumero++);
            database.update(a);
        }
    }

    public Map<EstadoAsiento, Long> contarAsientosPorEstado(Periodo periodo) {
        return database.find(Asiento.class)
                .where()
                .eq("periodo.id", periodo.getId())
                .findList()
                .stream()
                .collect(Collectors.groupingBy(
                        Asiento::getEstadoAsiento,
                        Collectors.counting()
                ));
    }

    public BigDecimal obtenerTotalDebitos(Periodo periodo) {
        return database.createQuery(Transaccion.class)
                .where()
                .eq("asiento.periodo.id", periodo.getId())
                .eq("tipo", TipoTransaccion.DEBITO)
                .findList()
                .stream()
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void deleteAsiento(Long asientoId, String usuario) {
        Asiento asiento = findById(asientoId);

        if (!tienePermisoEliminacion(usuario, asiento)) {
            throw new SecurityException("Usuario no autorizado para eliminar este asiento");
        }

        deleteAsiento(asientoId);
    }

    private boolean tienePermisoEliminacion(String usuario, Asiento asiento) {
        // Lógica de verificación de permisos
        // Puede integrarse con Spring Security o otro sistema de autorización
        return true; // Implementación temporal
    }

    /**
     * Marca todos los asientos en edición como confirmados (terminados).
     */
    @Transactional
    public void terminarAsientos() {
        List<Asiento> asientos = database.find(Asiento.class)
                .where().eq("estadoAsiento", EstadoAsiento.EDICION)
                .findList();
        for (Asiento asiento : asientos) {
            if (asiento.estaCuadrado()) {
                asiento.setEstadoAsiento(EstadoAsiento.CONFIRMADO);
                database.update(asiento);
            }
        }
    }

    /**
     * Invierte los saldos de la unidad actual (ejemplo: invierte débitos y
     * créditos de todos los asientos del periodo activo).
     */
    @Transactional
    public void invertirSaldosUnidad() {
        // Ejemplo: invierte todos los asientos del periodo activo
        // (En un sistema real, se filtraría por unidad/periodo actual)
        List<Asiento> asientos = database.find(Asiento.class)
                .where().eq("estadoAsiento", EstadoAsiento.CONFIRMADO)
                .findList();
        for (Asiento asiento : asientos) {
            for (var trans : asiento.getTransacciones()) {
                if (trans.getTipo() == TipoTransaccion.DEBITO) {
                    trans.setTipo(TipoTransaccion.CREDITO);
                } else if (trans.getTipo() == TipoTransaccion.CREDITO) {
                    trans.setTipo(TipoTransaccion.DEBITO);
                }
                database.update(trans);
            }
        }
    }

    /**
     * Invierte todos los asientos confirmados (crea asientos inversos por cada
     * uno).
     */
    @Transactional
    public void invertirTodosAsentados() {
        List<Asiento> asientos = database.find(Asiento.class)
                .where().eq("estadoAsiento", EstadoAsiento.CONFIRMADO)
                .findList();
        for (Asiento asiento : asientos) {
            invertirAsiento(asiento);
        }
    }

    /**
     * Importa asientos desde un archivo (ejemplo: XML o CSV).
     */
    @Transactional
    public void importarDesdeArchivo(java.io.File archivo) {
        // Implementación de ejemplo: solo loguea el nombre del archivo
        // En un sistema real, se parsearía el archivo y se crearían asientos
        log.info("Importando asientos desde archivo: {}", archivo.getAbsolutePath());
        // TODO: Implementar importación real (XML/CSV)
    }

    /**
     * Invierte un asiento: crea un nuevo asiento con transacciones invertidas.
     */
    @Transactional
    public void invertirAsiento(Asiento original) {
        if (original == null) {
            return;
        }
        Asiento inverso = new Asiento();
        inverso.setDescripcion("Inverso de: " + original.getDescripcion());
        inverso.setFecha(java.time.LocalDate.now());
        inverso.setEstadoAsiento(EstadoAsiento.EDICION);
        inverso.setPeriodo(original.getPeriodo());
        for (var trans : original.getTransacciones()) {
            var tInv = new com.univsoftdev.econova.contabilidad.model.Transaccion();
            tInv.setCuenta(trans.getCuenta());
            tInv.setMonto(trans.getMonto());
            tInv.setTipo(trans.getTipo() == TipoTransaccion.DEBITO ? TipoTransaccion.CREDITO : TipoTransaccion.DEBITO);
            inverso.addTransaccion(tInv);
        }
        database.save(inverso);
    }

    /**
     * Duplica un asiento: crea un nuevo asiento idéntico pero en estado
     * EDICION.
     */
    @Transactional
    public void duplicarAsiento(Asiento original) {
        if (original == null) {
            return;
        }
        Asiento copia = new Asiento();
        copia.setDescripcion("Copia de: " + original.getDescripcion());
        copia.setFecha(java.time.LocalDate.now());
        copia.setEstadoAsiento(EstadoAsiento.EDICION);
        copia.setPeriodo(original.getPeriodo());
        for (var trans : original.getTransacciones()) {
            var tCopia = new com.univsoftdev.econova.contabilidad.model.Transaccion();
            tCopia.setCuenta(trans.getCuenta());
            tCopia.setMonto(trans.getMonto());
            tCopia.setTipo(trans.getTipo());
            copia.addTransaccion(tCopia);
        }
        database.save(copia);
    }

    /**
     * Busca asientos por descripción o número.
     * @param query
     */
    public List<Asiento> buscar(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        try {
            int nro = Integer.parseInt(query);
            return database.find(Asiento.class)
                    .where().eq("nro", nro)
                    .findList();
        } catch (NumberFormatException ex) {
            // No es número, buscar por descripción
            return database.find(Asiento.class)
                    .where().ilike("descripcion", "%" + query + "%")
                    .findList();
        }
    }

    /**
     * Crea un nuevo asiento a partir de otro (idéntico, en estado EDICION).
     * @param original
     */
    @Transactional
    public void nuevoAPartirDe(Asiento original) {
        duplicarAsiento(original);
    }
}
