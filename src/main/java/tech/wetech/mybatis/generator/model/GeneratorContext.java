package tech.wetech.mybatis.generator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author cjbi
 */
public class GeneratorContext {

    private List<TableVO> tables;

    private GeneratorConfig configuration;

    private String projectFolder;

    private TemplateConfig templateConfig;

    private PropertiesConfig propertiesConfig;

    public Map<String, Object> buildResolveParams() {
        Map<String, Object> map = new HashMap<>();

        //属性信息
        propertiesConfig.getPropertiesVOList().forEach(propertiesVO ->
                map.put(propertiesVO.getKey(), propertiesVO.getValue()));
        //模板信息
        templateConfig.getTemplateFileSet().forEach(templateFile -> {
            TemplateInfo templateInfo = new TemplateInfo.Builder(templateFile.getVariableName(), templateFile.getTargetPackage()).build();
            map.put(templateInfo.getVariableName(), templateInfo);
        });
        return map;
    }

    public Properties buildProperties() {

        Properties properties = new Properties();
        //属性信息
        propertiesConfig.getPropertiesVOList().forEach(propertiesVO ->
                properties.setProperty(propertiesVO.getKey(), propertiesVO.getValue()));

        //模板信息
        templateConfig.getTemplateFileSet().forEach(templateFile -> {
            TemplateInfo templateInfo = new TemplateInfo.Builder(templateFile.getVariableName(), templateFile.getTargetPackage()).build();
            properties.put(templateInfo.getVariableName(), templateInfo);
        });
        return properties;
    }

    public PropertiesConfig getPropertiesConfig() {
        return propertiesConfig;
    }

    public void setPropertiesConfig(PropertiesConfig propertiesConfig) {
        this.propertiesConfig = propertiesConfig;
    }

    public GeneratorConfig getConfiguration() {
        return configuration;
    }

    public void setConfiguration(GeneratorConfig configuration) {
        this.configuration = configuration;
    }

    public List<TableVO> getTables() {
        return tables;
    }

    public void setTables(List<TableVO> tables) {
        this.tables = tables;
    }

    public String getProjectFolder() {
        return projectFolder;
    }

    public void setProjectFolder(String projectFolder) {
        this.projectFolder = projectFolder;
    }

    public TemplateConfig getTemplateConfig() {
        return templateConfig;
    }

    public void setTemplateConfig(TemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
    }

}
