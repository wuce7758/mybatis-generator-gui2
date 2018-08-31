package tech.wetech.mybatis.generator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import tech.wetech.mybatis.generator.model.TemplateFile;
import tech.wetech.mybatis.generator.utils.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class TemplateConfigEditController extends BaseFXController {

    @FXML
    private TextField templateFileNameTextField;
    @FXML
    private TextField targetFileNameTextField;
    @FXML
    private TextField targetPackageTextField;
    @FXML
    private TextField targetFolderTextField;
    @FXML
    private TextArea templateContentTextArea;

    private TemplateFile templateFile;

    private boolean okClicked = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void handleCancel() {
        getDialogStage().close();
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    public void handleOk() {
        if (isInputValid()) {
            if (templateFile == null) {
                templateFile = new TemplateFile();
            }

            templateFile.setTemplateFileName(templateFileNameTextField.getText());
            templateFile.setTargetFileName(targetFileNameTextField.getText());
            templateFile.setTargetPackage(targetPackageTextField.getText());
            templateFile.setTargetFolder(targetFolderTextField.getText());
            templateFile.setTemplateContent(templateContentTextArea.getText());
            okClicked = true;
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (StringUtils.isBlank(templateFileNameTextField.getText())) {
            errorMessage += "模板文件名称不能为空!\n";
        }
        if (StringUtils.isBlank(targetFileNameTextField.getText())) {
            errorMessage += "输出文件名称不能为空!\n";
        }
        if (StringUtils.isBlank(targetPackageTextField.getText())) {
            errorMessage += "输出文件包名不能为空!\n";
        }
        if (StringUtils.isBlank(targetFolderTextField.getText())) {
            errorMessage += "存放目录不能为空!\n";
        }
        if (StringUtils.isBlank(templateContentTextArea.getText())) {
            errorMessage += "模板内容不能为空!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            AlertUtil.showWarnAlert(String.format(errorMessage));
            return false;
        }

    }

    public void setTemplateFile(TemplateFile templateFile) {
        if (templateFile == null) {
            templateFile = new TemplateFile();
        }
        this.templateFile = templateFile;

        templateFileNameTextField.setText(templateFile.getTemplateFileName());
        targetFileNameTextField.setText(templateFile.getTargetFileName());
        targetPackageTextField.setText(templateFile.getTargetPackage());
        targetFolderTextField.setText(templateFile.getTargetFolder());
        templateContentTextArea.setText(templateFile.getTemplateContent());
    }

    public TemplateFile getTemplateFile() {
        return templateFile;
    }
}
