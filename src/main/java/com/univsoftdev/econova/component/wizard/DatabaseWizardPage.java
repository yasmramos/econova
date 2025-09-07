package com.univsoftdev.econova.component.wizard;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import com.univsoftdev.econova.config.service.ConfigService;
import com.univsoftdev.econova.core.Injector;
import com.univsoftdev.econova.core.swing.SwingUtils;
import com.univsoftdev.econova.db.postgres.PostgreSQLConnection;
import com.univsoftdev.econova.db.postgres.PostgreSQLDatabaseLister;
import java.awt.Color;
import java.awt.HeadlessException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import raven.modal.Toast;
import raven.modal.toast.ToastPromise;

@Slf4j
public class DatabaseWizardPage extends WizardPage {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_DB_NAME = "econova";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";
    private static final String DEFAULT_SERVER = "localhost";
    private static final String DEFAULT_PORT = "5432";

    private PostgreSQLConnection pgConnection;

    private final ConfigService configService;

    public DatabaseWizardPage() {
        super("Configuración Base de Datos", "");
        initComponents();
        configService = Injector.get(ConfigService.class);
        setDefaultValues();
    }

    private void setDefaultValues() {
        txtDatabaseName.setText(DEFAULT_DB_NAME);
        txtNombreUsuarioAdmin.setText(DEFAULT_USER);
        txtPassword.setText(DEFAULT_PASSWORD);
        txtServidor.setText(DEFAULT_SERVER);
        txtPuerto.setText(DEFAULT_PORT);
    }

    @Override
    public boolean onNext(WizardSettings settings) {

        if (!validateFields()) {
            return false;
        }

        settings.put("econova.database.name", txtDatabaseName.getText().trim());
        settings.put("econova.database.admin.username", txtNombreUsuarioAdmin.getText().trim());
        settings.put("econova.database.server", txtServidor.getText().trim());
        settings.put("econova.database.port", txtPuerto.getText().trim());

        // Manejo seguro de la contraseña
        char[] password = txtPassword.getPassword();
        settings.put("econova.database.admin.password", new String(password));

        WizardDataOutput.saveObject(settings);

        Arrays.fill(password, '\0');
        return true;
    }

    private boolean validateFields() {
        // Validar servidor
        if (txtServidor.getText().trim().isEmpty()) {
            showError("El servidor es obligatorio");
            return false;
        }

        // Validar puerto
        try {
            int port = Integer.parseInt(txtPuerto.getText().trim());
            if (port < 1 || port > 65535) {
                showError("Puerto inválido (1-65535)");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("El puerto debe ser numérico");
            return false;
        }

        // Validar usuario
        if (txtNombreUsuarioAdmin.getText().trim().isEmpty()) {
            showError("El usuario es obligatorio");
            return false;
        }

        // Validar nombre de base de datos
        if (txtDatabaseName.getText().trim().isEmpty()) {
            showError("El nombre de la base de datos es obligatorio");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setForeground(Color.RED);
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblMessage.setForeground(new Color(0, 128, 0)); // Verde oscuro
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDatabaseName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtNombreUsuarioAdmin = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtServidor = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtPuerto = new javax.swing.JTextField();
        btnProbarConexion = new javax.swing.JButton();
        lblMessage = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel1.setText("<html>Configuración Base de Datos</html>");

        jLabel2.setText("Nombre base de datos");

        txtDatabaseName.setToolTipText("Si no se especifica se usara la base de datos por defecto");
        txtDatabaseName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtDatabaseNamePropertyChange(evt);
            }
        });

        jLabel3.setText("Nombre usuario administrador");

        jLabel4.setText("Contraseña");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Parámetros de acceso"));

        jLabel5.setText("Servidor");

