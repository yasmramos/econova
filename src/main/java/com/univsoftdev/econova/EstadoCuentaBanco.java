package com.univsoftdev.econova;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase principal para el registro de cuenta*
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class EstadoCuentaBanco {

    private String fecha;
    private String ref_corrie;
    private String observ;
    private BigDecimal importe;
    private String tipo;

    @Override
    public String toString() {
        return "Fecha: " + fecha
                + " | Ref: " + ref_corrie
                + " | Observación: " + observ
                + " | Importe: " + importe
                + " | Tipo: " + tipo;
    }
}

/**
 * Contenedor del conjunto de registros*
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "NewDataSet")
@XmlAccessorType(XmlAccessType.FIELD)
class NewDataSet {

    @XmlElement(name = "Estado_x0020_de_x0020_Cuenta")
    private List<EstadoCuentaBanco> registros;

}

// Clase principal de ejecución
class App {

    public static void main(String[] args) throws Exception {
        File file = new File("Pasted_Text_1748030339793.txt");

        JAXBContext context = JAXBContext.newInstance(NewDataSet.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        NewDataSet dataSet = (NewDataSet) unmarshaller.unmarshal(file);

        System.out.println("Total de registros: " + dataSet.getRegistros().size());
        for (EstadoCuentaBanco ec : dataSet.getRegistros()) {
            System.out.println(ec);
        }
    }
}
