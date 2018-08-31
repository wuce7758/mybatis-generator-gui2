package tech.wetech.mybatis.generator.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import tech.wetech.mybatis.generator.model.TemplateConfig;
import tech.wetech.mybatis.generator.model.TemplateFile;
import tech.wetech.mybatis.generator.utils.AlertUtil;
import tech.wetech.mybatis.generator.utils.ConfigHelper;
import tech.wetech.mybatis.generator.utils.ConfigConstant;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author cjbi
 */
public class TemplateConfigController extends BaseFXController {

    @FXML
    private TableView<TemplateFile> templateTable;
    @FXML
    private TableColumn<TemplateFile, Boolean> checkedColumn;
    @FXML
    public TableColumn<TemplateFile, String> templateFileNameColumn;

    private ObservableList<TemplateFile> columnList = FXCollections.observableArrayList();

    @FXML
    private Label templateFileNameLabel;
    @FXML
    private Label targetFileNameLabel;
    @FXML
    private Label targetPackageLabel;
    @FXML
    private Label targetFolderLabel;
    @FXML
    private TextArea templateContentTextArea;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            TemplateConfig config = ConfigHelper.loadConfig(ConfigConstant.TEMPLATE_CONFIG, TemplateConfig.class);
            columnList.addAll(new ArrayList(config.getTemplateFileSet()));
            templateTable.setItems(columnList);

            checkedColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
            templateFileNameColumn.setCellValueFactory(cellData -> cellData.getValue().templateFileNameProperty());

            checkedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkedColumn));

            templateTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showTemplateDetails(newValue));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTemplateDetails(TemplateFile templateFile) {
        if (templateFile != null) {
            templateFileNameLabel.setText(templateFile.getTemplateFileName());
            targetFileNameLabel.setText(templateFile.getTargetFileName());
            targetPackageLabel.setText(templateFile.getTargetPackage());
            targetFolderLabel.setText(templateFile.getTargetFolder());
            templateContentTextArea.setText(templateFile.getTemplateContent());
        } else {
            //TemplateFile is null, remove all the text.
            templateFileNameLabel.setText("");
            targetFileNameLabel.setText("");
            targetPackageLabel.setText("");
            targetFolderLabel.setText("");
            templateContentTextArea.setText("");
        }
    }

    @FXML
    public void handleNewTemplateFile() {

        TemplateConfigEditController controller = loadDialog("新增模板", FXMLConstant.TEMPLATE_CONFIG_EDIT, false);
        dialogStage.showAndWait();
        if (controller.isOkClicked()) {
            columnList.add(controller.getTemplateFile());
            try {
                TemplateConfig config = ConfigHelper.loadConfig(ConfigConstant.TEMPLATE_CONFIG, TemplateConfig.class);
                config.addTemplateFile(controller.getTemplateFile());
                saveConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    public void handleEditTemplateFile() {
        TemplateFile selectedTemplateFile = templateTable.getSelectionModel().getSelectedItem();
        if (selectedTemplateFile != null) {
            TemplateConfigEditController controller = loadDialog("新增模板", FXMLConstant.TEMPLATE_CONFIG_EDIT, false);
            controller.setTemplateFile(selectedTemplateFile);
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                showTemplateDetails(selectedTemplateFile);
                saveConfig();
            }
        } else {
            AlertUtil.showWarnAlert("请先选择一条记录!");
        }
    }

    @FXML
    public void handleDeleteTemplateFile() {
        int selectedIndex = templateTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Alert alert = AlertUtil.buildConfirmationAlert("删除后不可恢复，是否确定?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                templateTable.getItems().remove(selectedIndex);
                saveConfig();
            }
        } else {
            AlertUtil.showWarnAlert("请选中一条记录");
        }
    }

    private void saveConfig() {
        TemplateConfig config = new TemplateConfig();
        config.setTemplateFileSet(new HashSet<>(columnList));
        try {
            ConfigHelper.saveConfig(ConfigConstant.TEMPLATE_CONFIG, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
