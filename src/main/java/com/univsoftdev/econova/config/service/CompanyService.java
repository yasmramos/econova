package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.config.model.Empresa;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.core.Service;
import io.ebean.Database;
import jakarta.inject.Singleton;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

/**
 * Servicio avanzado para gestión de empresas. Incluye operaciones CRUD,
 * validaciones y gestión de unidades organizativas.
 */
@Slf4j
@Singleton
public class EmpresaService extends Service<Empresa> {

    private final UnidadService unidadService;

    @Inject
    public EmpresaService(Database database, UnidadService unidadService) {
        super(database, Empresa.class);
        this.unidadService = unidadService;
    }

    /**
     * Crea una nueva empresa con validaciones básicas
     * @param nombre
     * @param razonSocial
     * @param nif
     * @param direccion
     * @param telefono
     * @param email
     * @param codigoPostal
     * @param ciudad
     * @param provincia
     * @param pais
     * @param cif
     * @return 
     */
    public Empresa crearEmpresa(
            @NotBlank String nombre,
            @NotBlank String razonSocial,
            @NotBlank String nif,
            String direccion,
            String telefono,
            @Email String email,
            String codigoPostal,
            String ciudad,
            String provincia,
            String pais,
            String cif) {

        validarNifUnico(nif);
        if (cif != null && !cif.isEmpty()) {
            validarCifUnico(cif);
        }
        validarEmail(email);

        Empresa nuevaEmpresa = new Empresa(nombre, razonSocial, nif);
        nuevaEmpresa.setAddress(direccion);
        nuevaEmpresa.setTelefono(telefono);
        nuevaEmpresa.setEmail(email);
        nuevaEmpresa.setCodigoPostal(codigoPostal);
        nuevaEmpresa.setCiudad(ciudad);
        nuevaEmpresa.setProvincia(provincia);
        nuevaEmpresa.setPais(pais);
        nuevaEmpresa.setCif(cif);

        database.save(nuevaEmpresa);
        log.info("Nueva empresa creada: {} - {}", nombre, nif);
        return nuevaEmpresa;
    }

    /**
     * Actualiza los datos básicos de una empresa
     * @param empresaId
     * @param nombre
     * @param email
     * @param direccion
     * @param razonSocial
     * @param telefono
     * @param provincia
     * @param pais
     * @param codigoPostal
     * @param ciudad
     * @return 
     */
    public Empresa actualizarEmpresa(
            Long empresaId,
            String nombre,
            String razonSocial,
            String direccion,
            String telefono,
            String email,
            String codigoPostal,
            String ciudad,
            String provincia,
            String pais) {

        Empresa empresa = obtenerEmpresaPorId(empresaId);

        if (nombre != null) {
            empresa.setName(nombre);
        }
        if (razonSocial != null) {
            empresa.setRazonSocial(razonSocial);
        }
        if (direccion != null) {
            empresa.setAddress(direccion);
        }
        if (telefono != null) {
            empresa.setTelefono(telefono);
        }
        if (email != null) {
            validarEmail(email);
            empresa.setEmail(email);
        }
        if (codigoPostal != null) {
            empresa.setCodigoPostal(codigoPostal);
        }
        if (ciudad != null) {
            empresa.setCiudad(ciudad);
        }
        if (provincia != null) {
            empresa.setProvincia(provincia);
        }
        if (pais != null) {
            empresa.setPais(pais);
        }

        database.update(empresa);
        log.info("Empresa actualizada: {}", empresaId);
        return empresa;
    }

    /**
     * Obtiene una empresa por su NIF
     * @param nif
     * @return 
     */
    public Optional<Empresa> obtenerEmpresaPorNif(String nif) {
        return database.createQuery(Empresa.class)
                .where()
                .eq("nif", nif)
                .findOneOrEmpty();
    }

    /**
     * Obtiene una empresa por su CIF
     * @param cif
     * @return 
     */
    public Optional<Empresa> obtenerEmpresaPorCif(String cif) {
        return database.createQuery(Empresa.class)
                .where()
                .eq("cif", cif)
                .findOneOrEmpty();
    }
    
