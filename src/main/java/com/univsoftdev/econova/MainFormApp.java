package com.univsoftdev.econova;

import java.awt.event.*;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.util.FontUtils;
import jakarta.inject.Inject;
import com.univsoftdev.econova.component.wizard.Wizard;
import com.univsoftdev.econova.config.service.EmpresaService;
import com.univsoftdev.econova.config.view.FormDatabaseConnection;
import com.univsoftdev.econova.config.view.FormSeleccionUnidad;
import com.univsoftdev.econova.contabilidad.component.About;
import com.univsoftdev.econova.core.config.AppConfig;
import com.univsoftdev.econova.core.system.FormManager;
import com.univsoftdev.econova.core.utils.AppPreferences;
import com.univsoftdev.econova.core.utils.DialogUtils;
import com.univsoftdev.econova.core.view.components.FormCambiarFechaProcesamiento;
import io.avaje.config.Config;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serial;
import javax.swing.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import lombok.extern.slf4j.Slf4j;
import raven.modal.Drawer;

@Slf4j
public class MainFormApp extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private AppContext appContext;

    @Inject
    public MainFormApp() {
        initComponents();
        this.init();
    }

    private void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        this.appContext = Injector.get(AppContext.class);
        this.setTitle(appContext.getAppName() + " " + appContext.getVersion().toString());
        getJMenuBar().setVisible(false);
        Drawer.installDrawer(this, EconovaDrawerBuilder.getInstance());
        FormManager.install(this);
        var frame = this;

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    EmpresaService empresaService = Injector.get(EmpresaService.class);
                    if (empresaService.findAll().isEmpty()) {
                        Wizard wizard = new Wizard(frame, true);
                        wizard.setLocationRelativeTo(null);
                        wizard.setVisible(true);
                    }
                });

            }

        });
    }

    private void salir(ActionEvent e) {
        
        System.exit(0);
    }

    private void cambiarUnidadContable(ActionEvent e) {
        //DialogUtils.showModalDialog(this, new FormSeleccionUnidad(), "Selección de Unidad Contable");
    }

    private void thisWindowClosing(WindowEvent e) {
        try {
            Injector.get(AppContext.class).reset();
            Injector.close();
            Config.asProperties().store(new FileOutputStream("application.properties"), null);
        } catch (FileNotFoundException ex) {
            System.getLogger(MainFormApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (IOException ex) {
            System.getLogger(MainFormApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void cambiarFechaProcesamiento(ActionEvent e) {
        DialogUtils.showModalDialog(this, new FormCambiarFechaProcesamiento(), "Cambiar Fecha de Procesamiento");
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.menuBar1 = new JMenuBar();
	this.menu1 = new JMenu();
	this.menuItemCambiarUnidadContable = new JMenuItem();
	this.menuItemCambiarFechaProcesamiento = new JMenuItem();
	this.menuItemEstadoOperaciones = new JMenuItem();
	this.menuItemImprimir = new JMenuItem();
	this.menuItemExportar = new JMenuItem();
	this.menuItemImportar = new JMenuItem();
	this.menuItemConfiguracion = new JMenuItem();
	this.menuItemSalir = new JMenuItem();
	this.menu2 = new JMenu();
	this.menuItem14 = new JMenuItem();
	this.menu3 = new JMenu();
	this.menu4 = new JMenu();
	this.menuItem25 = new JMenuItem();
	this.menuItem26 = new JMenuItem();
	this.menuItem27 = new JMenuItem();
	this.menu5 = new JMenu();
	this.menuItemApertura = new JMenuItem();
	this.menuItemMostrarInactivas = new JMenuItem();
	this.menuItemFormato = new JMenuItem();
	this.menuItemNivelParaAnalisis = new JMenuItem();
	this.menuItemDefinicionIngresosEgresos = new JMenuItem();
	this.menuItemGruposCuentas = new JMenuItem();
	this.menuHistoria = new JMenu();
	this.menuItemSubMayor = new JMenuItem();
	this.menuItemSaldoCuentas = new JMenuItem();
	this.menuItemSaldosTransitorios = new JMenuItem();
	this.menuItemMayor = new JMenuItem();
	this.menuItemSaldoPeriodos = new JMenuItem();
	this.menuItemResumen = new JMenuItem();
	this.menuItemCuentasControladas = new JMenuItem();
	this.menuItemCuentasNatMixta = new JMenuItem();
	this.menuItemCuentasSaldoInverso = new JMenuItem();
	this.menuItemCentrosSaldoInverso = new JMenuItem();
	this.menuItemElementosSaldoInverso = new JMenuItem();
	this.menu6 = new JMenu();
	this.menuItem8 = new JMenuItem();
	this.menuItem9 = new JMenuItem();
	this.menuItem10 = new JMenuItem();
	this.menuItem11 = new JMenuItem();
	this.menuItem12 = new JMenuItem();
	this.menuItem13 = new JMenuItem();
	this.menu7 = new JMenu();
	this.menu8 = new JMenu();

	//======== this ========
	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		thisWindowClosing(e);
	    }
	});
	var contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== menuBar1 ========
	{

	    //======== menu1 ========
	    {
		this.menu1.setText("Sistema"); //NOI18N

		//---- menuItemCambiarUnidadContable ----
		this.menuItemCambiarUnidadContable.setText("Cambiar Unidad Contable"); //NOI18N
		this.menuItemCambiarUnidadContable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.menuItemCambiarUnidadContable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
		this.menuItemCambiarUnidadContable.addActionListener(e -> cambiarUnidadContable(e));
		this.menu1.add(this.menuItemCambiarUnidadContable);

		//---- menuItemCambiarFechaProcesamiento ----
		this.menuItemCambiarFechaProcesamiento.setText("Cambiar Fecha de Procesamiento"); //NOI18N
		this.menuItemCambiarFechaProcesamiento.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.menuItemCambiarFechaProcesamiento.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
		this.menuItemCambiarFechaProcesamiento.addActionListener(e -> cambiarFechaProcesamiento(e));
		this.menu1.add(this.menuItemCambiarFechaProcesamiento);
		this.menu1.addSeparator();

		//---- menuItemEstadoOperaciones ----
		this.menuItemEstadoOperaciones.setText("Estado de Operaciones"); //NOI18N
		this.menuItemEstadoOperaciones.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.menuItemEstadoOperaciones.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		this.menu1.add(this.menuItemEstadoOperaciones);
		this.menu1.addSeparator();

		//---- menuItemImprimir ----
		this.menuItemImprimir.setText("Imprimir"); //NOI18N
		this.menuItemImprimir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.menuItemImprimir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
		this.menu1.add(this.menuItemImprimir);

		//---- menuItemExportar ----
		this.menuItemExportar.setText("Exportar"); //NOI18N
		this.menuItemExportar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.menuItemExportar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
		this.menu1.add(this.menuItemExportar);

		//---- menuItemImportar ----
		this.menuItemImportar.setText("Importar"); //NOI18N
		this.menuItemImportar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.menuItemImportar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
		this.menu1.add(this.menuItemImportar);
		this.menu1.addSeparator();

		//---- menuItemConfiguracion ----
		this.menuItemConfiguracion.setText("Configuraci\u00f3n"); //NOI18N
		this.menuItemConfiguracion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.menu1.add(this.menuItemConfiguracion);
		this.menu1.addSeparator();

		//---- menuItemSalir ----
		this.menuItemSalir.setText("Salir"); //NOI18N
		this.menuItemSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
		this.menuItemSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.menuItemSalir.addActionListener(e -> salir(e));
		this.menu1.add(this.menuItemSalir);
	    }
	    this.menuBar1.add(this.menu1);

	    //======== menu2 ========
	    {
		this.menu2.setText("Costos"); //NOI18N

		//---- menuItem14 ----
		this.menuItem14.setText("text"); //NOI18N
		this.menu2.add(this.menuItem14);
	    }
	    this.menuBar1.add(this.menu2);

	    //======== menu3 ========
	    {
		this.menu3.setText("Indicadores"); //NOI18N
	    }
	    this.menuBar1.add(this.menu3);

	    //======== menu4 ========
	    {
		this.menu4.setText("Comprobantes"); //NOI18N

		//---- menuItem25 ----
		this.menuItem25.setText("Nuevo"); //NOI18N
		this.menu4.add(this.menuItem25);

		//---- menuItem26 ----
		this.menuItem26.setText("Abrir"); //NOI18N
		this.menu4.add(this.menuItem26);

		//---- menuItem27 ----
		this.menuItem27.setText("Buscar..."); //NOI18N
		this.menu4.add(this.menuItem27);
	    }
	    this.menuBar1.add(this.menu4);

	    //======== menu5 ========
	    {
		this.menu5.setText("Cuentas"); //NOI18N

		//---- menuItemApertura ----
		this.menuItemApertura.setText("Apertura"); //NOI18N
		this.menu5.add(this.menuItemApertura);

		//---- menuItemMostrarInactivas ----
		this.menuItemMostrarInactivas.setText("Mostrar Inactivas"); //NOI18N
		this.menu5.add(this.menuItemMostrarInactivas);

		//---- menuItemFormato ----
		this.menuItemFormato.setText("Formato"); //NOI18N
		this.menu5.add(this.menuItemFormato);

		//---- menuItemNivelParaAnalisis ----
		this.menuItemNivelParaAnalisis.setText("Nivel para An\u00e1lisis"); //NOI18N
		this.menu5.add(this.menuItemNivelParaAnalisis);

		//---- menuItemDefinicionIngresosEgresos ----
		this.menuItemDefinicionIngresosEgresos.setText("Defnici\u00f3n de Ingresos/Egresos"); //NOI18N
		this.menu5.add(this.menuItemDefinicionIngresosEgresos);

		//---- menuItemGruposCuentas ----
		this.menuItemGruposCuentas.setText("Grupos de Cuentas"); //NOI18N
		this.menu5.add(this.menuItemGruposCuentas);
		this.menu5.addSeparator();

		//======== menuHistoria ========
		{
		    this.menuHistoria.setText("Historia"); //NOI18N
		}
		this.menu5.add(this.menuHistoria);

		//---- menuItemSubMayor ----
		this.menuItemSubMayor.setText("Submayor"); //NOI18N
		this.menu5.add(this.menuItemSubMayor);

		//---- menuItemSaldoCuentas ----
		this.menuItemSaldoCuentas.setText("Saldos de Cuentas"); //NOI18N
		this.menu5.add(this.menuItemSaldoCuentas);

		//---- menuItemSaldosTransitorios ----
		this.menuItemSaldosTransitorios.setText("Saldos Transitorios"); //NOI18N
		this.menu5.add(this.menuItemSaldosTransitorios);

		//---- menuItemMayor ----
		this.menuItemMayor.setText("Mayor"); //NOI18N
		this.menu5.add(this.menuItemMayor);

		//---- menuItemSaldoPeriodos ----
		this.menuItemSaldoPeriodos.setText("Saldos en los per\u00edodos ..."); //NOI18N
		this.menu5.add(this.menuItemSaldoPeriodos);

		//---- menuItemResumen ----
		this.menuItemResumen.setText("Resumen"); //NOI18N
		this.menu5.add(this.menuItemResumen);

		//---- menuItemCuentasControladas ----
		this.menuItemCuentasControladas.setText("Cuentas Controladas"); //NOI18N
		this.menu5.add(this.menuItemCuentasControladas);

		//---- menuItemCuentasNatMixta ----
		this.menuItemCuentasNatMixta.setText("Cuentas con Naturaleza Mixta"); //NOI18N
		this.menu5.add(this.menuItemCuentasNatMixta);
		this.menu5.addSeparator();

		//---- menuItemCuentasSaldoInverso ----
		this.menuItemCuentasSaldoInverso.setText("Cuentas con saldo inverzo a su naturaleza"); //NOI18N
		this.menu5.add(this.menuItemCuentasSaldoInverso);

		//---- menuItemCentrosSaldoInverso ----
		this.menuItemCentrosSaldoInverso.setText("Centros con saldo inverso a la naturaleza de la cuenta"); //NOI18N
		this.menu5.add(this.menuItemCentrosSaldoInverso);

		//---- menuItemElementosSaldoInverso ----
		this.menuItemElementosSaldoInverso.setText("Elementos con saldo inverso a la naturaleza de la cuenta"); //NOI18N
		this.menu5.add(this.menuItemElementosSaldoInverso);
	    }
	    this.menuBar1.add(this.menu5);

	    //======== menu6 ========
	    {
		this.menu6.setText("Agregaci\u00f3n"); //NOI18N

		//---- menuItem8 ----
		this.menuItem8.setText("Configuraci\u00f3n"); //NOI18N
		this.menu6.add(this.menuItem8);

		//---- menuItem9 ----
		this.menuItem9.setText("text"); //NOI18N
		this.menu6.add(this.menuItem9);

		//---- menuItem10 ----
		this.menuItem10.setText("text"); //NOI18N
		this.menu6.add(this.menuItem10);

		//---- menuItem11 ----
		this.menuItem11.setText("text"); //NOI18N
		this.menu6.add(this.menuItem11);

		//---- menuItem12 ----
		this.menuItem12.setText("text"); //NOI18N
		this.menu6.add(this.menuItem12);

		//---- menuItem13 ----
		this.menuItem13.setText("text"); //NOI18N
		this.menu6.add(this.menuItem13);
	    }
	    this.menuBar1.add(this.menu6);

	    //======== menu7 ========
	    {
		this.menu7.setText("Operaciones Autom\u00e1ticas"); //NOI18N
	    }
	    this.menuBar1.add(this.menu7);

	    //======== menu8 ========
	    {
		this.menu8.setText("Ayuda"); //NOI18N
	    }
	    this.menuBar1.add(this.menu8);
	}
	setJMenuBar(this.menuBar1);
	pack();
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    public static void main(String[] args) {

        AppPreferences.init();

        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("com.univsoftdev.econova.themes");
        UIManager.put("defaultFont", FontUtils.getCompositeFont(FlatRobotoFont.FAMILY, Font.PLAIN, 13));

        AppPreferences.setupLaf();

        EventQueue.invokeLater(() -> {

            if (SystemTray.isSupported()) {
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().getImage(MainFormApp.class.getClassLoader().getResource("com/univsoftdev/econova/Econova_Logo.png"));
                PopupMenu menu = new PopupMenu();
                MenuItem itemSalir = new MenuItem("Salir");
                itemSalir.addActionListener((ActionEvent e) -> {
                    System.exit(0);
                });
                MenuItem itemAcercaDe = new MenuItem("Acerca de Econova");
                itemAcercaDe.addActionListener((ActionEvent e) -> {
                    DialogUtils.createDialog(new About(), "Acerca de...", false);
                });
                MenuItem itemConfiguracion = new MenuItem("Configuración");
                MenuItem itemContabilidad = new MenuItem("Contabilidad");
                menu.add(itemConfiguracion);
                menu.add(itemContabilidad);

                Menu menuHerramientas = new Menu("Herramientas");
                MenuItem itemConnServidor = new MenuItem("Conexción al Servidor");
                itemConnServidor.addActionListener((ActionEvent e) -> {
                    new FormDatabaseConnection().setVisible(true);
                });

                MenuItem itemCambContras = new MenuItem("Cambiar Contraseña");
                MenuItem itemRestBaseDatos = new MenuItem("Restaurar Base de Datos");
                menuHerramientas.add(itemConnServidor);
                menuHerramientas.add(itemCambContras);
                menuHerramientas.add(itemRestBaseDatos);

                menu.addSeparator();
                menu.add(menuHerramientas);
                menu.add(itemAcercaDe);
                menu.add(itemSalir);

                TrayIcon trayIcon = new TrayIcon(image, Injector.get(AppConfig.class).getAppName(), menu);
                trayIcon.setImageAutoSize(true);

                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    log.error(e.getMessage());
                }
            }

            new MainFormApp().setVisible(true);
        });
    }
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItemCambiarUnidadContable;
    private JMenuItem menuItemCambiarFechaProcesamiento;
    private JMenuItem menuItemEstadoOperaciones;
    private JMenuItem menuItemImprimir;
    private JMenuItem menuItemExportar;
    private JMenuItem menuItemImportar;
    private JMenuItem menuItemConfiguracion;
    private JMenuItem menuItemSalir;
    private JMenu menu2;
    private JMenuItem menuItem14;
    private JMenu menu3;
    private JMenu menu4;
    private JMenuItem menuItem25;
    private JMenuItem menuItem26;
    private JMenuItem menuItem27;
    private JMenu menu5;
    private JMenuItem menuItemApertura;
    private JMenuItem menuItemMostrarInactivas;
    private JMenuItem menuItemFormato;
    private JMenuItem menuItemNivelParaAnalisis;
    private JMenuItem menuItemDefinicionIngresosEgresos;
    private JMenuItem menuItemGruposCuentas;
    private JMenu menuHistoria;
    private JMenuItem menuItemSubMayor;
    private JMenuItem menuItemSaldoCuentas;
    private JMenuItem menuItemSaldosTransitorios;
    private JMenuItem menuItemMayor;
    private JMenuItem menuItemSaldoPeriodos;
    private JMenuItem menuItemResumen;
    private JMenuItem menuItemCuentasControladas;
    private JMenuItem menuItemCuentasNatMixta;
    private JMenuItem menuItemCuentasSaldoInverso;
    private JMenuItem menuItemCentrosSaldoInverso;
    private JMenuItem menuItemElementosSaldoInverso;
    private JMenu menu6;
    private JMenuItem menuItem8;
    private JMenuItem menuItem9;
    private JMenuItem menuItem10;
    private JMenuItem menuItem11;
    private JMenuItem menuItem12;
    private JMenuItem menuItem13;
    private JMenu menu7;
    private JMenu menu8;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
