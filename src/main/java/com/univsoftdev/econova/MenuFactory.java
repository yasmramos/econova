package com.univsoftdev.econova;

import com.univsoftdev.econova.config.view.*;
import com.univsoftdev.econova.contabilidad.forms.FormDashboardContabilidad;
import com.univsoftdev.econova.contabilidad.views.*;
import com.univsoftdev.econova.contabilidad.views.clasificador.*;
import com.univsoftdev.econova.contabilidad.views.comprobante.*;
import com.univsoftdev.econova.core.component.About;
import raven.modal.drawer.item.Item;
import raven.modal.drawer.item.MenuItem;

public class MenuFactory {

    public static MenuItem[] buildMenuItems() {

        return new MenuItem[]{
            new Item.Label("ECONOVA"),
            new Item("Dashboard", "dashboard.svg", FormDashboardContabilidad.class),
            new Item.Label("MODULOS"),
            new Item("Contabilidad", "forms.svg", FormContabilidad.class)
                    .subMenu("Comprobantes", FormComprobantes.class)
                    .subMenu("Clasificador de Cuentas", FormClasificador.class)
                    .subMenu("Balance de Comprobación", FormBalanceComprobacion.class)
                    .subMenu("Estado de Rendimiento", FormEstadoResultado.class),
            new Item("Costos y Procesos", "components.svg", FormCostosProcesos.class),
            new Item.Label("OTHER"),
            new Item("Configuración", "setting.svg", FormConfig.class)
                    .subMenu(new Item("Codificadores", FormCodificadores.class)
                            .subMenu("Unidades", FormUnidades.class))
                    .subMenu(new Item("Parámetros", FormParametros.class)
                            .subMenu("Ejercicio", FormEjercicios.class)
                            .subMenu("Monedas", FormMonedas.class)
                            .subMenu("Formatos", FormFormatos.class)
                            .subMenu("Idiomas", FormIdiomas.class)) 
                    .subMenu(new Item("Seguridad", FormSeguridad.class)
                            .subMenu("Permisos", FormPermisos.class)
                            .subMenu("Usuarios", FormUsuarios.class)),
            new Item("Acerca de ...", "about.svg", About.class),
            new Item("Salir", "logout.svg")
        };
    }
}
