package com.univsoftdev.econova.contabilidad.views.comprobante.dto;  
  
import java.util.List;  
  
public record ValidationResult(boolean isValid, List<String> errores) {  
      
    public List<String> getErrores() {  
        return errores;  
    }  
      
    public boolean hasErrors() {  
        return !errores.isEmpty();  
    }  
      
    public int getErrorCount() {  
        return errores.size();  
    }  
}