package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.core.Service;
import io.ebean.Database;
import jakarta.inject.Singleton;
import com.univsoftdev.econova.contabilidad.model.Moneda;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de monedas del sistema.
 * Incluye operaciones CRUD, conversión y manejo de moneda por defecto.
 */
@Slf4j
@Singleton
public class MonedaService extends Service<Moneda> {

    @Inject
    public MonedaService(Database database) {
        super(database, Moneda.class);
    }

    /**
     * Crea una nueva moneda con validaciones básicas
     */
    public Moneda crearMoneda(String symbol, String nombre, String pais, 
                            int fraccion, BigDecimal tasaCambio, boolean porDefecto) {
        validarSymbolUnico(symbol);
        validarDatosMoneda(nombre, pais, fraccion, tasaCambio);
        
        Moneda nuevaMoneda = new Moneda();
        nuevaMoneda.setSymbol(symbol);
        nuevaMoneda.setDisplayName(nombre);
        nuevaMoneda.setPais(pais);
        nuevaMoneda.setFraccion(fraccion);
        nuevaMoneda.setTasaCambio(tasaCambio);
        
        // Manejar moneda por defecto
        if (porDefecto) {
            desmarcarOtrasMonedasPorDefecto();
        }
        nuevaMoneda.setPorDefecto(porDefecto);
        
        database.save(nuevaMoneda);
        log.info("Nueva moneda creada: {} ({})", nombre, symbol);
        return nuevaMoneda;
    }

    /**
     * Actualiza los datos de una moneda existente
     */
    public Moneda actualizarMoneda(Long monedaId, String nombre, String pais, 
                                 int fraccion, BigDecimal tasaCambio, boolean porDefecto) {
        Moneda moneda = obtenerMonedaPorId(monedaId);
        validarDatosMoneda(nombre, pais, fraccion, tasaCambio);
        
        moneda.setDisplayName(nombre);
        moneda.setPais(pais);
        moneda.setFraccion(fraccion);
        moneda.setTasaCambio(tasaCambio);
        
        // Manejar moneda por defecto
        if (porDefecto && !moneda.isPorDefecto()) {
            desmarcarOtrasMonedasPorDefecto();
        }
        moneda.setPorDefecto(porDefecto);
        
        database.update(moneda);
        log.info("Moneda actualizada: {}", monedaId);
        return moneda;
    }

    /**
     * Obtiene una moneda por su símbolo (código)
     */
    public Optional<Moneda> obtenerMonedaPorSymbol(String symbol) {
        return database.createQuery(Moneda.class)
                .where()
                .eq("symbol", symbol)
                .findOneOrEmpty();
    }

