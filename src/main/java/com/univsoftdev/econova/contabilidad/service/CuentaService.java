package com.univsoftdev.econova.contabilidad.service;

import jakarta.inject.Singleton;
import com.univsoftdev.econova.contabilidad.NaturalezaCuenta;
import com.univsoftdev.econova.contabilidad.TipoApertura;
import com.univsoftdev.econova.contabilidad.TipoCuenta;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.model.LibroMayor;
import com.univsoftdev.econova.core.Service;

import io.ebean.Database;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class CuentaService extends Service<Cuenta> {

    @Inject
    public CuentaService(Database database) {
        super(database, Cuenta.class);
    }

    public void aplicarDebito(Cuenta cuenta, BigDecimal monto) {
        BigDecimal nuevoSaldo = cuenta.getNaturaleza() == NaturalezaCuenta.DEUDORA
                ? cuenta.getSaldo().add(monto)
                : cuenta.getSaldo().subtract(monto);

        cuenta.setSaldo(nuevoSaldo);
        save(cuenta);
    }

    public void aplicarCredito(Cuenta cuenta, BigDecimal monto) {
        BigDecimal nuevoSaldo = cuenta.getNaturaleza() == NaturalezaCuenta.DEUDORA
                ? cuenta.getSaldo().subtract(monto)
                : cuenta.getSaldo().add(monto);

        cuenta.setSaldo(nuevoSaldo);
        save(cuenta);
    }

    public BigDecimal getSaldoTotalJerarquico(Long cuentaId) {
        Cuenta cuenta = findById(cuentaId);
        return calcularSaldoRecursivo(cuenta);
    }

    private BigDecimal calcularSaldoRecursivo(Cuenta cuenta) {
        BigDecimal saldoSubcuentas = cuenta.getSubCuentas().stream()
                .map(this::calcularSaldoRecursivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return cuenta.getSaldo().add(saldoSubcuentas);
    }

    public BigDecimal findCuentasSaldoNegativo() {
        return findAll().stream().filter(Cuenta::tieneSaldoNegativo).map(Cuenta::obtenerSaldoNegativoTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal obtenerSaldoNegativoTotal() {
        return this.findAll().stream()
                .filter(Cuenta::tieneSaldoNegativo)
                .map(Cuenta::obtenerSaldoNegativoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void addSubCuenta(Cuenta cuentaPadre, Cuenta subCuenta) {
        cuentaPadre.addSubCuenta(subCuenta);
    }

    @Transactional
    public void addCuenta(Cuenta cuenta) {
        cuenta.setLibroMayor(new LibroMayor(cuenta));
        database.save(cuenta);
    }

    public void addCuentas(Cuenta... cuentas) {
        for (Cuenta cuenta : cuentas) {
            addCuenta(cuenta);
        }
    }

    public Optional<Cuenta> findByCodigo(String codigo){
        return findBy("codigo", codigo);
    }

    public List<Cuenta> findByTipoCuenta(TipoCuenta tipoCuenta) {
        return findAll().stream().filter( c -> c.getTipoCuenta() == tipoCuenta).toList();
    }
}
