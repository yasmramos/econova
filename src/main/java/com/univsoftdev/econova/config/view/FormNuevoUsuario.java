package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.security.argon2.Argon2PasswordHasher;
import com.univsoftdev.econova.config.service.UserService;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.Injector;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

public class FormNuevoUsuario extends Modal {
    
    private static final long serialVersionUID = 1191104289857202935L;
    
    private UserService userService;
    private final JTable table;
    
    public FormNuevoUsuario(JTable table) {
        initComponents();
        this.userService = Injector.get(UserService.class);
        this.table = table;
    }
    
    private void aceptarActionPerformed(ActionEvent e) {
        final String nombre = textFieldNombre.getText().trim();
        final String identificador = textFieldIdentificador.getText().trim();
        final char[] password = passwordFieldContrasenna.getPassword();
        final char[] passwordConfirmar = passwordFieldConfirmarContrasenna.getPassword();

        // First check if passwords match
        if (!Arrays.equals(password, passwordConfirmar)) {
            JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden.", "Información", JOptionPane.INFORMATION_MESSAGE);
            // Clear password fields for security
            Arrays.fill(password, '\0');
            Arrays.fill(passwordConfirmar, '\0');
            return; // Exit the method
        }

        // Now hash the password properly
        final Argon2PasswordHasher passwordHasher = new Argon2PasswordHasher();
        try {
            
            final String hashedPassword = passwordHasher.hash(password);

            // Create and save user
            final User usuario = new User();
            usuario.setFullName(nombre);
            usuario.setUserName(identificador);
            usuario.setPassword(hashedPassword); // Store the hashed password, not the plain one
            usuario.setActive(true);
            userService.save(usuario);
            
            var model = (DefaultTableModel) table.getModel();
            model.addRow(new Object[]{
                usuario.getFullName(),
                usuario.getUserName(),
                "Econova",
                usuario.isAdminSistema() ? "X" : "",
                usuario.isAdminEconomico() ? "X" : "",
                usuario.isActive()
            });
            
            JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
        } finally {
            // Always clear sensitive data from memory
            Arrays.fill(password, '\0');
            Arrays.fill(passwordConfirmar, '\0');
        }
    }
    
    private void textFieldNombreFocusLost(FocusEvent e) {
        final String nombre = textFieldNombre.getText().trim();
        if (nombre == null || nombre.isEmpty() || nombre.isBlank()) {
            buttonAceptar.setEnabled(false);
        } else {
            if (nombre.contains(" ")) {
                final String[] split = nombre.split(" ");
                final String identificador = split[0] + "." + split[1];
                textFieldIdentificador.setText(identificador);
            } else {
                textFieldIdentificador.setText(nombre);
            }
            buttonAceptar.setEnabled(true);
        }
    }
    
    private void cancelar(ActionEvent e) {
        ModalDialog.closeModal(getId());
    }
    
    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.label1 = new JLabel();
	this.label2 = new JLabel();
	this.label3 = new JLabel();
	this.label4 = new JLabel();
	this.passwordFieldContrasenna = new JPasswordField();
	this.label5 = new JLabel();
	this.passwordFieldConfirmarContrasenna = new JPasswordField();
	this.textFieldNombre = new JTextField();
	this.textFieldIdentificador = new JTextField();
	this.buttonCancelar = new JButton();
	this.buttonAceptar = new JButton();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));

	//---- label1 ----
	this.label1.setText("Nombre"); //NOI18N

	//---- label2 ----
	this.label2.setText("Identificador"); //NOI18N

	//---- label3 ----
	this.label3.setText("Seguridad"); //NOI18N

	//---- label4 ----
	this.label4.setText("Contrase\u00f1a"); //NOI18N

	//---- label5 ----
	this.label5.setText("Confirmar Contrase\u00f1a"); //NOI18N

	//---- textFieldNombre ----
	this.textFieldNombre.addFocusListener(new FocusAdapter() {
	    @Override
	    public void focusLost(FocusEvent e) {
		textFieldNombreFocusLost(e);
	    }
	});

	//---- buttonCancelar ----
	this.buttonCancelar.setText("Cancelar"); //NOI18N
	this.buttonCancelar.addActionListener(e -> cancelar(e));

	//---- buttonAceptar ----
	this.buttonAceptar.setText("Aceptar"); //NOI18N
	this.buttonAceptar.setEnabled(false);
	this.buttonAceptar.addActionListener(e -> aceptarActionPerformed(e));

	GroupLayout layout = new GroupLayout(this);
	setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
			    .addGap(23, 23, 23)
			    .addGroup(layout.createParallelGroup()
				.addComponent(this.label3)
				.addComponent(this.label2)
				.addComponent(this.label1)))
			.addGroup(layout.createSequentialGroup()
			    .addGap(61, 61, 61)
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
				    .addComponent(this.buttonAceptar)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(this.buttonCancelar))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				    .addGroup(layout.createSequentialGroup()
					.addComponent(this.label5)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(this.passwordFieldConfirmarContrasenna))
				    .addGroup(layout.createSequentialGroup()
					.addComponent(this.label4)
					.addGroup(layout.createParallelGroup()
					    .addGroup(layout.createSequentialGroup()
						.addGap(63, 63, 63)
						.addComponent(this.passwordFieldContrasenna, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE))
					    .addGroup(layout.createSequentialGroup()
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
						    .addComponent(this.textFieldNombre, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
						    .addComponent(this.textFieldIdentificador, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)))))))))
		    .addContainerGap(25, Short.MAX_VALUE))
	);
	layout.setVerticalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGap(20, 20, 20)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.label1)
			.addComponent(this.textFieldNombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.label2)
			.addComponent(this.textFieldIdentificador, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGap(47, 47, 47)
		    .addComponent(this.label3)
		    .addGap(18, 18, 18)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.label4)
			.addComponent(this.passwordFieldContrasenna, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGap(18, 18, 18)
		    .addGroup(layout.createParallelGroup()
			.addComponent(this.label5)
			.addComponent(this.passwordFieldConfirmarContrasenna, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.buttonCancelar)
			.addComponent(this.buttonAceptar))
		    .addContainerGap())
	);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JPasswordField passwordFieldContrasenna;
    private JLabel label5;
    private JPasswordField passwordFieldConfirmarContrasenna;
    private JTextField textFieldNombre;
    private JTextField textFieldIdentificador;
    private JButton buttonCancelar;
    private JButton buttonAceptar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
