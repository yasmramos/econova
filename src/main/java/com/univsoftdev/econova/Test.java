package com.univsoftdev.econova;

import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.config.service.EjercicioService;
import com.univsoftdev.econova.config.service.UnidadService;
import com.univsoftdev.econova.config.service.UsuarioService;
import com.univsoftdev.econova.contabilidad.NaturalezaCuenta;
import com.univsoftdev.econova.contabilidad.TipoApertura;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.contabilidad.service.CuentaService;
import com.univsoftdev.econova.contabilidad.service.PlanDeCuentasService;
import com.univsoftdev.econova.seguridad.ShiroConfig;
import io.ebean.Database;
import java.time.LocalDate;

public class Test {

    public static void main(String[] args) {
        final var injector = AppContext.getInstance().getInjector();
        var unidadService = injector.get(UnidadService.class);
        var ejercicioService = injector.get(EjercicioService.class);
        var cuentaService = injector.get(CuentaService.class);
        var planDeCuentasService = injector.get(PlanDeCuentasService.class);
        var usuarioService = injector.get(UsuarioService.class);
        var appContext = AppContext.getInstance();
        var session = appContext.getSession();
        ShiroConfig.setupShiro(injector.get(Database.class));

        // Crear y registrar Unidades multi-tenant
        Unidad unidad01 = new Unidad();
        unidad01.setCodigo("01");
        unidad01.setNombre("EMPRESA");
        unidad01.setSchemaTenant("tenant_01");
        session.setUnidad(unidad01);

        Unidad unidad02 = new Unidad();
        unidad02.setCodigo("02");
        unidad02.setNombre("CULTIVOS VARIOS");
        unidad02.setSchemaTenant("tenant_02");

        Unidad unidad03 = new Unidad();
        unidad03.setCodigo("03");
        unidad03.setNombre("GANADERA");
        unidad03.setSchemaTenant("tenant_03");

        Unidad unidad04 = new Unidad();
        unidad04.setCodigo("04");
        unidad04.setNombre("ATENCION A GRANOS");
        unidad04.setSchemaTenant("tenant_04");

        final var usuario01 = new User();
        usuario01.setUserName("yaramos");
        usuario01.setPassword("admin");
        usuario01.setFullName("Yasmany Ramos Garcia");
        usuario01.setActivo(true);
        
        session.setCurrentUser(usuario01);
        usuarioService.save(usuario01);

        final var usuario02 = new User();
        usuario02.setUserName("yrgarcia");
        usuario02.setPassword("admin");
        usuario02.setFullName("Yasmany Ramos");
        usuario02.setActivo(true);

        usuarioService.save(usuario02);

        final var ejercicio2025 = new Ejercicio();
        ejercicio2025.setNombre("2025");
        ejercicio2025.setYear(2025);
        ejercicio2025.setFechaInicio(LocalDate.now());
        ejercicio2025.setFechaFin(LocalDate.now().plusMonths(5));
        ejercicio2025.setCurrent(true);

        final var periodoEnero2025 = new Periodo();
        periodoEnero2025.setNombre("Enero");
        periodoEnero2025.setFechaInicio(LocalDate.now());
        periodoEnero2025.setFechaFin(LocalDate.now().plusMonths(5));
        periodoEnero2025.setEjercicio(ejercicio2025);
        periodoEnero2025.setCurrent(true);

        ejercicio2025.addPeriodo(periodoEnero2025);

        ejercicioService.save(ejercicio2025);

        final var ejercicio2026 = new Ejercicio();
        ejercicio2026.setNombre("2026");
        ejercicio2026.setYear(2026);
        ejercicio2026.setFechaInicio(LocalDate.now());
        ejercicio2026.setFechaFin(LocalDate.now().plusMonths(5));
        ejercicio2026.setCurrent(false);

        final var periodoEnero2026 = new Periodo();
        periodoEnero2026.setNombre("Enero");
        periodoEnero2026.setFechaInicio(LocalDate.now());
        periodoEnero2026.setFechaFin(LocalDate.now().plusMonths(5));
        periodoEnero2026.setEjercicio(ejercicio2026);
        periodoEnero2026.setCurrent(false);

        ejercicio2026.addPeriodo(periodoEnero2026);

        ejercicioService.save(ejercicio2026);

        session.setEjercicio(ejercicio2025);
        session.setPeriodo(periodoEnero2025);

        final var cuenta100 = new Cuenta();
        cuenta100.setCodigo("100");
        cuenta100.setNombre("Efectivo en Caja");
        cuenta100.setActiva(true);
        cuenta100.setApertura(false);
        cuenta100.setPlanDeCuenta(planDeCuentasService.getPlanDeCuentas());
        cuenta100.setTipoApertura(TipoApertura.SIN_APERTURA);
        cuenta100.setNaturaleza(NaturalezaCuenta.DEUDORA);

        final var cuenta110 = new Cuenta();
        cuenta110.setCodigo("110");
        cuenta110.setNombre("Efectivo en Banco");
        cuenta110.setActiva(true);
        cuenta110.setApertura(false);
        cuenta110.setPlanDeCuenta(planDeCuentasService.getPlanDeCuentas());
        cuenta110.setTipoApertura(TipoApertura.SIN_APERTURA);
        cuenta110.setNaturaleza(NaturalezaCuenta.DEUDORA);

        final var cuenta470 = new Cuenta();
        cuenta470.setCodigo("470");
        cuenta470.setNombre("Prestamos Bancarios a Corto Plazo");
        cuenta470.setActiva(true);
        cuenta470.setApertura(false);
        cuenta470.setPlanDeCuenta(planDeCuentasService.getPlanDeCuentas());
        cuenta470.setTipoApertura(TipoApertura.SIN_APERTURA);
        cuenta470.setNaturaleza(NaturalezaCuenta.ACREEDORA);

        cuentaService.addCuentas(cuenta100, cuenta110, cuenta470);

    }
}
