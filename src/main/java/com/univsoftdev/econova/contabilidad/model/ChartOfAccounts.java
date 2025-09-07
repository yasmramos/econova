package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.contabilidad.TipoCuenta;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cont_plan_cuentas")
public class PlanDeCuentas extends BaseModel {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El nombre del plan de cuentas no puede estar vacío.")
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas = new ArrayList<>();

    public PlanDeCuentas() {
    }

    public PlanDeCuentas(String nombre) {
        Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.nombre = nombre;
    }

    /**
     * Obtiene todas las subcuentas del plan de cuentas.
     *
     * @return Lista de subcuentas.
     */
    public List<Cuenta> obtenerSubCuentas() {
        return this.cuentas.stream()
                .flatMap(cuenta -> cuenta.getSubCuentas().stream())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los controles del plan de cuentas.
     *
     * @return Lista de controles.
     */
    public List<Cuenta> obtenerControles() {
        return this.obtenerSubCuentas().stream()
                .flatMap(subCuenta -> subCuenta.getSubCuentas().stream())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los subcontroles del plan de cuentas.
     *
     * @return Lista de subcontroles.
     */
    public List<Cuenta> obtenerSubControles() {
        return this.obtenerControles().stream()
                .flatMap(control -> control.getSubCuentas().stream())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los análisis del plan de cuentas.
     *
     * @return Lista de análisis.
     */
    public List<Cuenta> obtenerAnalisis() {
        return this.obtenerSubControles().stream()
                .flatMap(subControl -> subControl.getSubCuentas().stream())
                .collect(Collectors.toList());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    /**
     * Obtiene todas las cuentas del plan de cuentas.
     *
     * @return Lista de cuentas.
     */
    public List<Cuenta> obtenerCuentas() {
        return this.cuentas;
    }

    /**
     * Agrega una cuenta al plan de cuentas.
     *
     * @param cuenta La cuenta a agregar.
     */
    public void agregarCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula.");
        }
        cuenta.setPlanDeCuenta(this);
        this.cuentas.add(cuenta);
    }

    /**
     * Elimina una cuenta del plan de cuentas.
     *
     * @param cuenta La cuenta a eliminar.
     */
    public void eliminarCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula.");
        }
        cuenta.setPlanDeCuenta(null);
        this.cuentas.remove(cuenta);
    }

    /**
     * Busca una cuenta por su código.
     *
     * @param codigo El código de la cuenta a buscar.
     * @return La cuenta encontrada, o null si no existe.
     */
    public Optional<Cuenta> buscarCuentaPorCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new IllegalArgumentException("El código no puede ser nulo o vacío.");
        }
        return this.cuentas.stream()
                .filter(cuenta -> cuenta.getCodigo().equals(codigo))
                .findFirst();
    }

    /**
     * Calcula el saldo total de todas las cuentas en el plan.
     *
     * @return El saldo total.
     */
    public BigDecimal calcularSaldoTotal() {
        return this.cuentas.stream()
                .map(this::calcularSaldoCuenta).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el saldo total de una cuenta, incluyendo sus subcuentas,
     * controles, etc.
     *
     * @param cuenta La cuenta cuyo saldo se desea calcular.
     * @return El saldo total de la cuenta.
     */
    public BigDecimal calcularSaldoCuenta(Cuenta cuenta) {
        BigDecimal saldo = cuenta.getSaldo();

        for (Cuenta hijo : cuenta.getSubCuentas()) {
            saldo = saldo.add(calcularSaldoCuenta(hijo));
        }

        return saldo;
    }

    /**
     * Calcula el saldo total de una subcuenta, incluyendo sus controles y
     * subcontroles.
     *
     * @param subCuenta La subcuenta cuyo saldo se desea calcular.
     * @return El saldo total de la subcuenta.
     */
    private BigDecimal calcularSaldoSubCuenta(Cuenta subCuenta) {
        BigDecimal saldo = BigDecimal.ZERO;

        // Sumar saldos de controles
        for (Cuenta control : subCuenta.getSubCuentas()) {
            saldo.add(calcularSaldoControl(control));
        }

        return saldo;
    }

    /**
     * Calcula el saldo total de un control, incluyendo sus subcontroles y
     * análisis.
     *
     * @param control El control cuyo saldo se desea calcular.
     * @return El saldo total del control.
     */
    private BigDecimal calcularSaldoControl(Cuenta control) {
        BigDecimal saldo = BigDecimal.ZERO;

        // Sumar saldos de subcontroles
        for (Cuenta subControl : control.getSubCuentas()) {
            saldo.add(calcularSaldoSubControl(subControl));
        }

        return saldo;
    }

    /**
     * Calcula el saldo total de un subcontrol, incluyendo sus análisis.
     *
     * @param subControl El subcontrol cuyo saldo se desea calcular.
     * @return El saldo total del subcontrol.
     */
    private BigDecimal calcularSaldoSubControl(Cuenta subControl) {
        BigDecimal saldo = BigDecimal.ZERO;

        // Sumar saldos de análisis
        for (Cuenta anal : subControl.getSubCuentas()) {
            saldo.add(anal.getSaldo());
        }

        return saldo;
    }

    public boolean tieneSaldoNegativo() {
        return this.cuentas.stream().anyMatch(Cuenta::tieneSaldoNegativo);
    }

    public BigDecimal obtenerSaldoNegativoTotal() {
        return this.cuentas.stream()
                .filter(Cuenta::tieneSaldoNegativo)
                .map(Cuenta::obtenerSaldoNegativoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addApertura(Cuenta cuentaRaiz) {
        if (cuentaRaiz == null || cuentaRaiz.getSubCuentas().isEmpty()) {
            throw new IllegalArgumentException("La cuenta raíz debe tener subcuentas.");
        }
        cuentaRaiz.setPlanDeCuenta(this);
        this.cuentas.add(cuentaRaiz);
    }

    public boolean contieneCuenta(Cuenta cuenta) {
        return this.cuentas.contains(cuenta);
    }

    public int getCantidadTotalCuentas() {
        return this.cuentas.stream()
                .mapToInt(c -> obtenerTodasLasCuentasHijas(c).size())
                .sum();
    }

    public List<Cuenta> getCuentasPorTipo(TipoCuenta tipo) {
        return this.cuentas.stream()
                .flatMap(c -> obtenerTodasLasCuentasHijas(c).stream())
                .filter(c -> c.getTipoCuenta() == tipo)
                .toList();
    }

    private List<Cuenta> obtenerTodasLasCuentasHijas(Cuenta cuenta) {
        List<Cuenta> todas = new ArrayList<>(List.of(cuenta));
        for (Cuenta hijo : cuenta.getSubCuentas()) {
            todas.addAll(obtenerTodasLasCuentasHijas(hijo));
        }
        return todas;
    }
}
