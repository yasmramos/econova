package com.univsoftdev.econova.contabilidad.views.comprobante.dto;

import com.univsoftdev.econova.TipoTransaccion;
import java.math.BigDecimal;

public record TransaccionData(TipoTransaccion tipo, BigDecimal monto) {

}
