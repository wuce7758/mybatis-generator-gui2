package tech.wetech.mybatis.generator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import tech.wetech.mybatis.generator.annotation.IgnoreProperty;

/**
 * @author cjbi
 */
public class TemplateFile {

    private BooleanProperty checked = new SimpleBooleanProperty(true);//Default true

    private StringProperty templateFileName = new SimpleStringProperty();

    private StringProperty targetFileName = new SimpleStringProperty();

    private StringProperty targetPackage = new SimpleStringProperty();

    private StringProperty targetFolder = new SimpleStringProperty();

    @IgnoreProperty
    private StringProperty templateContent = new SimpleStringProperty();

    private JdbcType jdbcType = JdbcType.MySQL;

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public TemplateFile() {
    }

    public TemplateFile(boolean checked, String templateFileName, String targetFileName, String targetPackage, String targetFolder, String templateContent) {
        this.checked.set(checked);
        this.templateFileName.set(templateFileName);
        this.targetFileName.set(targetFileName);
        this.targetPackage.set(targetPackage);
        this.targetFolder.set(targetFolder);
        this.templateContent.set(templateContent);
    }

    public TemplateFile(String templateFileName, String targetFileName, String targetPackage, String targetFolder, String templateContent) {
        this.templateFileName.set(templateFileName);
        this.targetFileName.set(targetFileName);
        this.targetPackage.set(targetPackage);
        this.targetFolder.set(targetFolder);
        this.templateContent.set(templateContent);
    }

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    public String getTemplateFileName() {
        return templateFileName.get();
    }

    public StringProperty templateFileNameProperty() {
        return templateFileName;
    }

    public void setTemplateFileName(String templateFileName) {
        this.templateFileName.set(templateFileName);
    }

    public String getTargetFileName() {
        return targetFileName.get();
    }

    public StringProperty targetFileNameProperty() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName.set(targetFileName);
    }

    public String getTargetPackage() {
        return targetPackage.get();
    }

    public StringProperty targetPackageProperty() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage.set(targetPackage);
    }

    public String getTargetFolder() {
        return targetFolder.get();
    }

    public StringProperty targetFolderProperty() {
        return targetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder.set(targetFolder);
    }

    public String getTemplateContent() {
        return templateContent.get();
    }

    public StringProperty templateContentProperty() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent.set(templateContent);
    }

    public String getVariableName() {
        if (targetFileName.get() == null) {
            return null;
        }
        if (targetFileName.get().lastIndexOf(".") == -1) {
            return null;
        }
        return targetFileName.get().substring(0, targetFileName.get().lastIndexOf("."));
    }

    public String getSuffix() {
        if (targetFileName == null) {
            return null;
        }
        if (targetFileName.get().lastIndexOf(".") == -1) {
            return null;
        }
        int fromIndex = targetFileName.get().length();
        return targetFileName.get().substring(targetFileName.get().lastIndexOf(".") + 1, fromIndex);
    }


}
