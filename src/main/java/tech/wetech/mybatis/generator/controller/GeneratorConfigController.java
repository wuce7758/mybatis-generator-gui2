package tech.wetech.mybatis.generator.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import tech.wetech.mybatis.generator.model.GeneratorConfig;
import tech.wetech.mybatis.generator.utils.ConfigHelper;
import tech.wetech.mybatis.generator.utils.ConfigConstant;

import java.net.URL;
import java.util.ResourceBundle;

public class GeneratorConfigController extends BaseFXController {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTooltip();
        //加载配置
        loadConfig();
        //默认选中第一个，否则如果忘记选择，没有对应错误提示
        encodingChoice.getSelectionModel().selectFirst();
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

    public CheckBox getOffsetLimitCheckBox() {
        return offsetLimitCheckBox;
    }

    public void setOffsetLimitCheckBox(CheckBox offsetLimitCheckBox) {
        this.offsetLimitCheckBox = offsetLimitCheckBox;
    }

    public CheckBox getCommentCheckBox() {
        return commentCheckBox;
    }

    public void setCommentCheckBox(CheckBox commentCheckBox) {
        this.commentCheckBox = commentCheckBox;
    }

    public CheckBox getOverrideXML() {
        return overrideXML;
    }

    public void setOverrideXML(CheckBox overrideXML) {
        this.overrideXML = overrideXML;
    }

    public CheckBox getNeedToStringHashcodeEquals() {
        return needToStringHashcodeEquals;
    }

    public void setNeedToStringHashcodeEquals(CheckBox needToStringHashcodeEquals) {
        this.needToStringHashcodeEquals = needToStringHashcodeEquals;
    }

    public CheckBox getForUpdateCheckBox() {
        return forUpdateCheckBox;
    }

    public void setForUpdateCheckBox(CheckBox forUpdateCheckBox) {
        this.forUpdateCheckBox = forUpdateCheckBox;
    }

    public CheckBox getAnnotationDAOCheckBox() {
        return annotationDAOCheckBox;
    }

    public void setAnnotationDAOCheckBox(CheckBox annotationDAOCheckBox) {
        this.annotationDAOCheckBox = annotationDAOCheckBox;
    }

    public CheckBox getUseTableNameAliasCheckbox() {
        return useTableNameAliasCheckbox;
    }

    public void setUseTableNameAliasCheckbox(CheckBox useTableNameAliasCheckbox) {
        this.useTableNameAliasCheckbox = useTableNameAliasCheckbox;
    }

    public CheckBox getAnnotationCheckBox() {
        return annotationCheckBox;
    }

    public void setAnnotationCheckBox(CheckBox annotationCheckBox) {
        this.annotationCheckBox = annotationCheckBox;
    }

    public CheckBox getUseActualColumnNamesCheckbox() {
        return useActualColumnNamesCheckbox;
    }

    public void setUseActualColumnNamesCheckbox(CheckBox useActualColumnNamesCheckbox) {
        this.useActualColumnNamesCheckbox = useActualColumnNamesCheckbox;
    }

    public CheckBox getUseExample() {
        return useExample;
    }

    public void setUseExample(CheckBox useExample) {
        this.useExample = useExample;
    }

    public CheckBox getUseDAOExtendStyle() {
        return useDAOExtendStyle;
    }

    public void setUseDAOExtendStyle(CheckBox useDAOExtendStyle) {
        this.useDAOExtendStyle = useDAOExtendStyle;
    }

    public CheckBox getUseSchemaPrefix() {
        return useSchemaPrefix;
    }

    public void setUseSchemaPrefix(CheckBox useSchemaPrefix) {
        this.useSchemaPrefix = useSchemaPrefix;
    }

    public CheckBox getJsr310Support() {
        return jsr310Support;
    }

    public void setJsr310Support(CheckBox jsr310Support) {
        this.jsr310Support = jsr310Support;
    }

    public ChoiceBox<String> getEncodingChoice() {
        return encodingChoice;
    }

    public void setEncodingChoice(ChoiceBox<String> encodingChoice) {
        this.encodingChoice = encodingChoice;
    }

    public void handleOk(ActionEvent actionEvent) throws Exception {
        GeneratorConfig configuration = extractConfigForUI();
        ConfigHelper.saveConfig(ConfigConstant.GENERATOR_CONFIG, configuration);
        getDialogStage().close();
    }

    private GeneratorConfig extractConfigForUI() {
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

    @FXML
    public void handleCancel() {
        getDialogStage().close();
    }
}
