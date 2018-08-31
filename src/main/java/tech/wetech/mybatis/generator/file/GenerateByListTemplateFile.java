package tech.wetech.mybatis.generator.file;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import tech.wetech.mybatis.generator.formatter.TemplateFormatter;
import tech.wetech.mybatis.generator.model.TableClass;
import tech.wetech.mybatis.generator.model.TemplateFile;
import tech.wetech.mybatis.generator.model.TemplateInfo;

import java.util.Properties;
import java.util.Set;

/**
 * @author cjbi
 */
public class GenerateByListTemplateFile extends GeneratedJavaFile {

    public static final String ENCODING = "UTF-8";

    private TemplateFile templateFile;

    private TemplateInfo templateInfo;

    private Set<TableClass> tableClassSet;

    private TemplateFormatter templateFormatter;

    private Properties properties;

    public GenerateByListTemplateFile(TemplateFile templateFile, Set<TableClass> tableClassSet, TemplateFormatter templateFormatter, Properties properties, String targetProject) {
        super(null, targetProject, ENCODING, null);
        this.templateFile = templateFile;
        this.templateInfo = new TemplateInfo.Builder(templateFile.getTargetFileName(), templateFile.getTargetPackage()).build();
        this.tableClassSet = tableClassSet;
        this.templateFormatter = templateFormatter;
        this.properties = properties;
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
        return templateFormatter.getFormattedContent(tableClassSet, templateInfo, properties, templateFile.getTemplateContent());
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
