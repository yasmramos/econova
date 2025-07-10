package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.config.service.UsuarioService;
import com.univsoftdev.econova.config.model.User;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.DialogUtils;
import io.avaje.inject.BeanScope;
import java.awt.*;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormUsuarios extends Form {

    private static final long serialVersionUID = 4339277786555247095L;
    private BeanScope injector;
    private DefaultTableModel model;
    private UsuarioService usuarioService;

    public FormUsuarios() {
        initComponents();
        try {
            injector = AppContext.getInstance().getInjector();
            usuarioService = injector.get(UsuarioService.class);

            // Verificar modelo de tabla
            if (!(tableUsuarios.getModel() instanceof DefaultTableModel)) {
                throw new IllegalStateException("El modelo de la tabla no es DefaultTableModel");
            }

            DefaultTableModel modelTable = (DefaultTableModel) tableUsuarios.getModel();
            java.util.List<User> usuarios = usuarioService.findAll();

            // Verificar lista de usuarios
            if (usuarios != null) {
                for (User usuario : usuarios) {
                    // Verificar usuario no nulo
                    if (usuario != null) {
                        var adminSistema = usuario.isAdminSistema() ? "X" : "";
                        var adminEconomico = usuario.isAdminEconomico() ? "X" : "";
                        var isActivo = usuario.isActivo();

                        // Solo agregar si está activo
                        if (isActivo) {
                            modelTable.addRow(new Object[]{
                                usuario.getFullName() != null ? usuario.getFullName() : "",
                                usuario.getUserName() != null ? usuario.getUserName() : "",
                                "Econova",
                                adminSistema,
                                adminEconomico,
                                "Si"
                            });
                        }
                    }
                }
            }

        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void adicionarActionPerformed(ActionEvent e) {
        DialogUtils.showModalDialog(this, new FormNuevoUsuario(tableUsuarios), "Nuevo Usuario");
    }

    private void eliminar(ActionEvent e) {
        model = (DefaultTableModel) tableUsuarios.getModel();
        final int row = tableUsuarios.getSelectedRow();
        final int col = 1;
        Optional<User> usuario = usuarioService.findBy("identificador", model.getValueAt(row, col));
        if (usuario.isPresent()) {
            model.removeRow(row);
            usuarioService.delete(usuario.get());
            JOptionPane.showMessageDialog(null, "Se ha eliminado el usuario correctamente.");
        } else {
            JOptionPane.showMessageDialog(null, "No se ha podido eliminar el usuario.");
        }
    }

    private void modificar(ActionEvent e) {
        // TODO add your code here
    }

    private void activar(ActionEvent e) {
        // TODO add your code here
    }

    private void verTodos(ActionEvent e) {
        java.util.List<User> inactivos = usuarioService.findByInactivos();
        model = (DefaultTableModel) tableUsuarios.getModel();
        for (User usuario : inactivos) {
            model.addRow(new Object[]{
                usuario.getFullName() != null ? usuario.getFullName() : "",
                usuario.getUserName() != null ? usuario.getUserName() : "",
                "Econova",
                usuario.isAdminSistema() ? "X" : "",
                usuario.isAdminEconomico() ? "X" : "",
                "Si"
            });
        }
    }

    private void permisosConfiguracion(ActionEvent e) {
        DialogUtils.showModalDialog(this, new DialogPermisosConfiguracion(), "Permisos de Configuración");
    }

    private void permisosSimplificados(ActionEvent e) {
        // TODO add your code here
    }

    private void crearUsuarioAdministrador(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.scrollPane1 = new JScrollPane();
	this.tableUsuarios = new JTable();
	this.popupMenu1 = new JPopupMenu();
	this.menuItemAdicionar = new JMenuItem();
	this.menuItemEliminar = new JMenuItem();
	this.menuItemModificar = new JMenuItem();
	this.menuItemActivar = new JMenuItem();
	this.menuItemVerTodos = new JMenuItem();
	this.menuItemPermisosConfiguracion = new JMenuItem();
	this.menuItemPermisosSimplificados = new JMenuItem();
	this.menuItemCrearUsuarioAdministrador = new JMenuItem();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    this.label1.setText("Usuarios"); //NOI18N
	    this.panel1.add(this.label1, BorderLayout.WEST);
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== scrollPane1 ========
	{
	    this.scrollPane1.setComponentPopupMenu(this.popupMenu1);

	    //---- tableUsuarios ----
	    this.tableUsuarios.setModel(new DefaultTableModel(
		new Object[][] {
		},
		new String[] {
		    "Nombre", "Identificador", "Tipo", "Admin. Sistema", "Admin. Econ\u00f3mico", "Activo" //NOI18N
		}
	    ));
	    this.tableUsuarios.setComponentPopupMenu(this.popupMenu1);
	    this.scrollPane1.setViewportView(this.tableUsuarios);
	}
	add(this.scrollPane1, BorderLayout.CENTER);

	//======== popupMenu1 ========
	{

	    //---- menuItemAdicionar ----
	    this.menuItemAdicionar.setText("Adicionar"); //NOI18N
	    this.menuItemAdicionar.addActionListener(e -> adicionarActionPerformed(e));
	    this.popupMenu1.add(this.menuItemAdicionar);

	    //---- menuItemEliminar ----
	    this.menuItemEliminar.setText("Eliminar"); //NOI18N
	    this.menuItemEliminar.addActionListener(e -> eliminar(e));
	    this.popupMenu1.add(this.menuItemEliminar);

	    //---- menuItemModificar ----
	    this.menuItemModificar.setText("Modificar"); //NOI18N
	    this.menuItemModificar.addActionListener(e -> modificar(e));
	    this.popupMenu1.add(this.menuItemModificar);

	    //---- menuItemActivar ----
	    this.menuItemActivar.setText("Activar"); //NOI18N
	    this.menuItemActivar.addActionListener(e -> activar(e));
	    this.popupMenu1.add(this.menuItemActivar);
	    this.popupMenu1.addSeparator();

	    //---- menuItemVerTodos ----
	    this.menuItemVerTodos.setText("Ver todos"); //NOI18N
	    this.menuItemVerTodos.addActionListener(e -> verTodos(e));
	    this.popupMenu1.add(this.menuItemVerTodos);

	    //---- menuItemPermisosConfiguracion ----
	    this.menuItemPermisosConfiguracion.setText("Permisos de Configuraci\u00f3n"); //NOI18N
	    this.menuItemPermisosConfiguracion.addActionListener(e -> permisosConfiguracion(e));
	    this.popupMenu1.add(this.menuItemPermisosConfiguracion);

	    //---- menuItemPermisosSimplificados ----
	    this.menuItemPermisosSimplificados.setText("Permisos Simplificados"); //NOI18N
	    this.menuItemPermisosSimplificados.addActionListener(e -> permisosSimplificados(e));
	    this.popupMenu1.add(this.menuItemPermisosSimplificados);

	    //---- menuItemCrearUsuarioAdministrador ----
	    this.menuItemCrearUsuarioAdministrador.setText("Crear Usuario Administrador "); //NOI18N
	    this.menuItemCrearUsuarioAdministrador.addActionListener(e -> crearUsuarioAdministrador(e));
	    this.popupMenu1.add(this.menuItemCrearUsuarioAdministrador);
	}
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable tableUsuarios;
    private JPopupMenu popupMenu1;
    private JMenuItem menuItemAdicionar;
    private JMenuItem menuItemEliminar;
    private JMenuItem menuItemModificar;
    private JMenuItem menuItemActivar;
    private JMenuItem menuItemVerTodos;
    private JMenuItem menuItemPermisosConfiguracion;
    private JMenuItem menuItemPermisosSimplificados;
    private JMenuItem menuItemCrearUsuarioAdministrador;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
