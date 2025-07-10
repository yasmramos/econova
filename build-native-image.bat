@echo off
REM Script para compilar Econova como imagen nativa con GraalVM Native Image en Windows
REM Requiere: GraalVM instalado y variable de entorno GRAALVM_HOME

set APP_NAME=econova
set MAIN_CLASS=com.univsoftdev.econova.Econova
set JAR_FILE=target\econova-1.0.jar
set OUTPUT=target\%APP_NAME%

REM Opciones recomendadas para aplicaciones Java Swing y Ebean
set OPTIONS=--no-fallback --enable-all-security-services --enable-url-protocols=http,https --report-unsupported-elements-at-runtime --initialize-at-build-time=io.ebean,com.univsoftdev.econova --allow-incomplete-classpath --enable-http --enable-https --enable-jni --enable-https --enable-all-security-services --verbose

REM Compilación
"%GRAALVM_HOME%\bin\native-image.cmd" %OPTIONS% -H:Name=%OUTPUT% -cp %JAR_FILE% %MAIN_CLASS%

if %ERRORLEVEL%==0 (
  echo Imagen nativa generada en %OUTPUT%
) else (
  echo Error al generar la imagen nativa
)
