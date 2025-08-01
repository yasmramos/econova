package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import com.univsoftdev.econova.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cont_libros_mayor")
public class LibroMayor extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    @OneToMany(mappedBy = "libroMayor", cascade = CascadeType.ALL)
    private List<Transaccion> transacciones = new ArrayList<>();

    public LibroMayor() {
    }

    public LibroMayor(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula.");
        }
        this.cuenta = cuenta;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones != null ? transacciones : new ArrayList<>();
    }

    // --- Métodos de negocio ---
    /**
     * Calcula el saldo actual del libro mayor basándose en sus transacciones.
     * Débito incrementa el saldo; Crédito lo reduce.
     *
     * @return
     */
    public BigDecimal calcularSaldo() {
        return transacciones.stream()
                .filter(t -> t.getMonto() != null && t.getTipo() != null)
                .map(t -> {
                    return switch (t.getTipo()) {
                        case DEBITO ->
                            t.getMonto();
                        case CREDITO ->
                            t.getMonto().negate();
                        default ->
                            BigDecimal.ZERO;
                    };
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalDebitos() {
        return transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.DEBITO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCreditos() {
        return transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.CREDITO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCantidadTransacciones() {
        return transacciones.size();
    }

    public boolean tieneTransacciones() {
        return transacciones != null && !transacciones.isEmpty();
    }

    public boolean estaBalanceado() {
        return BigDecimal.ZERO.compareTo(calcularSaldo()) == 0;
    }

    /**
     * Agrega una transacción al libro mayor y establece la relación inversa.
     *
     * @param transaccion
     */
    public void agregarTransaccion(Transaccion transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }

        transaccion.setLibroMayor(this);
        this.transacciones.add(transaccion);
    }

    /**
     * Elimina una transacción del libro mayor y rompe la relación inversa.
     *
     * @param transaccion
     */
    public void eliminarTransaccion(Transaccion transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }

        transaccion.setLibroMayor(null);
        this.transacciones.remove(transaccion);
    }

}