        jLabel7.setText("Puerto");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5)
                    .addComponent(txtServidor, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                    .addComponent(txtPuerto))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtServidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        btnProbarConexion.setBackground(new java.awt.Color(51, 51, 255));
        btnProbarConexion.setForeground(new java.awt.Color(255, 255, 255));
        btnProbarConexion.setText("Probar Conexión");
        btnProbarConexion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProbarConexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProbarConexionActionPerformed(evt);
            }
        });

        lblMessage.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtDatabaseName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtNombreUsuarioAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnProbarConexion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addComponent(txtDatabaseName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(10, 10, 10)
                .addComponent(txtNombreUsuarioAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProbarConexion)
                    .addComponent(lblMessage))
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnProbarConexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProbarConexionActionPerformed

        if (!validateFields()) {
            return;
        }

        Toast.showPromise(this, "Conectando a la base de datos...", new ToastPromise(UUID.randomUUID().toString()) {
            @Override
            public void execute(ToastPromise.PromiseCallback pc) {
                char[] password = null;
                try {
                    // Obtener parámetros de conexión
                    String server = txtServidor.getText().trim();
                    String port = txtPuerto.getText().trim();
                    String username = txtNombreUsuarioAdmin.getText().trim();
                    password = txtPassword.getPassword();
                    String dbName = txtDatabaseName.getText().trim();
                    String defaultDb = "postgres"; // Para operaciones administrativas

                    // Crear conexión administrativa
                    pc.update("Probando conexión al servidor...");
                    pgConnection = new PostgreSQLConnection(server, port, username, new String(password), defaultDb);

                    // Probar conexión básica
                    if (!pgConnection.testConnection()) {
                        pc.done(Toast.Type.ERROR, "No se pudo conectar al servidor");
                        return;
                    }

                    // Verificar si la base de datos existe
                    pc.update("Verificando base de datos...");
                    boolean dbExists = pgConnection.databaseExists(dbName);

                    if (!dbExists) {
                        pc.update("La base de datos no existe, preguntando al usuario...");
                        int option = JOptionPane.showConfirmDialog(DatabaseWizardPage.this,
                                "La base de datos '" + dbName + "' no existe. ¿Desea crearla?",
                                "Confirmación",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);

                        if (option == JOptionPane.YES_OPTION) {
                            pc.update("Creando base de datos...");
                            pgConnection.createDatabase(dbName);
                        } else {
                            pc.done(Toast.Type.INFO, "Operación cancelada por el usuario");
                            return;
                        }
                    }

                    // Cambiar a la nueva base de datos
                    pc.update("Configurando conexión...");
                    pgConnection.switchDatabase(dbName);
                    pgConnection.configureConnectionPool();

                    // Probar conexión final
                    if (pgConnection.testConnection(dbName)) {
                        pc.done(Toast.Type.SUCCESS, "Conexión establecida correctamente");
                        showSuccess("✓ Conexión establecida correctamente");
                    } else {
                        pc.done(Toast.Type.ERROR, "No se pudo conectar a la base de datos");
                    }
                } catch (HeadlessException | SQLException e) {
                    log.error("Error en prueba de conexión", e);
                    pc.done(Toast.Type.ERROR, "Error: " + e.getMessage());
                    showError("✗ Error de conexión: " + e.getMessage());
                } finally {
                    Arrays.fill(password, '\0');
                }
            }
        });
    }//GEN-LAST:event_btnProbarConexionActionPerformed

    private void txtDatabaseNamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtDatabaseNamePropertyChange
        String propertyName = evt.getPropertyName();
        if (propertyName.equals("text")) {
            String value = evt.getNewValue().toString().trim();
            if (value.isEmpty()) {
                txtDatabaseName.putClientProperty(FlatClientProperties.OUTLINE, "error");
            } else {
                txtDatabaseName.putClientProperty(FlatClientProperties.OUTLINE, null);
            }
        }
    }//GEN-LAST:event_txtDatabaseNamePropertyChange

    public Optional<String> getDatabases() {
        final var lister = new PostgreSQLDatabaseLister(
                SwingUtils.getValue(txtServidor),
                Integer.getInteger(SwingUtils.getValue(txtPuerto)),
                SwingUtils.getValue(txtNombreUsuarioAdmin),
                new String(txtPassword.getPassword()));

        var databseName = SwingUtils.getValue(txtDatabaseName);

        final Optional<String> filter = lister.getAvailableDatabases()
                .stream()
                .filter(
                        d -> d.equals(databseName)
                ).findFirst();
        return filter;
    }

    public PostgreSQLConnection getConnection() {
        return pgConnection;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProbarConexion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JTextField txtDatabaseName;
    private javax.swing.JTextField txtNombreUsuarioAdmin;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPuerto;
    private javax.swing.JTextField txtServidor;
    // End of variables declaration//GEN-END:variables
}
