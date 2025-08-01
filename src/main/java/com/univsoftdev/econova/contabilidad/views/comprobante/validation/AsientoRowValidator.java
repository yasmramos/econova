package com.univsoftdev.econova.contabilidad.views.comprobante.validation;  
  
import com.univsoftdev.econova.contabilidad.views.comprobante.dto.AsientoRowData;
import com.univsoftdev.econova.contabilidad.views.comprobante.dto.ValidationResult;
import jakarta.inject.Singleton;
import java.math.BigDecimal;  
import java.util.ArrayList;  
import java.util.List;  
  
@Singleton
public class AsientoRowValidator {  
      
    private final List<String> errores = new ArrayList<>();  
      
    public ValidationResult validate(AsientoRowData rowData, int filaIndex) {  
        errores.clear();  
          
        validateCuentaRequerida(rowData.getCta(), filaIndex);  
        validateTransaccionData(rowData.getDebito(), rowData.getCredito(), filaIndex);  
          
        return new ValidationResult(errores.isEmpty(), new ArrayList<>(errores));  
    }  
      
    private void validateCuentaRequerida(String cuenta, int filaIndex) {  
        if (cuenta == null || cuenta.trim().isEmpty()) {  
            errores.add(String.format("Fila %d: Código de cuenta es requerido", filaIndex + 1));  
        }  
    }  
      
    private void validateTransaccionData(Object debito, Object credito, int filaIndex) {  
        boolean hasDebito = debito != null && !debito.toString().replace("$", "").trim().isEmpty();  
        boolean hasCredito = credito != null && !credito.toString().replace("$", "").trim().isEmpty();  
          
        if (hasDebito && hasCredito) {  
            errores.add(String.format("Fila %d: No se puede tener débito y crédito simultáneamente", filaIndex + 1));  
            return;  
        }  
          
        if (!hasDebito && !hasCredito) {  
            errores.add(String.format("Fila %d: Se requiere débito o crédito", filaIndex + 1));  
            return;  
        }  
          
        validateMonto(hasDebito ? debito : credito, filaIndex);  
    }  
      
    private void validateMonto(Object valor, int filaIndex) {  
        try {  
            String valorStr = valor.toString().replace("$", "").replace(",", "").trim();  
            BigDecimal monto = new BigDecimal(valorStr);  
              
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {  
                errores.add(String.format("Fila %d: El monto debe ser mayor a cero", filaIndex + 1));  
            }  
        } catch (NumberFormatException e) {  
            errores.add(String.format("Fila %d: Formato monetario inválido. Use ej: $1,000.00", filaIndex + 1));  
        }  
    }  
}