    /**
     * Obtiene todas las monedas ordenadas por nombre
     */
    public List<Moneda> obtenerTodasLasMonedas() {
        return database.createQuery(Moneda.class)
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Obtiene la moneda por defecto del sistema
     */
    public Moneda obtenerMonedaPorDefecto() {
        return database.createQuery(Moneda.class)
                .where()
                .eq("porDefecto", true)
                .findOneOrEmpty()
                .orElseThrow(() -> new BusinessLogicException("No hay moneda por defecto configurada"));
    }

    /**
     * Establece una moneda como la por defecto
     */
    public Moneda establecerMonedaPorDefecto(Long monedaId) {
        Moneda moneda = obtenerMonedaPorId(monedaId);
        
        if (!moneda.isPorDefecto()) {
            desmarcarOtrasMonedasPorDefecto();
            moneda.setPorDefecto(true);
            database.update(moneda);
            log.info("Moneda establecida como por defecto: {}", moneda.getSymbol());
        }
        
        return moneda;
    }

    /**
     * Convierte un monto entre dos monedas
     */
    public BigDecimal convertirMoneda(BigDecimal monto, String monedaOrigen, String monedaDestino) {
        if (monedaOrigen.equals(monedaDestino)) {
            return monto;
        }
        
        Moneda origen = obtenerMonedaPorSymbol(monedaOrigen)
                .orElseThrow(() -> new BusinessLogicException("Moneda origen no encontrada: " + monedaOrigen));
        
        Moneda destino = obtenerMonedaPorSymbol(monedaDestino)
                .orElseThrow(() -> new BusinessLogicException("Moneda destino no encontrada: " + monedaDestino));
        
        // Convertir a moneda base primero (si es necesario)
        BigDecimal montoEnBase = monedaOrigen.equals(getMonedaBase().getSymbol()) 
                ? monto 
                : monto.divide(origen.getTasaCambio(), 6, RoundingMode.HALF_UP);
        
        // Convertir a moneda destino
        return monedaDestino.equals(getMonedaBase().getSymbol())
                ? montoEnBase
                : montoEnBase.multiply(destino.getTasaCambio());
    }

    /**
     * Obtiene la moneda base del sistema (la que tiene tasa de cambio = 1)
     */
    public Moneda getMonedaBase() {
        return database.createQuery(Moneda.class)
                .where()
                .eq("tazaCambio", BigDecimal.ONE)
                .findOneOrEmpty()
                .orElseThrow(() -> new BusinessLogicException("No hay moneda base configurada (tasa = 1)"));
    }

    /**
     * Valida que el símbolo de la moneda sea único
     */
    private void validarSymbolUnico(String symbol) {
        if (obtenerMonedaPorSymbol(symbol).isPresent()) {
            throw new BusinessLogicException("Ya existe una moneda con el símbolo: " + symbol);
        }
    }

    /**
     * Valida los datos básicos de la moneda
     */
    private void validarDatosMoneda(String nombre, String pais, int fraccion, BigDecimal tasaCambio) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessLogicException("El nombre de la moneda no puede estar vacío");
        }
        if (pais == null || pais.trim().isEmpty()) {
            throw new BusinessLogicException("El país de la moneda no puede estar vacío");
        }
        if (fraccion < 0) {
            throw new BusinessLogicException("La fracción de la moneda no puede ser negativa");
        }
        if (tasaCambio == null || tasaCambio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("La tasa de cambio debe ser un valor positivo");
        }
    }

    /**
     * Desmarca otras monedas como no por defecto
     */
    private void desmarcarOtrasMonedasPorDefecto() {
        database.createQuery(Moneda.class)
                .where()
                .eq("porDefecto", true)
                .asUpdate()
                .set("porDefecto", false)
                .update();
    }

    /**
     * Obtiene una moneda por ID con manejo de excepciones
     */
    public Moneda obtenerMonedaPorId(Long monedaId) {
        return database.find(Moneda.class, monedaId);
    }

    /**
     * Elimina una moneda (no se puede eliminar la moneda por defecto)
     */
    public void eliminarMoneda(Long monedaId) {
        Moneda moneda = obtenerMonedaPorId(monedaId);
        
        if (moneda.isPorDefecto()) {
            throw new BusinessLogicException("No se puede eliminar la moneda por defecto");
        }
        
        database.delete(moneda);
        log.info("Moneda eliminada: {}", monedaId);
    }

    /**
     * Actualiza las tasas de cambio basado en una moneda de referencia
     */
    public void actualizarTasasCambio(String monedaReferencia, BigDecimal nuevaTasaReferencia) {
        Moneda referencia = obtenerMonedaPorSymbol(monedaReferencia)
                .orElseThrow(() -> new BusinessLogicException("Moneda referencia no encontrada"));
        
        if (nuevaTasaReferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("La tasa de referencia debe ser positiva");
        }
        
        // Obtener todas las monedas excepto la de referencia
        List<Moneda> monedas = database.createQuery(Moneda.class)
                .where()
                .ne("symbol", monedaReferencia)
                .findList();
        
        // Actualizar tasas relativas a la nueva tasa de referencia
        for (Moneda moneda : monedas) {
            BigDecimal nuevaTasa = moneda.getTasaCambio()
                    .divide(referencia.getTasaCambio(), 6, RoundingMode.HALF_UP)
                    .multiply(nuevaTasaReferencia);
            moneda.setTasaCambio(nuevaTasa);
            database.update(moneda);
        }
        
        // Actualizar la tasa de referencia
        referencia.setTasaCambio(nuevaTasaReferencia);
        database.update(referencia);
        
        log.info("Tasas de cambio actualizadas respecto a {}", monedaReferencia);
    }
}