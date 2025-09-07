package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.config.model.Empresa;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.core.Service;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import io.ebean.Database;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio avanzado para gestión de unidades organizativas. Incluye operaciones
 * para manejo de unidades, transacciones y asientos contables.
 */
@Slf4j
@Singleton
public class UnidadService extends Service<Unidad> {

    @Inject
    public UnidadService(Database database) {
        super(database, Unidad.class);
    }

    /**
     * Busca una entidad {@link Unidad} por su código único.
     *
     * @param codigo Código identificador de la unidad
     * @return La entidad encontrada, o {@code null} si no existe
     */
    public Optional<Unidad> findByCodigo(String codigo) {
        return findBy("codigo", codigo);
    }

    /**
     * Crea una nueva unidad organizativa con validaciones básicas
     */
    public Unidad crearUnidad(String codigo, String nombre, String direccion, String correo,
            String nae, String dpa, String reup, Empresa empresa) {
        validarCodigoUnico(codigo);
        validarEmail(correo);

        Unidad nuevaUnidad = new Unidad(codigo, nombre, direccion, correo, nae, dpa, reup);
        nuevaUnidad.setEmpresa(empresa);
        database.save(nuevaUnidad);

        log.info("Nueva unidad creada: {} - {}", codigo, nombre);
        return nuevaUnidad;
    }

    public Unidad crearUnidad(String codigo, String nombre, Empresa empresa, String schema) {
        validarCodigoUnico(codigo);

        Unidad nuevaUnidad = new Unidad(codigo, nombre);
        nuevaUnidad.setEmpresa(empresa);
        database.save(nuevaUnidad);

        log.info("Nueva unidad creada: {} - {}", codigo, nombre);
        return nuevaUnidad;
    }

    /**
     * Asocia una unidad a una empresa
     * @param unidadId
     * @param empresaId
     * @return 
     */
    public Unidad asignarEmpresa(Long unidadId, Long empresaId) {
        Unidad unidad = database.find(Unidad.class, unidadId);

        Empresa empresa = database.find(Empresa.class, empresaId);

        unidad.setEmpresa(empresa);
        database.update(unidad);

        log.info("Unidad {} asociada a empresa {}", unidad.getCodigo(), empresa.getName());
        return unidad;
    }

    /**
     * Agrega una transacción a la unidad
     * @param unidadId
     * @param transaccion
     * @return 
     */
    public Unidad agregarTransaccion(Long unidadId, Transaccion transaccion) {
        Unidad unidad = database.find(Unidad.class, unidadId);

        unidad.addTransaccion(transaccion);
        database.update(unidad);

        log.debug("Transacción agregada a unidad {}", unidad.getCodigo());
        return unidad;
    }

    /**
     * Agrega un asiento contable a la unidad
     * @param unidadId
     * @param asiento
     * @return 
     */
    public Unidad agregarAsiento(Long unidadId, Asiento asiento) {
        Unidad unidad = database.find(Unidad.class, unidadId);

        unidad.addAsiento(asiento);
        database.update(unidad);

        log.info("Asiento {} agregado a unidad {}", asiento.getNro(), unidad.getCodigo());
        return unidad;
    }

    /**
     * Obtiene el saldo total de la unidad (débitos - créditos)
     * @param unidadId
     * @return 
     */
    public BigDecimal obtenerSaldoTotal(Long unidadId) {
        Unidad unidad = database.find(Unidad.class, unidadId);

        return unidad.getTransacciones().stream()
                .map(t -> t.esDebito() ? t.getMonto() : t.getMonto().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtiene todas las transacciones de la unidad ordenadas por fecha
     */
    public List<Transaccion> obtenerTransacciones(Long unidadId) {
        return database.createQuery(Transaccion.class)
                .where()
                .eq("unidad.id", unidadId)
                .orderBy("fecha asc")
                .findList();
    }

    /**
     * Obtiene todos los asientos de la unidad ordenados por fecha
     */
    public List<Asiento> obtenerAsientos(Long unidadId) {
        return database.createQuery(Asiento.class)
                .where()
                .eq("unidad.id", unidadId)
                .orderBy("fecha asc")
                .findList();
    }

    /**
     * Obtiene una unidad por su código único
     */
    public Optional<Unidad> obtenerPorCodigo(String codigo) {
        return database.createQuery(Unidad.class)
                .where()
                .eq("codigo", codigo)
                .findOneOrEmpty();
    }

    /**
     * Obtiene todas las unidades de una empresa específica
     */
    public List<Unidad> obtenerUnidadesPorEmpresa(Long empresaId) {
        return database.createQuery(Unidad.class)
                .where()
                .eq("empresa.id", empresaId)
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Valida que el código de unidad sea único
     */
    private void validarCodigoUnico(String codigo) {
        if (obtenerPorCodigo(codigo).isPresent()) {
            throw new BusinessLogicException("Ya existe una unidad con el código: " + codigo);
        }
    }

    /**
     * Valida el formato del email (si está presente)
     */
    private void validarEmail(String email) {
        if (email != null && !email.isEmpty() && !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BusinessLogicException("El formato del email no es válido");
        }
    }

    /**
     * Actualiza los datos básicos de una unidad
     */
    public Unidad actualizarDatos(Long unidadId, String nombre, String direccion, String correo) {
        Unidad unidad = database.find(Unidad.class, unidadId);

        if (nombre != null) {
            unidad.setNombre(nombre);
        }
        if (direccion != null) {
            unidad.setDireccion(direccion);
        }
        if (correo != null) {
            validarEmail(correo);
            unidad.setCorreo(correo);
        }

        database.update(unidad);
        log.info("Datos actualizados para unidad {}", unidad.getCodigo());
        return unidad;
    }

    /**
     * Obtiene el balance resumido de la unidad (total débitos, créditos y
     * saldo)
     */
    public BalanceUnidad obtenerBalance(Long unidadId) {
        Unidad unidad = database.find(Unidad.class, unidadId);

        BigDecimal totalDebitos = unidad.getTransacciones().stream()
                .filter(Transaccion::esDebito)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditos = unidad.getTransacciones().stream()
                .filter(Transaccion::esCredito)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BalanceUnidad(totalDebitos, totalCreditos, totalDebitos.subtract(totalCreditos));
    }

    /**
     * Record para representar el balance de una unidad
     */
    public record BalanceUnidad(BigDecimal totalDebitos, BigDecimal totalCreditos, BigDecimal saldo) {

    }
}
