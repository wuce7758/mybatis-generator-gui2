package tech.wetech.mybatis.generator.formatter;

import java.util.Map;

/**
 * @author cjbi
 */
public interface Formatter {

    /**
     * 获取根据模板生成的数据
     *
     * @param templateName
     * @param templateContent
     * @param params
     * @return
     */
    String getFormattedContent(String templateName, String templateContent, Map<String,Object> params);

    /**
     * 格式化属性字段上面的表达式
     *
     * @param object
     * @param params
     * @param <T>
     * @return
     */
    <T> T resolveProperty(T object, Map<String,Object> params) throws Exception;


}
