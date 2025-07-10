package com.univsoftdev.econova.contabilidad.views.comprobante;

import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.TipoTransaccion;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.model.Moneda;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.contabilidad.service.ContabilidadService;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.table.DefaultTableModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsientoProcessor {

    private final ContabilidadService contabilidadService;
    private final Asiento asiento;
    private List<String> errores = new ArrayList<>();

    public AsientoProcessor(ContabilidadService contabilidadService, Asiento asiento) {
        this.contabilidadService = contabilidadService;
        this.asiento = asiento;
    }

    public void procesarTabla(DefaultTableModel table1) {
        errores.clear(); // Limpiar errores previos
        int rows = table1.getRowCount();

        for (int i = 0; i < rows; i++) {
            try {
                procesarFila(table1, i);
            } catch (Exception e) {
                String errorMsg = String.format("Error procesando fila %d: %s", i + 1, e.getMessage());
                log.error(errorMsg, e); // Esto registrará el mensaje y el stack trace
                errores.add(errorMsg);
            }
        }

        if (!errores.isEmpty()) {
            log.error("Errores encontrados al procesar el asiento: {}", errores);
            asiento.setEstadoAsiento(EstadoAsiento.ERROR);
        } 
    }

    private void procesarFila(DefaultTableModel table1, int filaIndex) {
        try {
            // Obtener valores con validación
            String cta = obtenerValorValidado(table1, filaIndex, 0, "Código de cuenta");
            String sbcta = obtenerValorOpcional(table1, filaIndex, 1);
            String sctro = obtenerValorOpcional(table1, filaIndex, 2);
            String anal = obtenerValorOpcional(table1, filaIndex, 3);
            String epig = obtenerValorOpcional(table1, filaIndex, 4);

            Object debito = table1.getValueAt(filaIndex, 5);
            Object credito = table1.getValueAt(filaIndex, 6);

            TransaccionData transaccionData = determinarTransaccion(debito, credito, filaIndex);
            procesarNivelesCuenta(cta, sbcta, sctro, anal, transaccionData, epig, filaIndex);

        } catch (Exception e) {
            throw new RuntimeException("Error en fila " + (filaIndex + 1) + ": " + e.getMessage(), e);
        }
    }

    private String obtenerValorValidado(DefaultTableModel table, int row, int col, String nombreCampo) {
        Object value = table.getValueAt(row, col);
        if (value == null || value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException(nombreCampo + " es requerido");
        }
        return value.toString().trim();
    }

    private String obtenerValorOpcional(DefaultTableModel table, int row, int col) {
        Object value = table.getValueAt(row, col);
        return (value != null) ? value.toString().trim() : null;
    }

    private TransaccionData determinarTransaccion(@NotNull Object debito,@NotNull Object credito, int filaIndex) {
        boolean hasDebito = debito != null && !debito.toString().replace("$", "").trim().isEmpty();
        boolean hasCredito = credito != null && !credito.toString().replace("$", "").trim().isEmpty();

        if (hasDebito && hasCredito) {
            throw new IllegalArgumentException("Fila " + (filaIndex + 1) + ": No se puede tener débito y crédito simultáneamente");
        }

        if (!hasDebito && !hasCredito) {
            throw new IllegalArgumentException("Fila " + (filaIndex + 1) + ": Se requiere débito o crédito");
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
                throw new IllegalArgumentException("Fila " + (filaIndex + 1) + ": El monto debe ser mayor a cero");
            }

            return new TransaccionData(tipo, monto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Fila " + (filaIndex + 1) + ": Formato monetario inválido. Use ej: $1,000.00");
        }
    }

    private void procesarNivelesCuenta(String cta, String sbcta, String sctro, String anal,
            TransaccionData transaccionData, String epigrafe, int filaIndex) {

        try {
            // Procesar cuenta principal
            procesarTransaccion(cta, null, null, null, transaccionData, epigrafe, filaIndex);

            // Procesar subcuenta si existe
            if (sbcta != null && !sbcta.isEmpty()) {
                procesarTransaccion(cta, sbcta, null, null, transaccionData, epigrafe, filaIndex);
            }

            // Procesar centro si existe
            if (sctro != null && !sctro.isEmpty()) {
                procesarTransaccion(cta, sbcta, sctro, null, transaccionData, epigrafe, filaIndex);
            }

            // Procesar analítica si existe
            if (anal != null && !anal.isEmpty()) {
                procesarTransaccion(cta, sbcta, sctro, anal, transaccionData, epigrafe, filaIndex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar niveles de cuenta en fila " + (filaIndex + 1) + ": " + e.getMessage(), e);
        }
    }

    private void procesarTransaccion(String cuenta, String subcuenta, String centro, String analitica,
            TransaccionData transaccionData, String epigrafe, int filaIndex) {

        String codigoCompleto = construirCodigoCuenta(cuenta, subcuenta, centro, analitica);

        try {
            Optional<Cuenta> cuentaOpt = contabilidadService.findCuentaByCodigo(codigoCompleto);

            if (cuentaOpt.isPresent()) {
                Cuenta cuentaEncontrada = cuentaOpt.get();

                // Validar que la cuenta esté activa
                if (!cuentaEncontrada.isActiva()) {
                    throw new IllegalArgumentException("La cuenta " + codigoCompleto + " no está activa");
                }

                Transaccion transaccion = new Transaccion(
                        transaccionData.tipo(),
                        transaccionData.monto(),
                        asiento.getFecha(),
                        new Moneda("CUP", "Moneda Nacional"),
                        AppContext.getInstance().getSession().getCurrentUser(),
                        cuentaEncontrada
                );

                transaccion.setAsiento(asiento);
                transaccion.setLibroMayor(cuentaEncontrada.getLibroMayor());
                asiento.getTransacciones().add(transaccion);

            } else {
                throw new IllegalArgumentException("Cuenta no encontrada: " + codigoCompleto);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error en transacción: " + e.getMessage(), e);
        }
    }

    private String construirCodigoCuenta(String cuenta, String subcuenta, String centro, String analitica) {
        StringBuilder codigo = new StringBuilder(cuenta);

        if (subcuenta != null && !subcuenta.isEmpty()) {
            codigo.append(".").append(subcuenta);
        }
        if (centro != null && !centro.isEmpty()) {
            codigo.append(".").append(centro);
        }
        if (analitica != null && !analitica.isEmpty()) {
            codigo.append(".").append(analitica);
        }

        return codigo.toString();
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }

    // Record para almacenar los datos de la transacción
    private record TransaccionData(TipoTransaccion tipo, BigDecimal monto) {

    }
}
