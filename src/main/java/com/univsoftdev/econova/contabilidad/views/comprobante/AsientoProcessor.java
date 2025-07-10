package com.univsoftdev.econova.contabilidad.views.comprobante;

import com.univsoftdev.econova.TipoTransaccion;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.contabilidad.service.ContabilidadService;
import com.univsoftdev.econova.contabilidad.views.comprobante.validation.AsientoRowValidator;
import com.univsoftdev.econova.contabilidad.views.comprobante.factory.TransaccionFactory;
import com.univsoftdev.econova.contabilidad.views.comprobante.dto.AsientoRowData;
import com.univsoftdev.econova.contabilidad.views.comprobante.dto.TransaccionData;
import com.univsoftdev.econova.contabilidad.views.comprobante.dto.ValidationResult;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AsientoProcessor {

    private final ContabilidadService contabilidadService;
    private final AsientoRowValidator validator;
    private final TransaccionFactory transaccionFactory;
    private final CuentaCodeBuilder cuentaCodeBuilder;
    private final Asiento asiento;
    private final List<String> errores = new ArrayList<>();

    public AsientoProcessor(ContabilidadService contabilidadService,
            AsientoRowValidator validator,
            TransaccionFactory transaccionFactory,
            CuentaCodeBuilder cuentaCodeBuilder,
            Asiento asiento) {
        this.contabilidadService = contabilidadService;
        this.validator = validator;
        this.transaccionFactory = transaccionFactory;
        this.cuentaCodeBuilder = cuentaCodeBuilder;
        this.asiento = asiento;
    }

    private List<AsientoRowData> extractRowsData(DefaultTableModel table) {
        List<AsientoRowData> rows = new ArrayList<>();
        for (int i = 0; i < table.getRowCount(); i++) {
            rows.add(AsientoRowData.fromTableModel(table, i));
        }
        return rows;
    }

    private void processRow(AsientoRowData rowData) {
        try {
            ValidationResult validation = validator.validate(rowData, rowData.getFilaIndex());
            if (!validation.isValid()) {
                errores.addAll(validation.getErrores());
                return;
            }

            TransaccionData transaccionData = extractTransaccionData(rowData);
            processAccountLevels(rowData, transaccionData);

        } catch (Exception e) {
            String errorMsg = String.format("Error procesando fila %d: %s",
                    rowData.getFilaIndex() + 1, e.getMessage());
            log.error(errorMsg, e);
            errores.add(errorMsg);
        }
    }

    private void processAccountLevels(AsientoRowData rowData, TransaccionData transaccionData) {
        List<String> accountCodes = cuentaCodeBuilder.buildAccountCodes(rowData);

        for (String accountCode : accountCodes) {
            processTransaction(accountCode, transaccionData, rowData.getFilaIndex());
        }
    }

    private void processTransaction(String accountCode, TransaccionData transaccionData, int filaIndex) {
        try {
            Optional<Cuenta> cuentaOpt = contabilidadService.findCuentaByCodigo(accountCode);

            if (cuentaOpt.isEmpty()) {
                throw new IllegalArgumentException("Cuenta no encontrada: " + accountCode);
            }

            Cuenta cuenta = cuentaOpt.get();
            validateCuentaActiva(cuenta, accountCode);

            Transaccion transaccion = transaccionFactory.createTransaccion(transaccionData, cuenta, asiento);
            asiento.getTransacciones().add(transaccion);

        } catch (Exception e) {
            throw new RuntimeException("Error en transacción fila " + (filaIndex + 1) + ": " + e.getMessage(), e);
        }
    }

    private void validateCuentaActiva(Cuenta cuenta, String codigo) {
        if (!cuenta.isActiva()) {
            throw new IllegalArgumentException("La cuenta " + codigo + " no está activa");
        }
    }

    public List<String> getErrores() {
        return new ArrayList<>(errores);
    }

    public void procesarTabla(DefaultTableModel table1) {
        errores.clear(); // Limpiar errores previos    

        // Eliminar última fila si está vacía    
        removeLastRowIfEmpty(table1);

        // Usar la nueva arquitectura refactorizada  
        List<AsientoRowData> rowsData = extractRowsData(table1);

        for (AsientoRowData rowData : rowsData) {
            processRow(rowData);
        }

        if (!errores.isEmpty()) {
            log.error("Errores encontrados al procesar el asiento: {}", errores);
            asiento.setEstadoAsiento(EstadoAsiento.ERROR);
        }
    }

    private void removeLastRowIfEmpty(DefaultTableModel table) {
        int rowCount = table.getRowCount();
        if (rowCount == 0) {
            return;
        }

        int lastRowIndex = rowCount - 1;
        boolean isEmpty = true;

        // Verificar si todas las columnas de la última fila están vacías  
        for (int col = 0; col < table.getColumnCount(); col++) {
            Object value = table.getValueAt(lastRowIndex, col);
            if (value != null && !value.toString().trim().isEmpty()) {
                isEmpty = false;
                break;
            }
        }

        if (isEmpty) {
            table.removeRow(lastRowIndex);
            log.debug("Última fila vacía eliminada de la tabla");
        }
    }

    private TransaccionData extractTransaccionData(AsientoRowData rowData) {
        Object debito = rowData.getDebito();
        Object credito = rowData.getCredito();

        boolean hasDebito = debito != null && !debito.toString().replace("$", "").trim().isEmpty();
        boolean hasCredito = credito != null && !credito.toString().replace("$", "").trim().isEmpty();

        if (hasDebito && hasCredito) {
            throw new IllegalArgumentException("Fila " + (rowData.getFilaIndex() + 1) + ": No se puede tener débito y crédito simultáneamente");
        }

        if (!hasDebito && !hasCredito) {
            throw new IllegalArgumentException("Fila " + (rowData.getFilaIndex() + 1) + ": Se requiere débito o crédito");
        }

        try {
            String valorStr;
            TipoTransaccion tipo;

            if (hasDebito) {
                valorStr = debito.toString().replace("$", "").trim();
                tipo = TipoTransaccion.DEBITO;
            } else {
                valorStr = credito.toString().replace("$", "").trim();
                tipo = TipoTransaccion.CREDITO;
            }

            BigDecimal monto = new BigDecimal(valorStr.replace(",", ""));

            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Fila " + (rowData.getFilaIndex() + 1) + ": El monto debe ser mayor a cero");
            }

            return new TransaccionData(tipo, monto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Fila " + (rowData.getFilaIndex() + 1) + ": Formato monetario inválido. Use ej: $1,000.00");
        }
    }
}
