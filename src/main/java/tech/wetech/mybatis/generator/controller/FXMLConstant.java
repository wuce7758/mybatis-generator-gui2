package tech.wetech.mybatis.generator.controller;

/**
 * @author cjbi
 */
public enum FXMLConstant {

    JDBC_CONNECTION("fxml/JdbcConnection.fxml"),
    TABLE("fxml/Table.fxml"),
    GENERATOR_CONFIG("fxml/GeneratorConfig.fxml"),
    SELECT_TABLE_COLUMN("fxml/SelectTableColumn.fxml"),
    GLOBAL_VARIABLE("fxml/PropertiesConfig.fxml"),
    TEMPLATE_CONFIG("fxml/TemplateConfig.fxml"),
    TEMPLATE_CONFIG_EDIT("fxml/TemplateConfigEdit.fxml");

    private String fxml;

    FXMLConstant(String fxml) {
        this.fxml = fxml;
    }

    public String getFxml() {
        return this.fxml;
    }


}
