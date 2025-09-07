package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.contabilidad.repository.TransactionRepository;
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
public class TransaccionService extends BaseService<Transaction, TransactionRepository> {

    @Inject
    public TransaccionService(TransactionRepository transactionRepository) {
        super(transactionRepository);
    }

    @Transactional
    @Override
    public void update(Transaction transaccion) {
        if (transaccion.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo.");
        }
        repository.update(transaccion);
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<Transaction> optTransaccion = repository.findById(id);
        if (optTransaccion.isPresent()) {
            var transaction = optTransaccion.get();
            transaction.softDelete();
            repository.update(transaction);
            return true;
        }
        return false;
    }

    public List<Transaction> findByMonto(BigDecimal monto) {
        return repository.find(Transaction.class).where().eq("monto", monto).findList();
    }

    public BigDecimal calcularSaldoPorCuenta(Long cuentaId) {
        return repository.find(Transaction.class)
                .where().eq("cuenta.id", cuentaId)
                .findList()
                .stream()
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void updateWithAudit(Transaction transaccion, User usuarioActual) {
        transaccion.setModifiedBy(usuarioActual);
        repository.update(transaccion);
    }

    public List<Transaction> findPaginated(int page, int pageSize) {
        return repository.find(Transaction.class)
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findList();
    }

}
