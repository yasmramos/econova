package com.univsoftdev.econova;

import com.github.avaje.ext.EncConfig;
import java.time.LocalDate;

import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Empresa;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.config.service.EjercicioService;
import com.univsoftdev.econova.config.service.EmpresaService;
import com.univsoftdev.econova.config.service.UsuarioService;
import com.univsoftdev.econova.contabilidad.NaturalezaCuenta;
import com.univsoftdev.econova.contabilidad.TipoCuenta;
import com.univsoftdev.econova.contabilidad.model.Moneda;
import com.univsoftdev.econova.contabilidad.model.PlanDeCuentas;
import com.univsoftdev.econova.contabilidad.service.ContabilidadService;
import com.univsoftdev.econova.ebean.config.FlywayMigrator;
import com.univsoftdev.econova.security.keystore.KeystoreManager;
import com.univsoftdev.econova.security.PasswordGenerator;
import com.univsoftdev.econova.security.shiro.ShiroConfig;
import io.avaje.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class Test {

    @Inject
    ShiroConfig shiroConfig;

    @Inject
    EjercicioService ejercicioService;

    @Inject
    UsuarioService usuarioService;

    @Inject
    AppContext appContext;

    @Inject
    EmpresaService empresaService;

    @Inject
    ContabilidadService contabilidadService;

    @Inject
    AppSession session;
    
    @Inject
    FlywayMigrator flywayMigrator;


    public static void main(String[] args) {
        System.setProperty("config.encryption.password", "HWEUIE4684685*/-*/-*/GE/R*E/G*/D/S*D/H56*/4*7/5*5/67*45878678567866574$&%");
        System.setProperty("app.env", "test");
        Test test = Injector.get(Test.class);
        test.run();
    }

    public void run() {

        EncConfig.setEncryptedProperty("econova.keystore.master.key", String.valueOf(PasswordGenerator.generateStrongPassword(64)));

        char[] masterPassword = Config.getAs("econova.keystore.master.key", String::toCharArray);
        System.out.println("masterPassword: " + new String(masterPassword));
        
        flywayMigrator.migrate("tenant_emp_01");

        UserContext.set("yaramos", "accounting");
        
        KeystoreManager keystoreManager = Injector.get(KeystoreManager.class);
        keystoreManager.storePassword("config.encryption.password", PasswordGenerator.generateStrongPassword(16));
        keystoreManager.storePassword("ebean.enc.password", PasswordGenerator.generateStrongPassword(16));
        keystoreManager.close();

        UserContext.set("yaramos", "tenant_emp_01");
        
        PlanDeCuentas planDeCuentas = contabilidadService.crearPlanDeCuentas(1L, "Cuentas");

        Empresa empresa01 = new Empresa();
        empresa01.setName("Empresa Agropecuaria Chambas");
        empresa01.setCode("01");

        // Crear y registrar Unidades multi-tenant
        Unidad unidad01 = new Unidad();
        unidad01.setCodigo("01");
        unidad01.setNombre("EMPRESA");

        session.setUnidad(unidad01);

        Unidad unidad02 = new Unidad();
        unidad02.setCodigo("02");
        unidad02.setNombre("CULTIVOS VARIOS");

        UserContext.set("yaramos", "accounting");
        User usuario01 = usuarioService.crearUsuario("yaramos01", "Yasmany Ramos Garcia", "admin");
        usuarioService.crearUsuario("yaramos02", "Yasmany Ramos Garcia", "admin");
        usuarioService.crearUsuario("yaramos03", "Yasmany Ramos Garcia", "admin");

        session.setUser(usuario01);

        UserContext.set("yaramos", "tenant_emp_01");
        empresa01.addUnidad(unidad01);
        empresa01.addUnidad(unidad02);

        empresaService.save(empresa01);

        final var periodoEnero2025 = new Periodo();
        periodoEnero2025.setNombre("Enero");
        periodoEnero2025.setFechaInicio(LocalDate.now());
        periodoEnero2025.setFechaFin(LocalDate.now().plusMonths(5));
        periodoEnero2025.setCurrent(true);

        final var periodoEnero2026 = new Periodo();
        periodoEnero2026.setNombre("Enero");
        periodoEnero2026.setFechaInicio(LocalDate.now());
        periodoEnero2026.setFechaFin(LocalDate.now().plusMonths(5));
        periodoEnero2026.setCurrent(false);

        Ejercicio ejercicio2025 = ejercicioService.crearEjercicio("2025", 2025, LocalDate.now(), LocalDate.now().plusMonths(5));
        ejercicio2025.addPeriodo(periodoEnero2025);

        Ejercicio ejercicio2026 = ejercicioService.crearEjercicio("2026", 2026, LocalDate.now(), LocalDate.now().plusMonths(5));
        ejercicio2026.addPeriodo(periodoEnero2026);

        session.setEjercicio(ejercicio2025);
        session.setPeriodo(periodoEnero2025);

        contabilidadService.crearCuenta(
                "100",
                "Efectivo en Caja",
                NaturalezaCuenta.ACREEDORA,
                TipoCuenta.ACTIVO,
                new Moneda("CUP", "Moneda Cubana"),
                planDeCuentas
        );

        contabilidadService.crearCuenta(
                "110",
                "Efectivo en Banco",
                NaturalezaCuenta.ACREEDORA,
                TipoCuenta.ACTIVO,
                new Moneda("CUP", "Moneda Cubana"),
                planDeCuentas
        );

        contabilidadService.crearCuenta(
                "470",
                "Prestamos Bancarios a Corto Plazo",
                NaturalezaCuenta.ACREEDORA,
                TipoCuenta.ACTIVO,
                new Moneda("CUP", "Moneda Cubana"),
                planDeCuentas
        );

    }

}
