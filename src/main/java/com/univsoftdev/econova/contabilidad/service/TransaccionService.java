package com.univsoftdev.econova.contabilidad.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.core.Service;
import io.ebean.Database;
import io.ebean.annotation.Transactional;
import jakarta.inject.Singleton;
import com.univsoftdev.econova.config.model.User;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class TransaccionService extends Service<Transaccion> {

    @Inject
    public TransaccionService(Database database) {
        super(database, Transaccion.class);
    }

    @Transactional
    @Override
    public void update(Transaccion transaccion) {
        if (transaccion.getMonto().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo.");
        }
        database.update(transaccion);
    }

    @Transactional
    public boolean delete(Long id) {
        Transaccion transaccion = findById(id);
        transaccion.setDeleted(true);
        database.update(transaccion);
        return true;
    }

    public List<Transaccion> findByMonto(BigDecimal monto) {
        return database.find(Transaccion.class).where().eq("monto", monto).findList();
    }

    public BigDecimal calcularSaldoPorCuenta(Long cuentaId) {
        return database.find(Transaccion.class)
                .where().eq("cuenta.id", cuentaId)
                .findList()
                .stream()
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void updateWithAudit(Transaccion transaccion, User usuarioActual) {
        transaccion.setWhoModified(usuarioActual);
        database.update(transaccion);
    }

    public List<Transaccion> findPaginated(int page, int pageSize) {
        return database.find(Transaccion.class)
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findList();
    }

}
