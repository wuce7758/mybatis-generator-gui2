package tech.wetech.mybatis.generator.file;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import tech.wetech.mybatis.generator.formatter.TemplateFormatter;
import tech.wetech.mybatis.generator.model.TableClass;
import tech.wetech.mybatis.generator.model.TemplateFile;
import tech.wetech.mybatis.generator.model.TemplateInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author cjbi
 */
public class GenerateByTemplateFile extends GeneratedJavaFile {

    public static final String ENCODING = "UTF-8";

    private TemplateFile templateFile;

    private TemplateInfo templateInfo;

    private TableClass tableClass;

    private TemplateFormatter templateFormatter;

    private Properties properties;

    public GenerateByTemplateFile(TemplateFile templateFile, TableClass tableClass, TemplateFormatter templateFormatter, Properties properties, String targetProject) {
        super(null, targetProject, ENCODING, null);


        this.templateInfo = new TemplateInfo.Builder(templateFile.getVariableName(), templateFile.getTargetPackage()).build();
        this.templateFile = templateFile;
        this.tableClass = tableClass;
        this.templateFormatter = templateFormatter;
        this.properties = properties;

        Map<String,Object> params = new HashMap<>();
        params.put("props", properties);
        params.put("package", templateInfo.getPackageName());
        params.put("templateInfo", templateInfo);
        params.put("tableClass", tableClass);
        for (Object o : properties.keySet()) {
            params.put(String.valueOf(o), properties.get(o));
        }
        try {
            templateFormatter.resolveProperty(templateFile, params);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public CompilationUnit getCompilationUnit() {
        return null;
    }

    @Override
    public String getFileName() {
        return templateFile.getTargetFileName();
    }

    @Override
    public String getFormattedContent() {
        return templateFormatter.getFormattedContent(tableClass, templateInfo, properties, templateFile.getTemplateContent());
    }

    @Override
    public String getTargetPackage() {
        return templateFile.getTargetPackage();
    }

    @Override
    public boolean isMergeable() {
        return false;
    }

}
