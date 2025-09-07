package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.config.model.Idioma;
import com.univsoftdev.econova.core.Service;
import io.ebean.Database;
import jakarta.inject.Singleton;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de idiomas del sistema. Incluye operaciones CRUD y
 * búsquedas específicas.
 */
@Slf4j
@Singleton
public class IdiomaService extends Service<Idioma> {

    @Inject
    public IdiomaService(Database database) {
        super(database, Idioma.class);
    }

    /**
     * Crea un nuevo idioma con validaciones básicas
     */
    public Idioma crearIdioma(String symbol, String nombre, String pais) {
        validarSymbolUnico(symbol);
        validarDatosIdioma(nombre, pais);

        Idioma nuevoIdioma = new Idioma();
        nuevoIdioma.setSymbol(symbol);
        nuevoIdioma.setNombre(nombre);
        nuevoIdioma.setPais(pais);

        database.save(nuevoIdioma);
        log.info("Nuevo idioma creado: {} ({})", nombre, symbol);
        return nuevoIdioma;
    }

    /**
     * Actualiza los datos de un idioma existente
     */
    public Idioma actualizarIdioma(Long idiomaId, String nombre, String pais) {
        Idioma idioma = obtenerIdiomaPorId(idiomaId);
        validarDatosIdioma(nombre, pais);

        idioma.setNombre(nombre);
        idioma.setPais(pais);

        database.update(idioma);
        log.info("Idioma actualizado: {}", idiomaId);
        return idioma;
    }

    /**
     * Obtiene un idioma por su símbolo (código)
     */
    public Optional<Idioma> obtenerIdiomaPorSymbol(String symbol) {
        return database.createQuery(Idioma.class)
                .where()
                .eq("symbol", symbol)
                .findOneOrEmpty();
    }

    /**
     * Obtiene todos los idiomas ordenados por nombre
     */
    public List<Idioma> obtenerTodosLosIdiomas() {
        return database.createQuery(Idioma.class)
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Busca idiomas por nombre (búsqueda parcial case-insensitive)
     */
    public List<Idioma> buscarIdiomasPorNombre(String nombre) {
        return database.createQuery(Idioma.class)
                .where()
                .ilike("nombre", "%" + nombre + "%")
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Obtiene idiomas por país
     */
    public List<Idioma> obtenerIdiomasPorPais(String pais) {
        return database.createQuery(Idioma.class)
                .where()
                .eq("pais", pais)
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Valida que el símbolo del idioma sea único
     */
    private void validarSymbolUnico(String symbol) {
        if (obtenerIdiomaPorSymbol(symbol).isPresent()) {
            throw new BusinessLogicException("Ya existe un idioma con el símbolo: " + symbol);
        }
    }

    /**
     * Valida los datos básicos del idioma
     */
    private void validarDatosIdioma(String nombre, String pais) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessLogicException("El nombre del idioma no puede estar vacío");
        }
        if (pais == null || pais.trim().isEmpty()) {
            throw new BusinessLogicException("El país del idioma no puede estar vacío");
        }
    }

    /**
     * Obtiene un idioma por ID con manejo de excepciones
     */
    public Idioma obtenerIdiomaPorId(Long idiomaId) {
        return database.find(Idioma.class, idiomaId);
    }

    /**
     * Elimina un idioma
     */
    public void eliminarIdioma(Long idiomaId) {
        Idioma idioma = obtenerIdiomaPorId(idiomaId);
        database.delete(idioma);
        log.info("Idioma eliminado: {}", idiomaId);
    }

    /**
     * Obtiene el idioma por defecto del sistema
     */
    public Optional<Idioma> obtenerIdiomaPorDefecto() {
        // Podría configurarse en propiedades del sistema
        return obtenerIdiomaPorSymbol("es");
    }

    /**
     * Verifica si un idioma existe por su símbolo
     */
    public boolean existeIdioma(String symbol) {
        return database.createQuery(Idioma.class)
                .where()
                .eq("symbol", symbol)
                .exists();
    }
}
