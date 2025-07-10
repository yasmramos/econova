package com.univsoftdev.econova.contabilidad.viewsfx;

import com.univsoftdev.econova.contabilidad.dto.AsientoDto;
import com.univsoftdev.econova.contabilidad.dto.CuentaDto;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;

public class ComprobanteController implements Initializable {

    @FXML
    public TableColumn columnSbcta;
    @FXML
    public TableColumn columnSctro;
    @FXML
    public TableColumn columnAnal;
    @FXML
    public TableColumn columnEpig;
    @FXML
    public TableColumn columnDebito;
    @FXML
    public TableColumn columnCredito;
    @FXML
    public Pane paneComprobante;
    @FXML
    private DatePicker fecha;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TableView<CuentaDto> tableCuentas;
    @FXML
    private DialogPane dialogComprobante;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnValidar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnVerReg;
    @FXML
    private Button btnImprimir;
    @FXML
    private TableColumn<CuentaDto, String> columnCta;

    @FXML
    private void add(ActionEvent evt) {
        tableCuentas.getItems().add(new CuentaDto());
    }

    @FXML
    private @NotNull TableColumn<CuentaDto, String> createComboBoxColumn(String title, String property, ObservableList<String> items) {
        TableColumn<CuentaDto, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(tc -> new TableCell<>() {
            private final ComboBox<String> comboBox = new ComboBox<>(items);

            {
                comboBox.setEditable(true);
                comboBox.setOnAction(e -> commitEdit(comboBox.getValue()));
                comboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        commitEdit(comboBox.getValue());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    comboBox.setValue(item);
                    setGraphic(comboBox);
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (isEmpty()) {
                    return;
                }

                comboBox.setValue(getItem());
                setText(null);
                setGraphic(comboBox);
                comboBox.requestFocus();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem());
                setGraphic(null);
            }

            @Override
            public void commitEdit(String newValue) {
                if (isEditing()) {
                    super.commitEdit(newValue);
                    setText(newValue);
                    setGraphic(null);
                }
            }

        });
        return column;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnAdd.setCursor(Cursor.HAND);
        btnEliminar.setCursor(Cursor.HAND);
        btnValidar.setCursor(Cursor.HAND);
        btnVerReg.setCursor(Cursor.HAND);
        btnImprimir.setCursor(Cursor.HAND);
        btnGuardar.setCursor(Cursor.HAND);
        columnCta = createComboBoxColumn("Cuenta", "cuenta", FXCollections.observableList(List.of()));
        tableCuentas.setEditable(true);
    }
}
