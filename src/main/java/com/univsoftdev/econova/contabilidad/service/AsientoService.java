package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.model.Unit;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.contabilidad.model.AccountingEntry;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.contabilidad.repository.AsientoRepository;
import com.univsoftdev.econova.core.service.BaseService;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
public class AsientoService extends BaseService<AccountingEntry, AsientoRepository> {

    /**
     * Constructor que inicializa el servicio con una instancia de la base de
     * datos.
     *
     * @param asientoRepository Instancia de la base de datos Ebean
     */
    @Inject
    public AsientoService(AsientoRepository asientoRepository) {
        super(asientoRepository);
    }

    public void asentarAsientos() {
        var asientos = findAll();
        for (AccountingEntry asiento : asientos) {
            if (asiento.getEstadoAsiento() != EstadoAsiento.CONFIRMADO && asiento.getEstadoAsiento() != EstadoAsiento.ERROR) {
                asiento.setEstadoAsiento(EstadoAsiento.CONFIRMADO);
            }
        }
    }

    /**
     * Valida si las transacciones de un asiento están correctamente cuadradas.
     *
     * @param asiento AccountingEntry contable a validar
     * @return `true` si las transacciones están cuadradas, `false` en caso
     * contrario
     */
    public boolean validateAsiento(AccountingEntry asiento) {
        if (asiento == null || asiento.getTransactions() == null) {
            return false;
        }

        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;

        for (Transaction transaccion : asiento.getTransactions()) {
            if (transaccion == null || transaccion.getTipo() == null || transaccion.getBalance() == null) {
                continue;
            }

            switch (transaccion.getTipo()) {
                case DEBITO ->
                    totalDebitos = totalDebitos.add(transaccion.getBalance());
                case CREDITO ->
                    totalCreditos = totalCreditos.add(transaccion.getBalance());
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
    public Optional<AccountingEntry> findByNro(int nro) {
        return repository.findByNumero(nro);
    }

    /**
     * Cambia el estado de un asiento contable y registra información de
     * auditoría.
     *
     * @param asiento AccountingEntry a modificar
     * @param estadoAsiento Nuevo estado del asiento
     * @param usuarioActual User que realizó el cambio
     */
    public void setEstadoAsiento(AccountingEntry asiento, EstadoAsiento estadoAsiento, User usuarioActual) {
        // Registrar el estado previo
        EstadoAsiento estadoAnterior = asiento.getEstadoAsiento();

        // Actualizar el estado del asiento
        asiento.setEstadoAsiento(estadoAsiento);

        // Registrar información de auditoría
        asiento.setModifiedBy(usuarioActual);
        asiento.setWhenModified(Instant.now());

        // Persistir los cambios en la base de datos
        repository.update(asiento);

        // Opción: registrar en un sistema de logs si es necesario
        log.info("Estado del asiento #{} cambiado de {} a {} por {}", asiento.getNro(), estadoAnterior, estadoAsiento, usuarioActual);
    }

    public void setEstadoAsiento(AccountingEntry asiento, EstadoAsiento estadoAsiento) {
        setEstadoAsiento(asiento, estadoAsiento, null);
    }

    @Transactional
    public Integer obtenerSiguienteCodigo(Period periodo) {
        // Validar que el período no sea nulo
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo no puede ser nulo.");
        }

        // Obtener el número consecutivo más alto en el período
        Integer maxNro = repository.find(AccountingEntry.class)
                .where().eq("periodo.id", periodo.getId())
                .orderBy("nro DESC")
                .setMaxRows(1)
                .findOneOrEmpty()
                .map(AccountingEntry::getNro)
                .orElse(0);

        // Retornar el siguiente número
        return maxNro + 1;
    }

    @Transactional
    public void deleteAsiento(Long asientoId) {
        // Obtener el asiento a eliminar
        Optional<AccountingEntry> optAsiento = repository.findById(asientoId);
        if (optAsiento.isPresent()) {
            var asiento = optAsiento.get();
            if (asiento == null) {
                throw new IllegalArgumentException("El asiento no existe.");
            }

            // Obtener el período asociado
            Period periodo = asiento.getPeriod();

            // Eliminar el asiento
            repository.delete(asiento);

            // Renumerar los asientos del período
            List<AccountingEntry> asientos = repository.find(AccountingEntry.class)
                    .where().eq("periodo.id", periodo.getId())
                    .orderBy("nro ASC")
                    .findList();

            int nuevoNumero = 1;
            for (AccountingEntry a : asientos) {
                a.setNro(nuevoNumero++);
                repository.update(a);
            }
        }

    }

    public Map<EstadoAsiento, Long> contarAsientosPorEstado(Period periodo) {
        return repository.find(AccountingEntry.class)
                .where()
                .eq("periodo.id", periodo.getId())
                .findList()
                .stream()
                .collect(Collectors.groupingBy(AccountingEntry::getEstadoAsiento,
                        Collectors.counting()
                ));
    }

    public BigDecimal obtenerTotalDebitos(Period periodo) {
        return repository.createQuery(Transaction.class)
                .where()
                .eq("asiento.periodo.id", periodo.getId())
                .eq("tipo", TipoTransaccion.DEBITO)
                .findList()
                .stream()
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void deleteAsiento(Long asientoId, String usuario) {
        Optional<AccountingEntry> optAsiento = repository.findById(asientoId);
        if (optAsiento.isPresent()) {
            deleteAsiento(asientoId);
        }
    }

    /**
     * Marca todos los asientos en edición como confirmados (terminados).
     */
    @Transactional
    public void terminarAsientos() {
        List<AccountingEntry> asientos = repository.find(AccountingEntry.class)
                .where().eq("estadoAsiento", EstadoAsiento.EDICION)
                .findList();
        for (AccountingEntry asiento : asientos) {
            if (asiento.estaCuadrado()) {
                asiento.setEstadoAsiento(EstadoAsiento.CONFIRMADO);
                repository.update(asiento);
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
        List<AccountingEntry> asientos = repository.find(AccountingEntry.class)
                .where().eq("estadoAsiento", EstadoAsiento.CONFIRMADO)
                .findList();
        for (AccountingEntry asiento : asientos) {
            for (var trans : asiento.getTransactions()) {
                if (trans.getTipo() == TipoTransaccion.DEBITO) {
                    trans.setTipo(TipoTransaccion.CREDITO);
                } else if (trans.getTipo() == TipoTransaccion.CREDITO) {
                    trans.setTipo(TipoTransaccion.DEBITO);
                }
                repository.update(asiento);
            }
        }
    }

    /**
     * Invierte todos los asientos confirmados (crea asientos inversos por cada
     * uno).
     */
    @Transactional
    public void invertirTodosAsentados() {
        List<AccountingEntry> asientos = repository.find(AccountingEntry.class)
                .where().eq("estadoAsiento", EstadoAsiento.CONFIRMADO)
                .findList();
        for (AccountingEntry asiento : asientos) {
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
    public void invertirAsiento(AccountingEntry original) {
        if (original == null) {
            return;
        }
        AccountingEntry inverso = new AccountingEntry();
        inverso.setDescription("Inverso de: " + original.getDescription());
        inverso.setFecha(java.time.LocalDate.now());
        inverso.setEstadoAsiento(EstadoAsiento.EDICION);
        inverso.setPeriod(original.getPeriod());
        for (var trans : original.getTransactions()) {
            var tInv = new com.univsoftdev.econova.contabilidad.model.Transaction();
            tInv.setAccount(trans.getAccount());
            tInv.setBalance(trans.getBalance());
            tInv.setTipo(trans.getTipo() == TipoTransaccion.DEBITO ? TipoTransaccion.CREDITO : TipoTransaccion.DEBITO);
            inverso.addTransaccion(tInv);
        }
        repository.save(inverso);
    }

    /**
     * Duplica un asiento: crea un nuevo asiento idéntico pero en estado
     * EDICION.
     */
    @Transactional
    public void duplicarAsiento(AccountingEntry original) {
        if (original == null) {
            return;
        }
        AccountingEntry copia = new AccountingEntry();
        copia.setDescription("Copia de: " + original.getDescription());
        copia.setFecha(java.time.LocalDate.now());
        copia.setEstadoAsiento(EstadoAsiento.EDICION);
        copia.setPeriod(original.getPeriod());
        for (var trans : original.getTransactions()) {
            var tCopia = new com.univsoftdev.econova.contabilidad.model.Transaction();
            tCopia.setAccount(trans.getAccount());
            tCopia.setBalance(trans.getBalance());
            tCopia.setTipo(trans.getTipo());
            copia.addTransaccion(tCopia);
        }
        repository.save(copia);
    }

    /**
     * Busca asientos por descripción o número.
     *
     * @param query
     * @return 
     */
    public List<AccountingEntry> buscar(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        try {
            int nro = Integer.parseInt(query);
            return repository.find(AccountingEntry.class)
                    .where().eq("nro", nro)
                    .findList();
        } catch (NumberFormatException ex) {
            // No es número, buscar por descripción
            return repository.find(AccountingEntry.class)
                    .where().ilike("descripcion", "%" + query + "%")
                    .findList();
        }
    }

    /**
     * Crea un nuevo asiento a partir de otro (idéntico, en estado EDICION).
     *
     * @param original
     */
    @Transactional
    public void nuevoAPartirDe(AccountingEntry original) {
        duplicarAsiento(original);
    }

    public AccountingEntry crearAsiento(int nro, String descripcion, LocalDate fecha, Period periodo, Unit unidad, EstadoAsiento estadoAsiento) {
        return new AccountingEntry(nro, descripcion, fecha, periodo, unidad, estadoAsiento);
    }
    
}
