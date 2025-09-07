package com.univsoftdev.econova.contabilidad.repository;

import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class TransactionRepository extends BaseRepository<Transaction> {

    @Inject
    public TransactionRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Transaction> getEntityType() {
        return Transaction.class;
    }

    @Override
    public List<Transaction> findByCriteria(String criteria) {
        return database.find(Transaction.class)
                .where()
                .ilike("description", "%" + criteria + "%")
                .or()
                .eq("monto", new BigDecimal(criteria))
                .findList();
    }

    public List<Transaction> findByFecha(LocalDate fecha) {
        return database.find(Transaction.class)
                .where()
                .eq("fecha", fecha)
                .orderBy("id asc")
                .findList();
    }

    public List<Transaction> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return database.find(Transaction.class)
                .where()
                .ge("fecha", fechaInicio)
                .le("fecha", fechaFin)
                .orderBy("fecha asc, id asc")
                .findList();
    }

    public List<Transaction> findByCuenta(Long cuentaId) {
        return database.find(Transaction.class)
                .where()
                .eq("cuenta.id", cuentaId)
                .orderBy("fecha asc, id asc")
                .findList();
    }

    public List<Transaction> findByAsiento(Long asientoId) {
        return database.find(Transaction.class)
                .where()
                .eq("asiento.id", asientoId)
                .orderBy("id asc")
                .findList();
    }

    public List<Transaction> findByMoneda(String monedaCodigo) {
        return database.find(Transaction.class)
                .where()
                .eq("moneda.symbol", monedaCodigo)
                .orderBy("fecha desc, id desc")
                .findList();
    }

    public List<Transaction> findByTipo(TipoTransaccion tipo) {
        return database.find(Transaction.class)
                .where()
                .eq("tipo", tipo)
                .orderBy("fecha desc, id desc")
                .findList();
    }

    public List<Transaction> findByLibroMayor(Long libroId) {
        return database.find(Transaction.class)
                .where()
                .eq("libroMayor.id", libroId)
                .orderBy("fecha desc, id desc")
                .findList();
    }

    public List<Transaction> findByExercise(Long exerciseId) {
        return database.find(Transaction.class)
                .where()
                .eq("exercise.id", exerciseId)
                .orderBy("fecha desc, id desc")
                .findList();
    }

    public List<Transaction> findByExerciseYRangoFechas(Long exerciseId, LocalDate fechaInicio, LocalDate fechaFin) {
        return database.find(Transaction.class)
                .where()
                .eq("exercise.id", exerciseId)
                .ge("fecha", fechaInicio)
                .le("fecha", fechaFin)
                .orderBy("fecha asc, id asc")
                .findList();
    }

    public List<Transaction> obtenerTransaccionesOrdenadasPorFecha() {
        return database.find(Transaction.class)
                .orderBy("fecha desc, id desc")
                .findList();
    }

    public List<Transaction> obtenerTransaccionesPorCuentaYFecha(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin) {
        return database.find(Transaction.class)
                .where()
                .eq("cuenta.id", cuentaId)
                .ge("fecha", fechaInicio)
                .le("fecha", fechaFin)
                .orderBy("fecha asc, id asc")
                .findList();
    }

    public BigDecimal getTotalMontoPorTipoYFecha(TipoTransaccion tipo, LocalDate fecha) {
        // Para calcular totales, necesitarías una consulta específica
        return BigDecimal.ZERO; // Placeholder
    }

    public BigDecimal getTotalDebitosPorPeriodo(Long periodoId) {
        // Para calcular totales, necesitarías una consulta específica
        return BigDecimal.ZERO; // Placeholder
    }

    public BigDecimal getTotalCreditosPorPeriodo(Long periodoId) {
        // Para calcular totales, necesitarías una consulta específica
        return BigDecimal.ZERO; // Placeholder
    }

    public boolean existeTransaccionConId(Long id) {
        return database.find(Transaction.class)
                .where()
                .eq("id", id)
                .exists();
    }

    public List<Transaction> obtenerTransaccionesPorUsuario(Long usuarioId) {
        return database.find(Transaction.class)
                .where()
                .eq("usuario.id", usuarioId)
                .orderBy("fecha desc, id desc")
                .findList();
    }

    public List<Transaction> obtenerUltimasTransacciones(int limite) {
        return database.find(Transaction.class)
                .orderBy("fecha desc, id desc")
                .setMaxRows(limite)
                .findList();
    }
}
