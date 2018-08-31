package tech.wetech.mybatis.generator.model;

import java.util.Set;

/**
 * @author cjbi
 */
public class TemplateConfig {

    private Set<TemplateFile> templateFileSet;

    public Set<TemplateFile> getTemplateFileSet() {
        return templateFileSet;
    }

    public void setTemplateFileSet(Set<TemplateFile> templateFileSet) {
        this.templateFileSet = templateFileSet;
    }

    public void addTemplateFile(TemplateFile templateFile) {
        templateFileSet.add(templateFile);
    }

}