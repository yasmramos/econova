package com.univsoftdev.econova.contabilidad.views.comprobante.dto;

import java.util.List;

public record ProcessingResult(boolean success, List<String> errores) {

    public boolean hasErrors() {
        return !errores.isEmpty();
    }

    public int getErrorCount() {
        return errores.size();
    }
}
