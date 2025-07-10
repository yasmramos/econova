package com.univsoftdev.econova.contabilidad.views.comprobante;

import com.univsoftdev.econova.contabilidad.views.comprobante.dto.AsientoRowData;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CuentaCodeBuilder {

    public List<String> buildAccountCodes(AsientoRowData rowData) {
        List<String> codes = new ArrayList<>();

        // Cuenta principal  
        codes.add(rowData.getCta());

        // Subcuenta si existe  
        if (hasValue(rowData.getSbcta())) {
            codes.add(buildCode(rowData.getCta(), rowData.getSbcta()));
        }

        // Centro si existe  
        if (hasValue(rowData.getSctro())) {
            codes.add(buildCode(rowData.getCta(), rowData.getSbcta(), rowData.getSctro()));
        }

        // Analítica si existe  
        if (hasValue(rowData.getAnal())) {
            codes.add(buildCode(rowData.getCta(), rowData.getSbcta(), rowData.getSctro(), rowData.getAnal()));
        }

        return codes;
    }

    private String buildCode(String... parts) {
        StringBuilder codigo = new StringBuilder();
        for (String part : parts) {
            if (hasValue(part)) {
                if (codigo.length() > 0) {
                    codigo.append(".");
                }
                codigo.append(part);
            }
        }
        return codigo.toString();
    }

    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
