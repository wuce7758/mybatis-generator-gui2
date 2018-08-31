package tech.wetech.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import tech.wetech.mybatis.generator.file.GenerateByListTemplateFile;
import tech.wetech.mybatis.generator.file.GenerateByTemplateFile;
import tech.wetech.mybatis.generator.formatter.TemplateFormatter;
import tech.wetech.mybatis.generator.model.TableClass;
import tech.wetech.mybatis.generator.model.TableColumnBuilder;
import tech.wetech.mybatis.generator.model.TemplateFile;

import java.util.*;


/**
 * @author cjbi
 */
public class TemplateFilePlugin extends PluginAdapter {

    /**
     * 项目路径（目录需要已经存在）
     */
    private String targetProject;
    /**
     * 单个文件模式
     */
    private String singleMode = "TRUE";

    /**
     * 模板生成器
     */
    private TemplateFormatter templateFormatter;


    private Set<TableClass> cacheTables;

    private TemplateFile templateFile;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private Map<String, Object> buildResolveParams(Properties properties) {
        Map<String, Object> resolveParams = new HashMap<>();
        for (Object o : properties.keySet()) {
            resolveParams.put(String.valueOf(o), properties.get(o));
        }
        return resolveParams;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> list = new ArrayList<>();
        TableClass tableClass = TableColumnBuilder.build(introspectedTable);
        if ("TRUE".equalsIgnoreCase(singleMode)) {
            properties.put("tableClass", tableClass);
            try {
                list.add(new GenerateByTemplateFile(templateFormatter.resolveProperty(templateFile, buildResolveParams(properties)),
                        tableClass,
                        templateFormatter,
                        properties,
                        targetProject));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            cacheTables.add(tableClass);
        }
        return list;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> list = new ArrayList<>();
        if (cacheTables != null && cacheTables.size() > 0) {
            try {
                list.add(new GenerateByListTemplateFile(templateFormatter.resolveProperty(templateFile, buildResolveParams(properties)),
                        cacheTables,
                        templateFormatter,
                        properties,
                        targetProject));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        if (!"TRUE".equalsIgnoreCase(singleMode)) {
            this.cacheTables = new LinkedHashSet<>();
        }
        this.templateFile = (TemplateFile) properties.get("templateFile");
        this.templateFormatter = (TemplateFormatter) properties.get("formatter");
        this.targetProject = properties.getProperty("targetProject");
    }
}