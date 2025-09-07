package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Company;
import com.univsoftdev.econova.config.model.Unit;
import com.univsoftdev.econova.config.repository.UnitRepository;
import com.univsoftdev.econova.contabilidad.model.AccountingEntry;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import com.univsoftdev.econova.core.service.BaseService;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class UnitService extends BaseService<Unit, UnitRepository> {

    @Inject
    public UnitService(UnitRepository repository) {
        super(repository);
    }

    @Transactional
    public Optional<Unit> findByCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }
        return repository.findByCodigo(codigo);
    }

    @Transactional
    public Unit crearUnidad(String codigo, String nombre, String direccion, String correo,
            String nae, String dpa, String reup, Company company) {

        validarParametrosCreacion(codigo, nombre);
        validarCodigoUnico(codigo);
        validarEmail(correo);

        Unit nuevaUnidad = new Unit(codigo, nombre, direccion, correo, nae, dpa, reup, company);
        save(nuevaUnidad);

        log.info("Nueva unidad creada: {} - {}", codigo, nombre);
        return nuevaUnidad;
    }

    @Transactional
    public Unit crearUnidad(String codigo, String nombre, Company company) {
        validarParametrosCreacion(codigo, nombre);
        validarCodigoUnico(codigo);

        Unit nuevaUnidad = new Unit(codigo, nombre, company);
        save(nuevaUnidad);

        log.info("Nueva unidad creada: {} - {}", codigo, nombre);
        return nuevaUnidad;
    }

    @Transactional
    public Unit asignarEmpresa(Long unidadId, Long empresaId) {
        validarIds(unidadId, empresaId);

        Unit unidad = findById(unidadId)
                .orElseThrow(() -> new BusinessLogicException("Unidad no encontrada con ID: " + unidadId));

        Company empresa = repository.find(Company.class, empresaId);
        if (empresa == null) {
            throw new BusinessLogicException("Empresa no encontrada con ID: " + empresaId);
        }

        unidad.setCompany(empresa);
        save(unidad); // En Ebean se usa save() para actualizar

        log.info("Unidad {} asociada a empresa {}", unidad.getCode(), empresa.getName());
        return unidad;
    }

    @Transactional
    public Unit agregarTransaccion(Long unidadId, Transaction transaccion) {
        if (unidadId == null) {
            throw new BusinessLogicException("El ID de la unidad no puede ser nulo");
        }
        if (transaccion == null) {
            throw new BusinessLogicException("La transacción no puede ser nula");
        }

        Unit unidad = findById(unidadId)
                .orElseThrow(() -> new BusinessLogicException("Unidad no encontrada con ID: " + unidadId));

        unidad.addTransaccion(transaccion);
        save(unidad);

        log.debug("Transacción agregada a unidad {}", unidad.getCode());
        return unidad;
    }

    @Transactional
    public Unit agregarAsiento(Long unidadId, AccountingEntry asiento) {
        if (unidadId == null) {
            throw new BusinessLogicException("El ID de la unidad no puede ser nulo");
        }
        if (asiento == null) {
            throw new BusinessLogicException("El asiento no puede ser nulo");
        }

        Unit unidad = findById(unidadId)
                .orElseThrow(() -> new BusinessLogicException("Unidad no encontrada con ID: " + unidadId));

        unidad.addAsiento(asiento);
        save(unidad);

        log.info("Asiento {} agregado a unidad {}", asiento.getNro(), unidad.getCode());
        return unidad;
    }

    @Transactional
    public BigDecimal obtenerSaldoTotal(Long unidadId) {
        if (unidadId == null) {
            throw new BusinessLogicException("El ID de la unidad no puede ser nulo");
        }

        Unit unidad = findById(unidadId)
                .orElseThrow(() -> new BusinessLogicException("Unidad no encontrada con ID: " + unidadId));

        return unidad.getTransactions().stream()
                .map(t -> t.esDebito() ? t.getBalance() : t.getBalance().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public List<Transaction> obtenerTransacciones(Long unidadId) {
        if (unidadId == null) {
            return List.of();
        }

        return repository.createQuery(Transaction.class)
                .where()
                .eq("unit.id", unidadId)
                .orderBy("fecha asc")
                .findList();
    }

    @Transactional
    public List<AccountingEntry> obtenerAsientos(Long unidadId) {
        if (unidadId == null) {
            return List.of();
        }

        return repository.createQuery(AccountingEntry.class)
                .where()
                .eq("unit.id", unidadId)
                .orderBy("fecha asc")
                .findList();
    }

    @Transactional
    public Optional<Unit> obtenerPorCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }

        return repository.createQuery(Unit.class)
                .where()
                .eq("code", code)
                .findOneOrEmpty();
    }

    @Transactional
    public List<Unit> obtenerUnidadesPorEmpresa(Long empresaId) {
        if (empresaId == null) {
            return List.of();
        }

        return repository.createQuery(Unit.class)
                .where()
                .eq("empresa.id", empresaId)
                .orderBy("nombre asc")
                .findList();
    }

    private void validarCodigoUnico(String code) {
        if (obtenerPorCode(code).isPresent()) {
            throw new BusinessLogicException("Ya existe una unidad con el código: " + code);
        }
    }

    private void validarEmail(String email) {
        if (email != null && !email.isEmpty() && !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BusinessLogicException("El formato del email no es válido");
        }
    }

    private void validarParametrosCreacion(String codigo, String nombre) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new BusinessLogicException("El código no puede ser nulo o vacío");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessLogicException("El nombre no puede ser nulo o vacío");
        }
    }

    private void validarIds(Long unidadId, Long empresaId) {
        if (unidadId == null) {
            throw new BusinessLogicException("El ID de la unidad no puede ser nulo");
        }
        if (empresaId == null) {
            throw new BusinessLogicException("El ID de la empresa no puede ser nulo");
        }
    }

    @Transactional
    public Unit actualizarDatos(Long unidadId, String nombre, String direccion, String correo) {
        if (unidadId == null) {
            throw new BusinessLogicException("El ID de la unidad no puede ser nulo");
        }

        Unit unit = findById(unidadId)
                .orElseThrow(() -> new BusinessLogicException("Unidad no encontrada con ID: " + unidadId));

        if (nombre != null && !nombre.trim().isEmpty()) {
            unit.setName(nombre);
        }
        if (direccion != null) {
            unit.setAddress(direccion);
        }
        if (correo != null) {
            validarEmail(correo);
            unit.setEmail(correo);
        }

        save(unit);
        log.info("Datos actualizados para unidad {}", unit.getCode());
        return unit;
    }

    @Transactional
    public BalanceUnidad obtenerBalance(Long unidadId) {
        if (unidadId == null) {
            throw new BusinessLogicException("El ID de la unidad no puede ser nulo");
        }

        Unit unidad = findById(unidadId)
                .orElseThrow(() -> new BusinessLogicException("Unidad no encontrada con ID: " + unidadId));

        BigDecimal totalDebitos = unidad.getTransactions().stream()
                .filter(Transaction::esDebito)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditos = unidad.getTransactions().stream()
                .filter(Transaction::esCredito)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalDebitos.subtract(totalCreditos);

        return new BalanceUnidad(totalDebitos, totalCreditos, saldo);
    }

    public record BalanceUnidad(BigDecimal totalDebitos, BigDecimal totalCreditos, BigDecimal saldo) {

        public BalanceUnidad   {
            totalDebitos = totalDebitos != null ? totalDebitos : BigDecimal.ZERO;
            totalCreditos = totalCreditos != null ? totalCreditos : BigDecimal.ZERO;
            saldo = saldo != null ? saldo : BigDecimal.ZERO;
        }
    }

    // Método auxiliar para obtener unidad o lanzar excepción
    @Override
    public Optional<Unit> findById(Long id) {
        return repository.findById(id);
    }
}
