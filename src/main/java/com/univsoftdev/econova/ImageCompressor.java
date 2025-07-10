package com.univsoftdev.econova;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageCompressor {

    public static void comprimirImagen(String rutaEntrada, String rutaSalida, float calidad, int nuevoAncho, int nuevoAlto) throws IOException {
        // 1. Leer la imagen original
        final BufferedImage imagenOriginal = ImageIO.read(new File(rutaEntrada));

        // 2. Redimensionar la imagen (opcional)
        final BufferedImage imagenRedimensionada = new BufferedImage(nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = imagenRedimensionada.createGraphics();
        g.drawImage(imagenOriginal, 0, 0, nuevoAncho, nuevoAlto, null);
        g.dispose();

        // 3. Configurar parámetros de compresión
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        final ImageWriter writer = writers.next();

        final ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(calidad); // 0.0 (máxima compresión) a 1.0 (máxima calidad)

        // 4. Escribir la imagen comprimida
        try (final ImageOutputStream ios = ImageIO.createImageOutputStream(new File(rutaSalida))) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(imagenRedimensionada, null, null), param);
        } finally {
            writer.dispose();
        }
    }

//    public static void main(String[] args) {
//        try {
//            // Ejemplo de uso:
//            comprimirImagen(
//                "imagen_original.jpg", 
//                "imagen_comprimida.jpg", 
//                0.5f,    // Calidad del 50%
//                800,     // Nuevo ancho
//                600      // Nuevo alto
//            );
//            System.out.println("Imagen comprimida exitosamente!");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
