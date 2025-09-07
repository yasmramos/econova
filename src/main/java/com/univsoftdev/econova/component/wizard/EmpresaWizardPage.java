package com.univsoftdev.econova.component.wizard;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import com.univsoftdev.econova.config.model.Company;
import com.univsoftdev.econova.core.Validations;
import com.univsoftdev.econova.core.utils.DialogUtils;
import jakarta.inject.Singleton;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

@Singleton
public class EmpresaWizardPage extends WizardPage {

    private static final long serialVersionUID = 1L;
    List<Company> companys = new ArrayList<>();

    public EmpresaWizardPage() {
        super("Datos de la Empresa", "Intro");
        initComponents();

        txtCode.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Código");
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nombre");
        txtOrganismo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Organismo");
        txtTelefono.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Teléfono");
        txtDireccion.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Dirección");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "econova@gmail.com");

        tableEmpresas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedEmpresa();
                }
            }
        });

        // Configurar validaciones en tiempo real
        setupFieldValidation();
    }

    private void setupFieldValidation() {
        // Validar código único (opcional)
        txtCode.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateCodeUniqueness();
            }
        });
    }

    @Override
    public boolean onNext(WizardSettings settings) {
        if (tableEmpresas.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Debe añadir al menos una empresa.", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Guardar datos en settings
        settings.put("empresas", companys);

        try {
            final var email = txtEmail.getText().trim();
            if (!email.isEmpty()) {
                if (Validations.isValidEmail(email)) {
                    settings.put("empresa.email", email);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "El formato del correo electrónico no es válido",
                            "Error de validación",
                            JOptionPane.ERROR_MESSAGE);
                    txtEmail.requestFocus();
                    return false;
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al validar el correo electrónico: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean validateForm() {
        // Validar campos requeridos
        if (txtCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El código de la empresa es obligatorio",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
            txtCode.requestFocus();
            return false;
        }

        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre de la empresa es obligatorio",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return false;
        }

        // Validar longitud mínima
        if (txtCode.getText().trim().length() < 2) {
            JOptionPane.showMessageDialog(this,
                    "El código debe tener al menos 2 caracteres",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            txtCode.requestFocus();
            return false;
        }

        if (txtName.getText().trim().length() < 3) {
            JOptionPane.showMessageDialog(this,
                    "El nombre debe tener al menos 3 caracteres",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return false;
        }

        return true;
    }

    private void validateCodeUniqueness() {
        String currentCode = txtCode.getText().trim();
        if (!currentCode.isEmpty()) {
            DefaultTableModel model = (DefaultTableModel) tableEmpresas.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String existingCode = (String) model.getValueAt(i, 0);
                if (currentCode.equals(existingCode)) {
                    // Mostrar advertencia visual
                    txtCode.putClientProperty(FlatClientProperties.OUTLINE, "error");
                    return;
                }
            }
            // Limpiar advertencia si es único
            txtCode.putClientProperty(FlatClientProperties.OUTLINE, null);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        menuItemEliminar = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtOrganismo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableEmpresas = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();

        menuItemEliminar.setText("jMenuItem1");
        menuItemEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEliminarActionPerformed(evt);
            }
        });
        jPopupMenu1.add(menuItemEliminar);

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel1.setText("<html>Datos de la Empresa</html>");

        jLabel2.setText("Código");

        jLabel3.setText("Nombre");

        jLabel4.setText("Organismo");

        jLabel5.setText("Teléfono");

        jLabel6.setText("Dirección");

        jLabel7.setText("Correo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 329, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6)
                        .addComponent(jLabel2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7)
                            .addComponent(txtDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                            .addComponent(txtTelefono)
                            .addComponent(txtOrganismo)
                            .addComponent(txtName)
                            .addComponent(jLabel5)
                            .addComponent(txtCode)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel4)
                        .addComponent(jLabel3))
                    .addContainerGap(18, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 347, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(9, 9, 9)
                    .addComponent(jLabel2)
                    .addGap(7, 7, 7)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel3)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel4)
                    .addGap(7, 7, 7)
                    .addComponent(txtOrganismo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel5)
                    .addGap(12, 12, 12)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel7)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(18, Short.MAX_VALUE)))
        );

        tableEmpresas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "NOMBRE"
            }
        ));
        tableEmpresas.setColumnSelectionAllowed(true);
        tableEmpresas.setFillsViewportHeight(true);
        tableEmpresas.setShowGrid(true);
        jScrollPane1.setViewportView(tableEmpresas);
        tableEmpresas.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        btnAdd.setText("Añadir");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        jLabel8.setText("Puede configurar una o varias empresas según desee.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(btnAdd))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // Validar formulario antes de añadir
        if (!validateForm()) {
            return;
        }

        var model = (DefaultTableModel) tableEmpresas.getModel();

        // Verificar si el código ya existe
        String newCode = txtCode.getText().trim();
        for (int i = 0; i < model.getRowCount(); i++) {
            String existingCode = (String) model.getValueAt(i, 0);
            if (newCode.equals(existingCode)) {
                JOptionPane.showMessageDialog(this,
                        "Ya existe una empresa con el código: " + newCode,
                        "Código duplicado",
                        JOptionPane.WARNING_MESSAGE);
                txtCode.requestFocus();
                return;
            }
        }

        // Añadir empresa a la tabla
        model.addRow(new Object[]{
            txtCode.getText().trim(),
            txtName.getText().trim()
        });

        Company company = new Company();
        company.setCode(newCode);
        company.setName(txtName.getText().trim());
        company.setTelefono(txtTelefono.getText().trim());
        company.setAddress(txtDireccion.getText().trim());
        company.setEmail(txtEmail.getText().trim());
        companys.add(company);
        
        // Limpiar campos para la siguiente entrada
        clearFormFields();

        // Preguntar sobre unidades
        int showConfirmDialog = JOptionPane.showConfirmDialog(
                this,
                "¿La empresa tiene unidades organizativas?",
                "Configuración adicional",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (showConfirmDialog == JOptionPane.YES_OPTION) {
            DialogUtils.showModalDialog(this, new DialogNuevaUnidad(), "Nueva unidad");
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void menuItemEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemEliminarActionPerformed
        removeSelectedEmpresa();
    }//GEN-LAST:event_menuItemEliminarActionPerformed

    private void removeSelectedEmpresa() {
        int selectedRow = tableEmpresas.getSelectedRow();
        if (selectedRow >= 0) {
            int option = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea eliminar la empresa seleccionada?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                DefaultTableModel model = (DefaultTableModel) tableEmpresas.getModel();
                model.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione una empresa para eliminar",
                    "Ninguna selección",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editSelectedEmpresa() {
        int selectedRow = tableEmpresas.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) tableEmpresas.getModel();
            txtCode.setText((String) model.getValueAt(selectedRow, 0));
            txtName.setText((String) model.getValueAt(selectedRow, 1));

            // Eliminar la fila existente
            model.removeRow(selectedRow);
        }
    }

    private void clearFormFields() {
        txtCode.setText("");
        txtName.setText("");
        txtOrganismo.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtEmail.setText("");
        txtCode.putClientProperty(FlatClientProperties.OUTLINE, null);
        txtCode.requestFocus();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menuItemEliminar;
    private javax.swing.JTable tableEmpresas;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOrganismo;
    private javax.swing.JFormattedTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
