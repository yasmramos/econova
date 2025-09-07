package com.univsoftdev.econova.contabilidad.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.univsoftdev.econova.contabilidad.EstadoCuenta;
import com.univsoftdev.econova.contabilidad.NaturalezaCuenta;
import com.univsoftdev.econova.contabilidad.finder.CuentaFinder;
import com.univsoftdev.econova.core.model.BaseModel;
import com.univsoftdev.econova.contabilidad.TipoCuenta;
import com.univsoftdev.econova.contabilidad.TipoApertura;
import com.univsoftdev.econova.contabilidad.AnalisisTipoApertura;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cont_cuentas")
public class Cuenta extends BaseModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static CuentaFinder finder = new CuentaFinder();

    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 3)
    private String codigo; // Código único de la cuenta

    @Column(nullable = false)
    private String nombre; // Nombre de la cuenta

    @Enumerated(EnumType.STRING)
    private EstadoCuenta estadoCuenta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoApertura tipoApertura; // Tipo de apertura (SIN APERTURA, SUBCUENTA, etc.)

    @Enumerated(EnumType.STRING)
    private AnalisisTipoApertura tipoAnalisisApertura; // Tipo de apertura (SIN APERTURA, SUBCUENTA, etc.)

    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta; // Tipo de cuenta (INGRESO, GASTO, ACTIVO, PASIVO, PATRIMONIO)

    @ManyToOne
    private Cuenta cuentaPadre; // Relación jerárquica: cuenta padre

    @OneToMany(mappedBy = "cuentaPadre", cascade = CascadeType.ALL)
    private List<Cuenta> subCuentas = new ArrayList<>(); // Lista de subCuentas

    @OneToOne
    @JoinColumn(name = "moneda_id")
    private Moneda moneda;
    
    private BigDecimal saldo = BigDecimal.ZERO;

    private boolean apertura = false;

    private boolean activa = false;

    @ManyToOne
    @JoinColumn(name = "libro_mayor_id")
    private LibroMayor libroMayor;

    @ManyToOne
    @JoinColumn(name = "plan_de_cuenta_id")
    private PlanDeCuentas planDeCuenta;

    @Enumerated(EnumType.STRING)
    private NaturalezaCuenta naturaleza;

    public Cuenta() {
    }

    public Cuenta(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Cuenta(String codigo, String nombre, BigDecimal saldo, Moneda moneda) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.moneda = moneda;
        this.saldo = saldo;
    }

    public Cuenta(String codigo, String nombre, NaturalezaCuenta naturaleza, TipoCuenta tipoCuenta, Moneda moneda) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.moneda = moneda;
        this.tipoCuenta = tipoCuenta;
        this.naturaleza = naturaleza;
    }

    public void addSubCuentas(List<Cuenta> nuevas) {
        for (Cuenta c : nuevas) {
            c.setCuentaPadre(this);
            c.setEstadoCuenta(EstadoCuenta.ACTIVA);
            subCuentas.add(c);
        }
    }

    public boolean esHija() {
        return subCuentas.isEmpty();
    }

    public boolean esActivo() {
        return tipoCuenta == TipoCuenta.ACTIVO;
    }

    public boolean esGasto() {
        return tipoCuenta == TipoCuenta.GASTO;
    }

    public boolean esIngreso() {
        return tipoCuenta == TipoCuenta.INGRESO;
    }

    public boolean esBalanceable() {
        return esActivo() || tipoCuenta == TipoCuenta.PASIVO || tipoCuenta == TipoCuenta.PATRIMONIO;
    }

    public boolean tieneSaldoNegativo() {
        return this.subCuentas.stream().anyMatch(Cuenta::tieneSaldoNegativo);
    }

    public BigDecimal obtenerSaldoNegativoTotal() {
        return this.subCuentas.stream()
                .filter(Cuenta::tieneSaldoNegativo)
                .map(Cuenta::obtenerSaldoNegativoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addSubCuenta(Cuenta subCuenta) {
        subCuenta.setCuentaPadre(this);
        subCuenta.setEstadoCuenta(EstadoCuenta.ACTIVA);
        subCuentas.add(subCuenta);
    }

    public LibroMayor getLibroMayor() {
        return libroMayor;
    }

    public void setLibroMayor(LibroMayor libroMayor) {
        this.libroMayor = libroMayor;
    }

    public boolean esActivoOPasivo() {
        return tipoCuenta == TipoCuenta.ACTIVO || tipoCuenta == TipoCuenta.PASIVO;
    }

    public boolean requiereSubcuentas() {
        return tipoApertura == TipoApertura.SUBCUENTA;
    }

    public static CuentaFinder getFinder() {
        return finder;
    }

    public AnalisisTipoApertura getTipoAnalisisApertura() {
        return tipoAnalisisApertura;
    }

    public void setTipoAnalisisApertura(AnalisisTipoApertura tipoAnalisisApertura) {
        this.tipoAnalisisApertura = tipoAnalisisApertura;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public EstadoCuenta getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(EstadoCuenta estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public boolean isApertura() {
        return apertura;
    }

    public void setApertura(boolean apertura) {
        this.apertura = apertura;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public NaturalezaCuenta getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(NaturalezaCuenta naturaleza) {
        this.naturaleza = naturaleza;
    }

    public PlanDeCuentas getPlanDeCuenta() {
        return planDeCuenta;
    }

    public void setPlanDeCuenta(PlanDeCuentas planDeCuenta) {
        this.planDeCuenta = planDeCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCodigoSinCuentaPadre() {
        String[] partes = codigo.split("\\.");
        return partes.length > 0 ? partes[partes.length - 1] : codigo;
    }

    public void setCodigo(String codigo) {
        if (cuentaPadre == null) {
            this.codigo = codigo;
        } else {
            this.codigo = cuentaPadre.getCodigo() + "." + codigo;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoApertura getTipoApertura() {
        return tipoApertura;
    }

    public void setTipoApertura(TipoApertura tipoApertura) {
        this.tipoApertura = tipoApertura;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public Cuenta getCuentaPadre() {
        return cuentaPadre;
    }

    public void setCuentaPadre(Cuenta cuentaPadre) {
        this.cuentaPadre = cuentaPadre;
    }

    public List<Cuenta> getSubCuentas() {
        return subCuentas;
    }

    public void setSubCuentas(List<Cuenta> subCuentas) {
        this.subCuentas = subCuentas;
    }

    @Override
    public String toString() {
        if (cuentaPadre != null) {
            return getCodigoSinCuentaPadre() + " - " + nombre;
        }
        return codigo + " - " + nombre;
    }

}
