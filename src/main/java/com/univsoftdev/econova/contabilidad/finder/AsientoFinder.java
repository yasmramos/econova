package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import io.ebean.Finder;
import java.time.LocalDate;
import java.util.List;

public class AsientoFinder extends Finder<Long, Asiento> {

    public AsientoFinder() {
        super(Asiento.class);
    }

    public List<Asiento> findByPeriodo(Periodo periodo) {
        return db().find(Asiento.class)
                .where()
                .eq("periodo.id", periodo.getId())
                .findList();
    }

    public List<Asiento> findConfirmadosByFecha(LocalDate desde, LocalDate hasta) {
        return db().find(Asiento.class)
                .where()
                .eq("estadoAsiento", EstadoAsiento.CONFIRMADO)
                .ge("fecha", desde)
                .le("fecha", hasta)
                .findList();
    }
}
