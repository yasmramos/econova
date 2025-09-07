package com.univsoftdev.econova.core.auth;

import com.formdev.flatlaf.FlatClientProperties;
import com.univsoftdev.econova.EconovaDrawerBuilder;
import com.univsoftdev.econova.config.view.FormSeleccionEmpresa;
import com.univsoftdev.econova.core.component.LabelButton;
import net.miginfocom.swing.MigLayout;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.system.FormManager;
import com.univsoftdev.econova.core.utils.DialogUtils;
import com.univsoftdev.econova.security.shiro.ShiroContext;
import com.univsoftdev.econova.security.shiro.ShiroUserPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import raven.modal.component.DropShadowBorder;

@Slf4j
public class Login extends Form {

    private static final long serialVersionUID = -4621709978240314840L;
    private static final String DEFAULT_TENANT = "accounting";
    private JTextField txtUsername;      // Referencia al campo de usuario
    private JPasswordField txtPassword;  // Referencia al campo de contraseña
    private JButton cmdLogin;            // Referencia al botón de login
    private JPanel loginContent;         // Referencia al panel de contenido

    public Login() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        createLogin();
    }

    private void createLogin() {
        JPanel panelLogin = new JPanel(new BorderLayout()) {
            @Override
            public void updateUI() {
                super.updateUI();
                applyShadowBorder(this);
            }
        };
        panelLogin.setOpaque(false);
        applyShadowBorder(panelLogin);

        loginContent = new JPanel(new MigLayout("fillx,wrap,insets 35 35 25 35", "[fill,300]"));

        // Título y descripción
        JLabel lbTitle = createTitleLabel();
        JLabel lbDescription = createDescriptionLabel();
        loginContent.add(lbTitle);
        loginContent.add(lbDescription);

        // Componentes de entrada
        txtUsername = createUsernameField();
        txtPassword = createPasswordField();
        cmdLogin = createLoginButton();

        // Agregar componentes
        loginContent.add(new JLabel("Nombre de Usuario"), "gapy 25");
        loginContent.add(txtUsername);
        loginContent.add(new JLabel("Contraseña"), "gapy 10");
        loginContent.add(txtPassword);
        loginContent.add(cmdLogin, "gapy 20");

        panelLogin.add(loginContent);
        add(panelLogin);

        // Configurar eventos
        setupEventHandlers(txtUsername, txtPassword, cmdLogin);
    }

    private JLabel createTitleLabel() {
        JLabel lbTitle = new JLabel("Bienvenido");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +14;");
        return lbTitle;
    }

    private JLabel createDescriptionLabel() {
        JLabel lbDescription = new JLabel("Por favor, inicie sesión para acceder a su cuenta");
        lbDescription.putClientProperty(FlatClientProperties.STYLE, "foreground:$Text.tertiary;");
        return lbDescription;
    }

    private JTextField createUsernameField() {
        JTextField txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su nombre de usuario");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;arc:12;");
        return txtUsername;
    }

    private JPasswordField createPasswordField() {
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su contraseña");
        txtPassword.putClientProperty(FlatClientProperties.STYLE,
                "margin:4,10,4,10;arc:12;showRevealButton:true;");
        return txtPassword;
    }

    private JButton createLoginButton() {
        JButton cmdLogin = new JButton("Iniciar Sesión") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;arc:12;");
        return cmdLogin;
    }

    private void setupEventHandlers(JTextField txtUsername, JPasswordField txtPassword, JButton cmdLogin) {
        // Evento para el botón de login
        cmdLogin.addActionListener(e -> performLogin(txtUsername, txtPassword));

        // Evento para presionar Enter en el campo de contraseña
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin(txtUsername, txtPassword);
                }
            }
        });

        // Evento para presionar Enter en el campo de usuario
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!txtUsername.getText().trim().isEmpty()) {
                        txtPassword.requestFocusInWindow();
                    }
                }
            }
        });

        // Enfocar el campo de usuario al iniciar
        SwingUtilities.invokeLater(() -> txtUsername.requestFocusInWindow());
    }

    private void performLogin(JTextField txtUsername, JPasswordField txtPassword) {
        String userName = txtUsername.getText().trim();
        char[] password = txtPassword.getPassword();

        // Deshabilitar UI durante el proceso
        setUIEnabled(false);

        try {
            // Validación inicial
            if (!validateInput(userName, password)) {
                return;
            }

            Subject currentUser = login(userName, password);

            if (currentUser != null && currentUser.isAuthenticated()) {
                handleSuccessfulLogin(currentUser);
            } else {
                showErrorMessage("Error de autenticación", "No se pudo autenticar el usuario");
            }

        } catch (UnknownAccountException e) {
            log.warn("Intento de login con usuario desconocido: {}", userName);
            showErrorMessage("Error de autenticación", "Usuario o contraseña incorrectos");

        } catch (IncorrectCredentialsException e) {
            log.warn("Credenciales incorrectas para usuario: {}", userName);
            showErrorMessage("Error de autenticación", "Usuario o contraseña incorrectos");
            txtPassword.requestFocus();

        } catch (LockedAccountException e) {
            log.warn("Cuenta bloqueada para usuario: {}", userName);
            showErrorMessage("Error de autenticación", "La cuenta está bloqueada. Contacte al administrador");

        } catch (ExcessiveAttemptsException e) {
            log.warn("Demasiados intentos fallidos para usuario: {}", userName);
            showErrorMessage("Error de autenticación", "Cuenta bloqueada temporalmente por seguridad");

        } catch (AuthenticationException e) {
            log.error("Error de autenticación para usuario: {}", userName, e);
            String message = StringUtils.isNotBlank(e.getMessage())
                    ? e.getMessage() : "Error en la autenticación";
            showErrorMessage("Error de autenticación", message);

        } catch (Exception e) {
            log.error("Error inesperado durante el login para usuario: {}", userName, e);
            showErrorMessage("Error", "Ocurrió un error inesperado. Por favor intente nuevamente.");
        } finally {
            // Limpieza segura de la contraseña
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            txtPassword.setText("");
            txtPassword.requestFocus();

            // Rehabilitar UI
            setUIEnabled(true);
        }
    }

    private void setUIEnabled(boolean enabled) {
        if (SwingUtilities.isEventDispatchThread()) {
            setUIEnabledInternal(enabled);
        } else {
            SwingUtilities.invokeLater(() -> setUIEnabledInternal(enabled));
        }
    }

    private void setUIEnabledInternal(boolean enabled) {
        try {
            // Deshabilitar/habilitar componentes de entrada
            if (txtUsername != null) {
                txtUsername.setEnabled(enabled);
                txtUsername.setEditable(enabled);
            }

            if (txtPassword != null) {
                txtPassword.setEnabled(enabled);
                txtPassword.setEditable(enabled);
            }

            if (cmdLogin != null) {
                cmdLogin.setEnabled(enabled);

                // Cambiar texto del botón para indicar proceso
                if (enabled) {
                    cmdLogin.setText("Iniciar Sesión");
                    cmdLogin.putClientProperty(FlatClientProperties.BUTTON_TYPE, null);
                } else {
                    cmdLogin.setText("Autenticando...");
                    cmdLogin.putClientProperty(FlatClientProperties.BUTTON_TYPE,
                            FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
                }
            }

            // Cambiar cursor del frame principal
            JFrame frame = FormManager.getFrame();
            if (frame != null) {
                frame.setCursor(enabled ? Cursor.getDefaultCursor()
                        : Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            // Repintar para asegurar cambios visuales
            if (loginContent != null) {
                loginContent.repaint();
            }

        } catch (Exception e) {
            log.warn("Error al {} la interfaz de login", enabled ? "habilitar" : "deshabilitar", e);
        }
    }

    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );

        // Opcional: Agregar sonido de error
        Toolkit.getDefaultToolkit().beep();
    }

    private boolean validateInput(String username, char[] password) {
        if (username == null || username.trim().isEmpty()) {
            showErrorMessage("Validación", "Por favor ingrese su nombre de usuario");
            return false;
        }

        if (password == null || password.length == 0) {
            showErrorMessage("Validación", "Por favor ingrese su contraseña");
            return false;
        }

        // Validación de longitud mínima
        if (password.length < 3) {
            showErrorMessage("Validación", "La contraseña debe tener al menos 3 caracteres");
            return false;
        }

        // Validación de longitud máxima (opcional, por seguridad)
        if (password.length > 128) {
            showErrorMessage("Validación", "La contraseña es demasiado larga");
            return false;
        }

        return true;
    }

    private void handleSuccessfulLogin(Subject currentUser) {
        try {
            var shiroPrincipal = ShiroContext.getCurrentUser();
            if (shiroPrincipal == null) {
                throw new AuthenticationException("No se pudo obtener la información del usuario autenticado");
            }

            var user = shiroPrincipal.getUser();
            if (user == null) {
                throw new AuthenticationException("Usuario no encontrado en la base de datos");
            }

            // Configurar usuario en la aplicación
            EconovaDrawerBuilder.getInstance().setUser(user);

            log.info("Autenticación exitosa para: {} (ID: {})", user.getUserName(), user.getId());

            // Mostrar siguiente formulario
            SwingUtilities.invokeLater(() -> {
                try {
                    FormManager.login();
                    showCompanySelectionDialog();
                } catch (Exception e) {
                    log.error("Error mostrando diálogo de selección de empresa", e);
                    showErrorMessage("Error", "Error al cargar la interfaz principal");
                    // Desloguear en caso de error crítico
                    SecurityUtils.getSubject().logout();
                }
            });

        } catch (AuthenticationException e) {
            log.error("Error durante el proceso post-login", e);
            showErrorMessage("Error", "Error al inicializar la sesión del usuario: " + e.getMessage());
            // Desloguear en caso de error
            try {
                currentUser.logout();
            } catch (Exception logoutEx) {
                log.warn("Error al desloguear usuario después de fallo post-login", logoutEx);
            }
        } catch (Exception e) {
            log.error("Error inesperado durante el proceso post-login", e);
            showErrorMessage("Error", "Error inesperado al inicializar la sesión");
            try {
                currentUser.logout();
            } catch (Exception logoutEx) {
                log.warn("Error al desloguear usuario después de error inesperado", logoutEx);
            }
        }
    }

    private void showCompanySelectionDialog() {
        JFrame mainFrame = FormManager.getFrame();
        if (mainFrame != null) {
            DialogUtils.showModalDialog(mainFrame,
                    new FormSeleccionEmpresa(),
                    "Seleccione la Empresa");
        } else {
            log.warn("No se pudo obtener el frame principal para mostrar selección de empresa");
        }
    }

    private Subject login(String username, char[] password) throws AuthenticationException {
        Objects.requireNonNull(username, "El nombre de usuario no puede ser null");
        Objects.requireNonNull(password, "La contraseña no puede ser null");

        // Validación básica adicional
        if (username.trim().isEmpty()) {
            throw new AuthenticationException("El nombre de usuario no puede estar vacío");
        }
        if (password.length == 0) {
            throw new AuthenticationException("La contraseña no puede estar vacía");
        }

        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(false);

        log.debug("Intentando login para usuario: {}", username);
        currentUser.login(token);

        // Esta verificación es redundante ya que si login() no lanza excepción, 
        // el usuario está autenticado, pero está bien mantenerla por claridad
        if (!currentUser.isAuthenticated()) {
            throw new AuthenticationException("La autenticación no fue exitosa");
        }

        log.info("Autenticación exitosa para usuario: {} (ID: {})",
                username,
                currentUser.getPrincipal() != null
                ? ((ShiroUserPrincipal) currentUser.getPrincipal()).getUser().getId() : "unknown");

        return currentUser;
    }

    private JPanel createInfo() {
        JPanel panelInfo = new JPanel(new MigLayout("wrap,al center", "[center]"));
        panelInfo.putClientProperty(FlatClientProperties.STYLE, "background:null;");

        panelInfo.add(new JLabel("¿No recuerda sus datos de acceso?"));
        panelInfo.add(new JLabel("Contáctenos en"), "split 2");
        LabelButton lbLink = new LabelButton("help@info.com");

        panelInfo.add(lbLink);

        // Evento para el enlace
        lbLink.addOnClick(e -> {
            // Implementar acción de contacto
            log.info("Usuario solicitó ayuda de contacto");
        });

        return panelInfo;
    }

    private void applyShadowBorder(JPanel panel) {
        if (panel != null) {
            panel.setBorder(new DropShadowBorder(new Insets(5, 8, 12, 8), 1, 25));
        }
    }
}
