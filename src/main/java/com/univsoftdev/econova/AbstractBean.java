package com.univsoftdev.econova;

import java.beans.*;
import java.io.Serializable;

/**
 * Clase base abstracta para objetos JavaBean que soportan notificaciones de
 * cambio de propiedades y cambios vetados mediante PropertyChangeSupport y
 * VetoableChangeSupport.
 */
public abstract class AbstractBean implements Serializable {

    private static final long serialVersionUID = 1L;

    protected transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    protected transient VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);

    /**
     * Método genérico para cambiar una propiedad con manejo de veto y
     * notificación.
     *
     * @param <T>
     * @param propertyName Nombre de la propiedad.
     * @param oldValue Valor anterior.
     * @param newValue Nuevo valor.
     * @throws PropertyVetoException Si algún listener vetó el cambio.
     */
    protected <T> void changeProperty(String propertyName, T oldValue, T newValue) throws PropertyVetoException {
        if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
            vetoableChangeSupport.fireVetoableChange(propertyName, oldValue, newValue);
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    // Sobrecargas para tipos primitivos (mejoran rendimiento evitando autoboxing)
    protected void changeProperty(String propertyName, int oldValue, int newValue) throws PropertyVetoException {
        if (oldValue != newValue) {
            vetoableChangeSupport.fireVetoableChange(propertyName, oldValue, newValue);
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void changeProperty(String propertyName, boolean oldValue, boolean newValue) throws PropertyVetoException {
        if (oldValue != newValue) {
            vetoableChangeSupport.fireVetoableChange(propertyName, oldValue, newValue);
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void changeProperty(String propertyName, double oldValue, double newValue) throws PropertyVetoException {
        if (Double.doubleToLongBits(oldValue) != Double.doubleToLongBits(newValue)) {
            vetoableChangeSupport.fireVetoableChange(propertyName, oldValue, newValue);
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    // Métodos para escuchar cambios en listas u otros elementos indexados
    protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    protected void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    protected void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
        propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    // Soporte para múltiples propiedades
    protected void firePropertyChange(PropertyChangeEvent event) {
        propertyChangeSupport.firePropertyChange(event);
    }

    // Listeners
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.addVetoableChangeListener(listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.removeVetoableChangeListener(listener);
    }

    // Para evitar errores de serialización
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        // No escribir los supports si son transientes
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        propertyChangeSupport = new PropertyChangeSupport(this);
        vetoableChangeSupport = new VetoableChangeSupport(this);
    }
}
