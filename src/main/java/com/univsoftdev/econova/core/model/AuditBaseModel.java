package com.univsoftdev.econova.core.model;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.model.Unit;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import io.ebean.annotation.WhoCreated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MappedSuperclass
public abstract class AuditBaseModel extends BaseModel {

    @NotNull(message = "El usuario es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @WhoCreated
    protected User user;

    @NotNull(message = "La unidad organizativa es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    protected Unit unit;

    @NotNull(message = "El período contable es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false)
    protected Period period;

    @NotNull(message = "El ejercicio contable es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    protected Exercise exercise;

    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    protected List<Transaction> transactions = new ArrayList<>();

    public User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        this.user = Objects.requireNonNull(user, "El usuario no puede ser nulo");
    }

    public Unit getUnidad() {
        return unit;
    }

    public void setUnidad(@NotNull Unit unidad) {
        this.unit = Objects.requireNonNull(unidad, "La unidad no puede ser nula");
        // Auto-set tenantId desde la unit para multitenancy
        if (unidad.getTenantId() != null) {
            this.setTenantId(unidad.getTenantId());
        }
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(@NotNull Period period) {
        this.period = Objects.requireNonNull(period, "El periodo no puede ser nulo");
        // Auto-set exercise desde el period
        if (period.getExercise() != null && this.exercise == null) {
            this.exercise = period.getExercise();
        }
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(@NotNull Exercise exercise) {
        this.exercise = Objects.requireNonNull(exercise, "El ejercicio no puede ser nulo");
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Método de conveniencia para validar la consistencia multitenant
     *
     * @return
     */
    public boolean isTenantConsistent() {
        return unit != null
                && unit.getTenantId() != null
                && unit.getTenantId().equals(this.getTenantId());
    }

    /**
     * Método de conveniencia para validar consistencia temporal
     *
     * @return
     */
    public boolean isTemporalConsistent() {
        return period != null
                && exercise != null
                && period.getExercise() != null
                && period.getExercise().getId().equals(exercise.getId());
    }
}
