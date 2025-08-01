package com.univsoftdev.econova.core.module;

import com.univsoftdev.econova.AppContext;
import java.util.*;  
import java.util.concurrent.ConcurrentHashMap;  
import lombok.extern.slf4j.Slf4j;
  
@Slf4j
public class ModuleDependencyManager {  
      
    private final Map<String, Module> registeredModules = new ConcurrentHashMap<>();  
    private final Map<String, ModuleState> moduleStates = new ConcurrentHashMap<>();  
    private final AppContext appContext;  
      
    public ModuleDependencyManager(AppContext appContext) {  
        this.appContext = appContext;  
    }  
      
    /**  
     * Registra un módulo sin inicializarlo  
     */  
    public void registerModule(Module module) {  
        String moduleId = module.getModuleId();  
        if (registeredModules.containsKey(moduleId)) {  
            throw new IllegalArgumentException("Módulo ya registrado: " + moduleId);  
        }  
          
        registeredModules.put(moduleId, module);  
        moduleStates.put(moduleId, ModuleState.REGISTERED);  
        log.info("Módulo registrado: {} v{}", module.getModuleName(), module.getVersion());  
    }  
      
    /**  
     * Inicializa todos los módulos respetando dependencias  
     */  
    public void initializeAllModules() throws ModuleInitializationException {  
        List<Module> sortedModules = topologicalSort();  
          
        for (Module module : sortedModules) {  
            initializeModule(module);  
        }  
    }  
      
    /**  
     * Inicializa un módulo específico y sus dependencias  
     */  
    public void initializeModule(String moduleId) throws ModuleInitializationException {  
        Module module = registeredModules.get(moduleId);  
        if (module == null) {  
            throw new ModuleInitializationException("Módulo no encontrado: " + moduleId);  
        }  
        initializeModule(module);  
    }  
      
    private void initializeModule(Module module) throws ModuleInitializationException {  
        String moduleId = module.getModuleId();  
        ModuleState currentState = moduleStates.get(moduleId);  
          
        if (currentState == ModuleState.INITIALIZED) {  
            return; // Ya inicializado  
        }  
          
        if (currentState == ModuleState.INITIALIZING) {  
            throw new ModuleInitializationException("Dependencia circular detectada: " + moduleId);  
        }  
          
        moduleStates.put(moduleId, ModuleState.INITIALIZING);  
          
        try {  
            // Verificar e inicializar dependencias requeridas  
            for (String depId : module.getDependencies()) {  
                Module dependency = registeredModules.get(depId);  
                if (dependency == null) {  
                    throw new ModuleInitializationException(  
                        String.format("Dependencia requerida no encontrada: %s para módulo %s", depId, moduleId)  
                    );  
                }  
                initializeModule(dependency);  
            }  
              
            // Inicializar dependencias opcionales si están disponibles  
            for (String optDepId : module.getOptionalDependencies()) {  
                Module optDependency = registeredModules.get(optDepId);  
                if (optDependency != null) {  
                    try {  
                        initializeModule(optDependency);  
                    } catch (Exception e) {  
                        log.warn("Fallo al inicializar dependencia opcional {}: {}", optDepId, e.getMessage());  
                    }  
                }  
            }  
              
            // Inicializar el módulo  
            module.initialize(appContext);  
            moduleStates.put(moduleId, ModuleState.INITIALIZED);  
              
            log.info("Módulo inicializado exitosamente: {}", module.getModuleName());  
              
        } catch (Exception e) {  
            moduleStates.put(moduleId, ModuleState.FAILED);  
            throw new ModuleInitializationException(  
                String.format("Error inicializando módulo %s: %s", moduleId, e.getMessage()), e  
            );  
        }  
    }  
      
    /**  
     * Ordena los módulos topológicamente basado en dependencias  
     */  
    private List<Module> topologicalSort() {  
        List<Module> result = new ArrayList<>();  
        Set<String> visited = new HashSet<>();  
        Set<String> visiting = new HashSet<>();  
          
        for (Module module : registeredModules.values()) {  
            if (!visited.contains(module.getModuleId())) {  
                topologicalSortUtil(module, visited, visiting, result);  
            }  
        }  
          
        // Ordenar por prioridad dentro del orden topológico  
        result.sort(Comparator.comparingInt(Module::getInitializationPriority));  
          
        return result;  
    }  
      
    private void topologicalSortUtil(Module module, Set<String> visited, Set<String> visiting, List<Module> result) {  
        String moduleId = module.getModuleId();  
          
        if (visiting.contains(moduleId)) {  
            throw new IllegalStateException("Dependencia circular detectada en: " + moduleId);  
        }  
          
        if (visited.contains(moduleId)) {  
            return;  
        }  
          
        visiting.add(moduleId);  
          
        // Visitar dependencias primero  
        for (String depId : module.getDependencies()) {  
            Module dependency = registeredModules.get(depId);  
            if (dependency != null) {  
                topologicalSortUtil(dependency, visited, visiting, result);  
            }  
        }  
          
        visiting.remove(moduleId);  
        visited.add(moduleId);  
        result.add(module);  
    }  
      
    /**  
     * Cierra todos los módulos en orden inverso  
     */  
    public void shutdownAllModules() {  
        List<Module> modules = new ArrayList<>(registeredModules.values());  
        Collections.reverse(modules); // Cerrar en orden inverso  
          
        for (Module module : modules) {  
            try {  
                if (moduleStates.get(module.getModuleId()) == ModuleState.INITIALIZED) {  
                    module.shutdown();  
                    moduleStates.put(module.getModuleId(), ModuleState.SHUTDOWN);  
                }  
            } catch (Exception e) {  
                log.error("Error cerrando módulo {}: {}", module.getModuleName(), e.getMessage(), e);  
            }  
        }  
    }  
      
    public Map<String, ModuleState> getModuleStates() {  
        return new HashMap<>(moduleStates);  
    }  
}