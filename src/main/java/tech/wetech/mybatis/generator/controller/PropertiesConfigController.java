package tech.wetech.mybatis.generator.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.commons.lang3.StringUtils;
import tech.wetech.mybatis.generator.model.PropertiesConfig;
import tech.wetech.mybatis.generator.model.PropertiesVO;
import tech.wetech.mybatis.generator.utils.AlertUtil;
import tech.wetech.mybatis.generator.utils.ConfigHelper;
import tech.wetech.mybatis.generator.utils.ConfigConstant;

import java.net.URL;
import java.util.ResourceBundle;

public class PropertiesConfigController extends BaseFXController {

    @FXML
    private TableView<PropertiesVO> propertiesTable;
    @FXML
    private TableColumn<PropertiesVO, String> keyColumn;
    @FXML
    private TableColumn<PropertiesVO, String> valueColumn;
    @FXML
    private TextField valueField;
    @FXML
    private TextField keyField;

    private ObservableList<PropertiesVO> columnList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            PropertiesConfig config = ConfigHelper.loadConfig(ConfigConstant.PROPERTIES_CONFIG, PropertiesConfig.class);
            columnList.addAll(config.getPropertiesVOList());

        } catch (Exception e) {
            e.printStackTrace();
        }

        propertiesTable.setItems(columnList);
        keyColumn.setCellValueFactory(cellData -> cellData.getValue().keyProperty());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setValue(event.getNewValue());
        });


    }

    @FXML
    public void handleCreate() {
        if (isInputValid()) {
            PropertiesVO globalVariableVO = new PropertiesVO(keyField.getText(), valueField.getText());
            columnList.add(globalVariableVO);
            keyField.setText("");
            valueField.setText("");
        }
    }

    /**
     * Returns true if the user clicked Ok,false otherwise.
     *
     * @return
     */
    private boolean isInputValid() {
        String errorMessage = "";
        if (StringUtils.isBlank(keyField.getText())) {
            errorMessage += "The key not blank!\n";
        }
        if (StringUtils.isBlank(valueField.getText())) {
            errorMessage += "The value not blank!\n";
        }
        boolean isUniqueKey = columnList.stream().anyMatch(globalVariableVO -> globalVariableVO.getKey().equals(keyField.getText()));
        if (isUniqueKey) {
            errorMessage += "Not a unique key!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            AlertUtil.showWarnAlert(errorMessage);
        }
        return false;
    }

    @FXML
    public void handleDelete() {
        int selectedIndex = propertiesTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            propertiesTable.getItems().remove(selectedIndex);
        } else {
            AlertUtil.showWarnAlert("Please select a record!");
        }
    }

    @FXML
    public void handleCancel() {
        getDialogStage().close();
    }

    @FXML
    public void handleOk() throws Exception {
        PropertiesConfig config = new PropertiesConfig();
        config.setPropertiesVOList(columnList);
        ConfigHelper.saveConfig(ConfigConstant.PROPERTIES_CONFIG, config);
        getDialogStage().close();
    }
}
