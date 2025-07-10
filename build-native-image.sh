#!/bin/bash
# Script para compilar Econova como imagen nativa con GraalVM Native Image
# Requiere: GraalVM instalado y variable de entorno GRAALVM_HOME

APP_NAME="econova"
MAIN_CLASS="com.univsoftdev.econova.Econova"
JAR_FILE="target/econova-1.0.jar"
OUTPUT="target/$APP_NAME"

# Opciones recomendadas para aplicaciones Java Swing y Ebean
OPTIONS="--no-fallback --enable-all-security-services --enable-url-protocols=http,https --report-unsupported-elements-at-runtime --initialize-at-build-time=io.ebean,com.univsoftdev.econova --allow-incomplete-classpath --enable-http --enable-https --enable-jni --enable-https --enable-all-security-services --verbose"

# Si necesitas recursos adicionales, agrega:
# --resources=src/main/resources/**

# Compilación
$GRAALVM_HOME/bin/native-image $OPTIONS -H:Name=$OUTPUT -cp $JAR_FILE $MAIN_CLASS

# Mensaje final
if [ $? -eq 0 ]; then
  echo "Imagen nativa generada en $OUTPUT"
else
  echo "Error al generar la imagen nativa"
fi
