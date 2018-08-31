package tech.wetech.mybatis.generator.formatter;

import tech.wetech.mybatis.generator.model.TableClass;
import tech.wetech.mybatis.generator.model.TemplateInfo;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author cjbi
 */
public interface TemplateFormatter extends Formatter {

    /**
     * 获取根据模板生成的数据
     *
     * @param tableClass
     * @param templateInfo
     * @param properties
     * @param templateContent
     * @return
     */
    String getFormattedContent(TableClass tableClass, TemplateInfo templateInfo, Properties properties, String templateContent);

    /**
     * 获取根据模板生成的数据
     *
     * @param tableClassSet
     * @param templateInfo
     * @param properties
     * @param templateContent
     * @return
     */
    String getFormattedContent(Set<TableClass> tableClassSet, TemplateInfo templateInfo, Properties properties, String templateContent);

}
