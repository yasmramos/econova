package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Language;
import com.univsoftdev.econova.config.repository.LanguageRepository;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import com.univsoftdev.econova.core.service.BaseService;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestión de idiomas del sistema. Incluye operaciones CRUD y
 * búsquedas específicas.
 */
@Slf4j
@Singleton
public class LanguageService extends BaseService<Language, LanguageRepository> {

    @Inject
    public LanguageService(LanguageRepository repository) {
        super(repository);
    }

    /**
     * Crea un nuevo idioma con validaciones básicas
     */
    @Transactional
    public Language crearIdioma(String symbol, String nombre, String pais) {
        validarParametrosCreacion(symbol, nombre, pais);
        validarSymbolUnico(symbol);

        Language nuevoIdioma = new Language();
        nuevoIdioma.setSymbol(symbol);
        nuevoIdioma.setNombre(nombre);
        nuevoIdioma.setPais(pais);

        save(nuevoIdioma); // Usar método de la clase base
        log.info("Nuevo idioma creado: {} ({})", nombre, symbol);
        return nuevoIdioma;
    }

    /**
     * Actualiza los datos de un idioma existente
     */
    @Transactional
    public Language actualizarIdioma(Long idiomaId, String nombre, String pais) {
        if (idiomaId == null) {
            throw new BusinessLogicException("El ID del idioma no puede ser nulo");
        }
        
        Language idioma = obtenerIdiomaPorId(idiomaId);
        validarDatosIdioma(nombre, pais);

        idioma.setNombre(nombre);
        idioma.setPais(pais);

        update(idioma); // Usar método de la clase base
        log.info("Idioma actualizado: {}", idiomaId);
        return idioma;
    }

    /**
     * Obtiene un idioma por su símbolo (código)
     */
    @Transactional
    public Optional<Language> obtenerIdiomaPorSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return repository.createQuery(Language.class)
                .where()
                .eq("symbol", symbol)
                .eq("deleted", false) // Agregar filtro de eliminación lógica
                .findOneOrEmpty();
    }

    /**
     * Obtiene todos los idiomas ordenados por nombre
     */
    @Transactional
    public List<Language> obtenerTodosLosIdiomas() {
        return repository.createQuery(Language.class)
                .where()
                .eq("deleted", false)
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Busca idiomas por nombre (búsqueda parcial case-insensitive)
     */
    @Transactional
    public List<Language> buscarIdiomasPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return List.of();
        }
        
        return repository.createQuery(Language.class)
                .where()
                .eq("deleted", false)
                .ilike("nombre", "%" + nombre + "%")
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Obtiene idiomas por país
     */
    @Transactional
    public List<Language> obtenerIdiomasPorPais(String pais) {
        if (pais == null || pais.trim().isEmpty()) {
            return List.of();
        }
        
        return repository.createQuery(Language.class)
                .where()
                .eq("deleted", false)
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
     * Valida parámetros para creación de idioma
     */
    private void validarParametrosCreacion(String symbol, String nombre, String pais) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new BusinessLogicException("El símbolo del idioma no puede estar vacío");
        }
        validarDatosIdioma(nombre, pais);
    }

    /**
     * Obtiene un idioma por ID con manejo de excepciones
     */
    @Transactional
    public Language obtenerIdiomaPorId(Long idiomaId) {
        if (idiomaId == null) {
            throw new BusinessLogicException("El ID del idioma no puede ser nulo");
        }
        
        try {
            Language idioma = repository.find(Language.class, idiomaId);
            if (idioma == null || (idioma instanceof com.univsoftdev.econova.core.model.BaseModel baseModel && baseModel.isDeleted())) {
                throw new EntityNotFoundException("Idioma no encontrado con ID: " + idiomaId);
            }
            return idioma;
        } catch (EntityNotFoundException e) {
            log.error("Error obteniendo idioma por ID: {}", idiomaId, e);
            throw new EntityNotFoundException("Error obteniendo idioma con ID: " + idiomaId);
        }
    }

    /**
     * Elimina un idioma
     */
    @Transactional
    public void eliminarIdioma(Long idiomaId) {
        if (idiomaId == null) {
            throw new BusinessLogicException("El ID del idioma no puede ser nulo");
        }
        
        Language idioma = obtenerIdiomaPorId(idiomaId);
        delete(idioma); // Usar método de la clase base
        log.info("Idioma eliminado: {}", idiomaId);
    }

    /**
     * Obtiene el idioma por defecto del sistema
     */
    @Transactional
    public Optional<Language> obtenerIdiomaPorDefecto() {
        // Podría configurarse en propiedades del sistema
        return obtenerIdiomaPorSymbol("es");
    }

    /**
     * Verifica si un idioma existe por su símbolo
     */
    @Transactional
    public boolean existeIdioma(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return false;
        }
        
        return repository.createQuery(Language.class)
                .where()
                .eq("symbol", symbol)
                .eq("deleted", false)
                .exists();
    }

    /**
     * Cuenta el total de idiomas disponibles
     */
    @Transactional
    public long contarIdiomas() {
        try {
            return repository.createQuery(Language.class)
                    .where()
                    .eq("deleted", false)
                    .findCount();
        } catch (Exception e) {
            log.error("Error contando idiomas", e);
            return 0;
        }
    }

    /**
     * Obtiene todos los países únicos que tienen idiomas registrados
     */
    @Transactional
    public List<String> obtenerPaisesConIdiomas() {
        return repository.createQuery(Language.class)
                .select("pais")
                .where()
                .eq("deleted", false)
                .setDistinct(true)
                .orderBy("pais asc")
                .findSingleAttributeList();
    }
}