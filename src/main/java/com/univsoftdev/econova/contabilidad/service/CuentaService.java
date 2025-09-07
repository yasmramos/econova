package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.contabilidad.AccountType;
import com.univsoftdev.econova.contabilidad.NatureOfAccount;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.Ledger;
import com.univsoftdev.econova.contabilidad.repository.CuentaRepository;
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
public class CuentaService extends BaseService<Account, CuentaRepository> {

    @Inject
    public CuentaService(CuentaRepository database) {
        super(database);
    }

    public void aplicarDebito(Account cuenta, BigDecimal monto) {
        BigDecimal nuevoSaldo = cuenta.getNatureOfAccount() == NatureOfAccount.DEBTOR
                ? cuenta.getBalance().add(monto)
                : cuenta.getBalance().subtract(monto);

        cuenta.setBalance(nuevoSaldo);
        save(cuenta);
    }

    public void aplicarCredito(Account cuenta, BigDecimal monto) {
        BigDecimal nuevoSaldo = cuenta.getNatureOfAccount() == NatureOfAccount.DEBTOR
                ? cuenta.getBalance().subtract(monto)
                : cuenta.getBalance().add(monto);

        cuenta.setBalance(nuevoSaldo);
        save(cuenta);
    }

    public BigDecimal getSaldoTotalJerarquico(Long cuentaId) {
        Optional<Account> optCuenta = repository.findById(cuentaId);
        if (optCuenta.isPresent()) {
            var cuenta = optCuenta.get();
            return calcularSaldoRecursivo(cuenta);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calcularSaldoRecursivo(Account cuenta) {
        BigDecimal saldoSubcuentas = cuenta.getSubAccounts().stream()
                .map(this::calcularSaldoRecursivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return cuenta.getBalance().add(saldoSubcuentas);
    }

    public BigDecimal findCuentasSaldoNegativo() {
        return findAll().stream().filter(Account::tieneSaldoNegativo).map(Account::obtenerSaldoNegativoTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal obtenerSaldoNegativoTotal() {
        return this.findAll().stream()
                .filter(Account::tieneSaldoNegativo)
                .map(Account::obtenerSaldoNegativoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void addSubCuenta(Account cuentaPadre, Account subCuenta) {
        cuentaPadre.addSubCuenta(subCuenta);
    }

    @Transactional
    public void addCuenta(Account cuenta) {
        cuenta.setLedger(new Ledger(cuenta));
        repository.save(cuenta);
    }

    public void addCuentas(Account... cuentas) {
        for (Account cuenta : cuentas) {
            addCuenta(cuenta);
        }
    }

    public Optional<Account> findByCodigo(String codigo) {
        return repository.findByCodigo(codigo);
    }

    public List<Account> findByTipoCuenta(AccountType tipoCuenta) {
        return repository.findByTipoCuenta(tipoCuenta);
    }

    public List<Account> findCuentasPadres() {
        return repository.find(Account.class).where()
                .isNull("accountFather")
                .findList();
    }
}
