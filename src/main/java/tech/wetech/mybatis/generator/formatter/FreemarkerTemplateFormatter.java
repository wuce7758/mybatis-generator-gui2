package tech.wetech.mybatis.generator.formatter;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.beanutils.PropertyUtils;
import tech.wetech.mybatis.generator.annotation.IgnoreProperty;
import tech.wetech.mybatis.generator.model.TableClass;
import tech.wetech.mybatis.generator.model.TemplateInfo;

import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 基于 freemarker 的实现
 *
 * @author cjbi
 */
public class FreemarkerTemplateFormatter implements TemplateFormatter {

    private final Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);

    private final StringTemplateLoader templateLoader = new StringTemplateLoader();

    private static final String DEFAULT_TEMPLATE_CACHE_NAME = "myTemplateName";

    public FreemarkerTemplateFormatter() {
        configuration.setLocale(Locale.CHINA);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateLoader(templateLoader);
        configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_23));
    }

    /**
     * 根据模板处理
     *
     * @param templateName
     * @param templateSource
     * @param params
     * @return
     */
    public String process(String templateName, String templateSource, Map<String, Object> params) {
        try {
            Template template = new Template(templateName, templateSource, configuration);
            Writer writer = new StringWriter();
            template.process(params, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFormattedContent(TableClass tableClass, TemplateInfo templateInfo, Properties properties, String templateContent) {
        Map<String, Object> params = new HashMap();
        params.put("props", properties);
        params.put("package", templateInfo.getPackageName());
        params.put("tableClass", tableClass);
        params.put("templateInfo", templateInfo);
        for (Object o : properties.keySet()) {
            params.put(String.valueOf(o), properties.get(o));
        }
        return process(templateInfo.getVariableName(), templateContent, params);
    }

    @Override
    public String getFormattedContent(Set<TableClass> tableClassSet, TemplateInfo templateInfo, Properties properties, String templateContent) {
        Map<String, Object> params = new HashMap<>();
        params.put("props", properties);
        params.put("package", templateInfo.getPackageName());
        params.put("tableClassSet", tableClassSet);
        params.put("templateInfo", templateInfo);
        for (Object o : properties.keySet()) {
            params.put(String.valueOf(o), properties.get(o));
        }
        return process(templateInfo.getVariableName(), templateContent, params);
    }

    @Override
    public String getFormattedContent(String templateName, String templateContent, Map<String, Object> params) {
        return process(templateName, templateContent, params);
    }

    @Override
    public <T> T resolveProperty(T object, Map<String, Object> params) throws Exception {

        if (object == null) {
            return null;
        }
        if (object instanceof Collection) {
            for (T element : (Collection<T>) object) {
                resolveProperty(element, params);

            }
        } else if (object instanceof Map) {

            ((Map) object).forEach((k, v) -> {
                try {
                    if (v != null) {
                        resolveProperty(v, params);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else if (object instanceof String) {
            return (T) process(DEFAULT_TEMPLATE_CACHE_NAME, object.toString(), params);
        } else {

            //关闭访问权限检查
            Method.setAccessible(object.getClass().getMethods(), false);

            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);

            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {


                //字段名称
                String name = propertyDescriptor.getName();

                //存在get和set方法
                if (!(PropertyUtils.isReadable(object, name) && PropertyUtils.isWriteable(object, name))) {
                    continue;
                }

                //是否是忽略字段
                if (PropertyUtils.getWriteMethod(propertyDescriptor).getAnnotation(IgnoreProperty.class) != null
                        || PropertyUtils.getReadMethod(propertyDescriptor).getAnnotation(IgnoreProperty.class) != null
                        || object.getClass().getDeclaredField(name).getAnnotation(IgnoreProperty.class) != null

                ) {
                    continue;
                }

                //字段的值
                Object value = PropertyUtils.getSimpleProperty(object, name);
                if (value == null) {
                    continue;
                }

                // No point in trying to set an object's class
                if ("class".equals(name)) {
                    continue;
                }

                if (value instanceof String) {
                    PropertyUtils.setSimpleProperty(object, name, process(name, (String) value, params));
                    continue;
                }

                //如果是集合对象
                if ((value instanceof Collection || value instanceof Map)) {
                    resolveProperty(value, params);
                }

                //如果是用户创建的类
//                if (value.getClass().getClassLoader() != null) {
//                    resolveProperty(value, params);
//                }

            }
        }
        return object;
    }

}