    public Optional<Empresa> findByCode(String code) {
        return database.createQuery(Empresa.class)
                .where()
                .eq("codigo", code)
                .findOneOrEmpty();
    }

    /**
     * Obtiene todas las empresas ordenadas por nombre
     * @return 
     */
    public List<Empresa> obtenerTodasLasEmpresas() {
        return database.createQuery(Empresa.class)
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Crea una nueva unidad organizativa asociada a la empresa
     * @param empresaId
     * @param codigoUnidad
     * @param nombreUnidad
     * @param direccionUnidad
     * @param correoUnidad
     * @param nae
     * @param dpa
     * @param reup
     * @return 
     */
    public Unidad crearUnidadParaEmpresa(
            Long empresaId,
            String codigoUnidad,
            String nombreUnidad,
            String direccionUnidad,
            String correoUnidad,
            String nae,
            String dpa,
            String reup) {

        Empresa empresa = obtenerEmpresaPorId(empresaId);
        return unidadService.crearUnidad(
                codigoUnidad,
                nombreUnidad,
                direccionUnidad,
                correoUnidad,
                nae,
                dpa,
                reup,
                empresa);
    }

    /**
     * Obtiene todas las unidades organizativas de una empresa
     * @param empresaId
     * @return 
     */
    public List<Unidad> obtenerUnidadesDeEmpresa(Long empresaId) {
        return database.createQuery(Unidad.class)
                .where()
                .eq("empresa.id", empresaId)
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Valida que el NIF sea único
     */
    private void validarNifUnico(String nif) {
        if (obtenerEmpresaPorNif(nif).isPresent()) {
            throw new BusinessLogicException("Ya existe una empresa con el NIF: " + nif);
        }
    }

    /**
     * Valida que el CIF sea único (si está presente)
     */
    private void validarCifUnico(String cif) {
        if (obtenerEmpresaPorCif(cif).isPresent()) {
            throw new BusinessLogicException("Ya existe una empresa con el CIF: " + cif);
        }
    }

    /**
     * Valida el formato del email (si está presente)
     */
    private void validarEmail(String email) {
        if (email != null && !email.isEmpty() && !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BusinessLogicException("El formato del email no es válido");
        }
    }

    /**
     * Obtiene una empresa por ID con manejo de excepciones
     * @param empresaId
     * @return 
     */
    public Empresa obtenerEmpresaPorId(Long empresaId) {
        return database.find(Empresa.class, empresaId);
    }

    /**
     * Busca empresas por nombre (búsqueda parcial case-insensitive)
     * @param nombre
     * @return 
     */
    public List<Empresa> buscarEmpresasPorNombre(String nombre) {
        return database.createQuery(Empresa.class)
                .where()
                .ilike("nombre", "%" + nombre + "%")
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Elimina una empresa (solo si no tiene unidades asociadas)
     * @param empresaId
     */
    public void eliminarEmpresa(Long empresaId) {
        Empresa empresa = obtenerEmpresaPorId(empresaId);

        if (!empresa.getUnidades().isEmpty()) {
            throw new BusinessLogicException("No se puede eliminar una empresa con unidades organizativas asociadas");
        }

        database.delete(empresa);
        log.info("Empresa eliminada: {}", empresaId);
    }

    /**
     * Obtiene estadísticas básicas de la empresa
     * @param empresaId
     * @return 
     */
    public EstadisticasEmpresa obtenerEstadisticas(Long empresaId) {
        Empresa empresa = obtenerEmpresaPorId(empresaId);
        int cantidadUnidades = empresa.getUnidades().size();

        return new EstadisticasEmpresa(
                empresa.getName(),
                cantidadUnidades
        );
    }
    
    /**
     * Record para representar estadísticas de la empresa
     */
    public record EstadisticasEmpresa(String nombreEmpresa, int cantidadUnidades) {

    }
}
