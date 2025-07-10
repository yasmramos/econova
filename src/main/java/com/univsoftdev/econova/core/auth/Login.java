package com.univsoftdev.econova.core.auth;

import com.formdev.flatlaf.FlatClientProperties;
import com.univsoftdev.econova.EconovaDrawerBuilder;
import com.univsoftdev.econova.ModelUser;
import com.univsoftdev.econova.config.view.FormSeleccionUnidad;
import com.univsoftdev.econova.core.component.LabelButton;
import net.miginfocom.swing.MigLayout;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.system.FormManager;
import com.univsoftdev.econova.core.utils.DialogUtils;
import javax.swing.*;
import java.awt.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import raven.modal.ModalDialog;
import raven.modal.component.DropShadowBorder;
import raven.modal.component.SimpleModalBorder;

@Slf4j
public class Login extends Form {

    private static final long serialVersionUID = -4621709978240314840L;

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

        JPanel loginContent = new JPanel(new MigLayout("fillx,wrap,insets 35 35 25 35", "[fill,300]"));

        JLabel lbTitle = new JLabel("Welcome back!");
        JLabel lbDescription = new JLabel("Please sign in to access your account");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold +12;");

        loginContent.add(lbTitle);
        loginContent.add(lbDescription);

        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        //JCheckBox chRememberMe = new JCheckBox("Remember Me");
        JButton cmdLogin = new JButton("Iniciar Seción") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };

        // style
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su nombre de usuario.");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su contraseña");

        panelLogin.putClientProperty(FlatClientProperties.STYLE, ""
                + "[dark]background:tint($Panel.background,1%);");

        loginContent.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null;");

        txtUsername.putClientProperty(FlatClientProperties.STYLE, ""
                + "margin:4,10,4,10;"
                + "arc:12;");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, ""
                + "margin:4,10,4,10;"
                + "arc:12;"
                + "showRevealButton:true;");

        cmdLogin.putClientProperty(FlatClientProperties.STYLE, ""
                + "margin:4,10,4,10;"
                + "arc:12;");

        loginContent.add(new JLabel("Nombre de Usuario"), "gapy 25");
        loginContent.add(txtUsername);

        loginContent.add(new JLabel("Contraseña"), "gapy 10");
        loginContent.add(txtPassword);
        //loginContent.add(chRememberMe);
        loginContent.add(cmdLogin, "gapy 20");
        // loginContent.add(createInfo());

        panelLogin.add(loginContent);
        add(panelLogin);

        // event
        cmdLogin.addActionListener(e -> {
            String userName = txtUsername.getText();
            String password = String.valueOf(txtPassword.getPassword());
            ModelUser user = getUser(userName, password);
            EconovaDrawerBuilder.getInstance().setUser(user);
            FormManager.login();

            SwingUtilities.invokeLater(() -> {
                JFrame mainFrame = FormManager.getFrame();
                if (mainFrame != null) {
                    DialogUtils.showModalDialog(mainFrame, new FormSeleccionUnidad(), "Seleccione la Unidad");
                }
            });

        });
    }

    private JPanel createInfo() {
        JPanel panelInfo = new JPanel(new MigLayout("wrap,al center", "[center]"));
        panelInfo.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null;");

        panelInfo.add(new JLabel("Don't remember your account details?"));
        panelInfo.add(new JLabel("Contact us at"), "split 2");
        LabelButton lbLink = new LabelButton("help@info.com");

        panelInfo.add(lbLink);

        // event
        lbLink.addOnClick(e -> {

        });
        return panelInfo;
    }

    private void applyShadowBorder(JPanel panel) {
        if (panel != null) {
            panel.setBorder(new DropShadowBorder(new Insets(5, 8, 12, 8), 1, 25));
        }
    }

    private ModelUser getUser(String user, String password) {
        try {
            Subject currentUser = SecurityUtils.getSubject();
            if (!currentUser.isAuthenticated()) {
                UsernamePasswordToken token = new UsernamePasswordToken(user, password);
                try {
                    currentUser.login(token);
                } catch (AuthenticationException ex) {
                    log.error("No se pudo iniciar seción: ", ex);
                }
            }
        } catch (UnknownAccountException e) {
            ModalDialog.showModal(this, new SimpleModalBorder(
                    new JLabel("¡No se pudo iniciar seción:  " + e.getMessage()),
                    "Inicio de Sesión Fallido",
                    SimpleModalBorder.OK_OPTION,
                    (controller, action) -> {
                        if (action == SimpleModalBorder.OK_OPTION) {
                            FormManager.login(); // Continuar al dashboard  
                        }
                    }
            ));
        }
        // just testing.
        // input any user and password is admin by default
        // user='staff' password='123' if we want to test validation menu for role staff
        if (user.equals("staff") && password.equals("123")) {
            return new ModelUser("Justin White", "justinwhite@gmail.com", ModelUser.Role.STAFF);
        }
        return new ModelUser("Ra Ven", "raven@gmail.com", ModelUser.Role.ADMIN);
    }
}
