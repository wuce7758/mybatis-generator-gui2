package tech.wetech.mybatis.generator.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.commons.lang3.StringUtils;
import tech.wetech.mybatis.generator.model.*;
import tech.wetech.mybatis.generator.utils.AlertUtil;
import tech.wetech.mybatis.generator.utils.ConfigConstant;
import tech.wetech.mybatis.generator.utils.ConfigHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author cjbi
 */
public class ConfigsController extends BaseFXController {

    //generatorConfig
    @FXML
    private CheckBox offsetLimitCheckBox;
    @FXML
    private CheckBox commentCheckBox;
    @FXML
    private CheckBox overrideXML;
    @FXML
    private CheckBox needToStringHashcodeEquals;
    @FXML
    private CheckBox forUpdateCheckBox;
    @FXML
    private CheckBox annotationDAOCheckBox;
    @FXML
    private CheckBox useTableNameAliasCheckbox;
    @FXML
    private CheckBox annotationCheckBox;
    @FXML
    private CheckBox useActualColumnNamesCheckbox;
    @FXML
    private CheckBox useExample;
    @FXML
    private CheckBox useDAOExtendStyle;
    @FXML
    private CheckBox useSchemaPrefix;
    @FXML
    private CheckBox jsr310Support;
    @FXML
    private ChoiceBox<String> encodingChoice;

    //generatorConfig
    @FXML
    private TableView<TemplateFile> templateTable;
    @FXML
    private TableColumn<TemplateFile, Boolean> checkedColumn;
    @FXML
    public TableColumn<TemplateFile, String> templateFileNameColumn;
    private final ObservableList<TemplateFile> templateFileColumnList = FXCollections.observableArrayList();


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

    //propertiesConfig
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

    private final ObservableList<PropertiesVO> propertiesColumnList = FXCollections.observableArrayList();

    //表格默认值
    @FXML
    private TextField modelTargetPackageTextField;
    @FXML
    private TextField mapperTargetPackageTextField;
    @FXML
    private TextField daoTargetPackageTextField;
    @FXML
    private TextField generateKeysFieldTextField;    //主键ID
    @FXML
    private TextField modelTargetProjectTextField;
    @FXML
    private TextField mappingTargetProjectTextField;
    @FXML
    private TextField daoTargetProjectTextField;

    private TableVO tableVO;

    public void setTableVO(TableVO tableVO) {
        if (tableVO == null) {
            tableVO = new TableVO();
        }
        this.tableVO = tableVO;

        modelTargetPackageTextField.setText(tableVO.getModelPackage());
        mapperTargetPackageTextField.setText(tableVO.getMappingXMLPackage());
        daoTargetPackageTextField.setText(tableVO.getDaoPackage());
        generateKeysFieldTextField.setText(tableVO.getGenerateKeys());
        modelTargetProjectTextField.setText(tableVO.getModelPackageTargetFolder());
        mappingTargetProjectTextField.setText(tableVO.getMappingXMLTargetFolder());
        daoTargetProjectTextField.setText(tableVO.getDaoTargetFolder());
    }

    private TableVO getTableVO() {
        if (tableVO == null) {
            tableVO = new TableVO();
        }
        tableVO.setModelPackage(modelTargetPackageTextField.getText());
        tableVO.setMappingXMLPackage(mapperTargetPackageTextField.getText());
        tableVO.setDaoPackage(daoTargetPackageTextField.getText());
        tableVO.setGenerateKeys(generateKeysFieldTextField.getText());
        tableVO.setModelPackageTargetFolder(modelTargetProjectTextField.getText());
        tableVO.setMappingXMLTargetFolder(mappingTargetProjectTextField.getText());
        tableVO.setDaoTargetFolder(daoTargetProjectTextField.getText());
        return tableVO;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTooltip();
        //加载配置
        loadConfig();
        //默认选中第一个，否则如果忘记选择，没有对应错误提示
        encodingChoice.getSelectionModel().selectFirst();

        try {
            TemplateConfig config = ConfigHelper.loadConfig(ConfigConstant.TEMPLATE_CONFIG, TemplateConfig.class);
            templateFileColumnList.addAll(new ArrayList(config.getTemplateFileSet()));
            templateTable.setItems(templateFileColumnList);

            checkedColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
            templateFileNameColumn.setCellValueFactory(cellData -> cellData.getValue().templateFileNameProperty());

            checkedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkedColumn));

            templateTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showTemplateDetails(newValue));

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            PropertiesConfig config = ConfigHelper.loadConfig(ConfigConstant.PROPERTIES_CONFIG, PropertiesConfig.class);
            propertiesColumnList.addAll(config.getPropertiesVOList());

        } catch (Exception e) {
            e.printStackTrace();
        }

        propertiesTable.setItems(propertiesColumnList);
        keyColumn.setCellValueFactory(cellData -> cellData.getValue().keyProperty());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setValue(event.getNewValue());
        });

        try {
            TableVO tableVO = ConfigHelper.loadConfig(ConfigConstant.DEFAULT_TABLE_CONFIG, TableVO.class);
            setTableVO(tableVO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadConfig() {
        try {
            GeneratorConfig config = ConfigHelper.loadConfig(ConfigConstant.GENERATOR_CONFIG, GeneratorConfig.class);
            if (config == null) {
                return;
            }
            offsetLimitCheckBox.setSelected(config.isOffsetLimit());
            commentCheckBox.setSelected(config.isComment());
            overrideXML.setSelected(config.isOverrideXML());
            needToStringHashcodeEquals.setSelected(config.isNeedToStringHashcodeEquals());
            forUpdateCheckBox.setSelected(config.isNeedForUpdate());
            annotationDAOCheckBox.setSelected(config.isAnnotationDAO());
            useTableNameAliasCheckbox.setSelected(config.isUseTableNameAlias());
            annotationCheckBox.setSelected(config.isAnnotation());
            useActualColumnNamesCheckbox.setSelected(config.isUseActualColumnNames());
            useExample.setSelected(config.isUseExample());
            useDAOExtendStyle.setSelected(config.isUseDAOExtendStyle());
            useSchemaPrefix.setSelected(config.isUseSchemaPrefix());
            jsr310Support.setSelected(config.isJsr310Support());
            encodingChoice.setValue(config.getEncoding());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTooltip() {
        encodingChoice.setTooltip(new Tooltip("生成文件的编码，必选"));
//        generateKeysFieldTextField.setTooltip(new Tooltip("insert时可以返回主键ID"));
        offsetLimitCheckBox.setTooltip(new Tooltip("是否要生成分页查询代码"));
        commentCheckBox.setTooltip(new Tooltip("使用数据库的列注释作为实体类字段名的Java注释 "));
        useActualColumnNamesCheckbox.setTooltip(new Tooltip("是否使用数据库实际的列名作为实体类域的名称"));
        useTableNameAliasCheckbox.setTooltip(new Tooltip("在Mapper XML文件中表名使用别名，并且列全部使用as查询"));
        overrideXML.setTooltip(new Tooltip("重新生成时把原XML文件覆盖，否则是追加"));
        useDAOExtendStyle.setTooltip(new Tooltip("将通用接口方法放在公共接口中，DAO接口留空"));
        forUpdateCheckBox.setTooltip(new Tooltip("在Select语句中增加for update后缀"));
    }


    private GeneratorConfig extractGeneratorConfigForUI() {
        GeneratorConfig configuration = new GeneratorConfig();
        configuration.setEncoding(encodingChoice.getValue());
        configuration.setComment(commentCheckBox.isSelected());
        configuration.setOverrideXML(overrideXML.isSelected());
        configuration.setUseSchemaPrefix(useSchemaPrefix.isSelected());
        configuration.setNeedToStringHashcodeEquals(needToStringHashcodeEquals.isSelected());
        configuration.setNeedForUpdate(forUpdateCheckBox.isSelected());
        configuration.setAnnotationDAO(annotationDAOCheckBox.isSelected());
        configuration.setUseDAOExtendStyle(useDAOExtendStyle.isSelected());
        configuration.setJsr310Support(jsr310Support.isSelected());
        configuration.setAnnotation(annotationCheckBox.isSelected());
        configuration.setUseActualColumnNames(useActualColumnNamesCheckbox.isSelected());
        configuration.setUseTableNameAlias(useTableNameAliasCheckbox.isSelected());
        configuration.setUseExample(useExample.isSelected());
        configuration.setOffsetLimit(offsetLimitCheckBox.isSelected());
        return configuration;
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

        TemplateConfigEditController controller = new TemplateConfigEditController().loadDialog("新增模板", FXMLConstant.TEMPLATE_CONFIG_EDIT, false);
        controller.dialogStage.showAndWait();
        if (controller.isOkClicked()) {
            templateFileColumnList.add(controller.getTemplateFile());
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
            TemplateConfigEditController controller = new TemplateConfigEditController().loadDialog("新增模板", FXMLConstant.TEMPLATE_CONFIG_EDIT, false);
            controller.setTemplateFile(selectedTemplateFile);
            controller.dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                showTemplateDetails(selectedTemplateFile);
                saveConfig();
            }
        } else {
            AlertUtil.showWarnAlert("请先选择一条记录!");
        }
    }

    private void saveConfig() {
        TemplateConfig config = new TemplateConfig();
        config.setTemplateFileSet(new HashSet<>(templateFileColumnList));
        try {
            ConfigHelper.saveConfig(ConfigConstant.TEMPLATE_CONFIG, config);
        } catch (Exception e) {
            e.printStackTrace();
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

    @FXML
    public void handleOk(ActionEvent actionEvent) throws Exception {
        GeneratorConfig generatorConfig = extractGeneratorConfigForUI();
        ConfigHelper.saveConfig(ConfigConstant.GENERATOR_CONFIG, generatorConfig);

        PropertiesConfig propertiesConfig = new PropertiesConfig();
        propertiesConfig.setPropertiesVOList(propertiesColumnList);
        ConfigHelper.saveConfig(ConfigConstant.PROPERTIES_CONFIG, propertiesConfig);

        TableVO tableVO = getTableVO();
        ConfigHelper.saveConfig(ConfigConstant.DEFAULT_TABLE_CONFIG, tableVO);
        this.dialogStage.close();
    }

    @FXML
    public void handleCancel() {
        this.dialogStage.close();
    }


    @FXML
    public void handleCreate() {
        if (isInputValid()) {
            PropertiesVO globalVariableVO = new PropertiesVO(keyField.getText(), valueField.getText());
            propertiesColumnList.add(globalVariableVO);
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
        boolean isUniqueKey = propertiesColumnList.stream().anyMatch(globalVariableVO -> globalVariableVO.getKey().equals(keyField.getText()));
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
    private void handleDelete() {
        int selectedIndex = propertiesTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            propertiesTable.getItems().remove(selectedIndex);
        } else {
            AlertUtil.showWarnAlert("Please select a record!");
        }
    }


    @FXML
    private void handleExportTemplateFile() {
        AlertUtil.showInfoAlert("handleExportTemplateFile功能实现中！");
    }

    @FXML
    private void handleImportTemplateFile() {
        AlertUtil.showInfoAlert("handleImportTemplateFile功能实现中！");
    }
}
