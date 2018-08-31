package tech.wetech.mybatis.generator.utils;

import com.alibaba.fastjson.JSON;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import tech.wetech.mybatis.generator.annotation.IgnoreProperty;
import tech.wetech.mybatis.generator.model.TemplateConfig;
import tech.wetech.mybatis.generator.model.TemplateFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author cjbi
 */
public class FreemarkerTest {
    private static FreemarkerTest util;
    private static Configuration cfg;

    private FreemarkerTest() {
    }

    public static FreemarkerTest getInstance(String pname) {
        if (util == null) {
            cfg.setLocale(Locale.CHINA);
            cfg = new Configuration(Configuration.VERSION_2_3_23);
            cfg.setClassForTemplateLoading(FreemarkerTest.class, pname);
            cfg.setDefaultEncoding("UTF-8");
            util = new FreemarkerTest();
        }
        return util;
    }

    private Template getTemplate(String fname) {
        try {
            return cfg.getTemplate(fname);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过标准输出流输出模板的结果
     *
     * @param root  数据对象
     * @param fname 模板文件
     */
    public void sprint(Map<String, Object> root, String fname) {
        try {
            Template template = getTemplate(fname);
            template.setEncoding("utf-8");
            template.process(root, new PrintWriter(System.out));
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 基于文件的输出
     *
     * @param root
     * @param fname
     * @param outpath
     */
    public void fprint(Map<String, Object> root, String fname, String outpath) {
        try {
            Template template = getTemplate(fname);
            template.setEncoding("utf-8");
            template.process(root, new FileWriterWithEncoding(outpath, "utf-8"));
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据模板处理
     *
     * @param params
     * @return
     */
    public static String process(String name, String sourceCode, Map<String, Object> params) {
        try {
            Template template = new Template(name, sourceCode, cfg);
            Writer writer = new StringWriter();
            template.process(params, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T resolveProperty(T object, Map<String, Object> properties) throws Exception {
        if (object == null) {
            return null;
        }
        if (object instanceof Collection) {
            for (T element : (Collection<T>) object) {
                resolveProperty(element, properties);

            }
        } else if (object instanceof Map) {

            ((Map) object).forEach((k, v) -> {
                try {
                    if (v != null) {
                        ((Map) object).put(k, process("myTemplate", v.toString(), properties));
                    }
                    resolveProperty(v, properties);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else {

            Field[] fields = object.getClass().getDeclaredFields();

            // 取消默认 Java 语言访问控制检查的能力
            Field.setAccessible(fields, true);

            for (Field field : fields) {

                if (field.get(object) == null) {
                    continue;
                }

                if (field.getAnnotation(IgnoreProperty.class) != null) {
                    continue;
                }

                if (field.getGenericType().toString().equals("class java.lang.String")) {
                    field.set(object, process("myTemplate", field.get(object).toString(), properties));
                    System.out.println(field.get(object));
                } else {
                    if ((field.get(object) instanceof Collection
                            || field.get(object) instanceof Map)
                            && !field.getName().startsWith("this$")
                    ) {
                        resolveProperty(field.get(object), properties);
                    }

                    //如果是用户创建的类
                    if (field.get(object).getClass().getClassLoader() != null) {
                        Map map = BeanUtils.describe(field.get(object));
                        resolveProperty(map, properties);
                        BeanUtils.copyProperties(field.get(object), map);
                    }
                }

            }

            if (object.getClass().getSuperclass().getName().equals("java.lang.Object")) {
                return object;
            } else {
                //递归遍历父类字段
                resolveProperty(object.getClass().getSuperclass(), properties);
            }

        }
        return object;
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> globalData = new HashMap<String, Object>() {{
            put("groupId", "com.example.test");
            put("tableName", "t_user");
        }};
        TemplateConfig templateConfig = ConfigHelper.loadConfig(ConfigConstant.TEMPLATE_CONFIG, TemplateConfig.class);
        Set<TemplateFile> templateFileSet = templateConfig.getTemplateFileSet();
        templateFileSet.add(new TemplateFile(
                "模板测试",
                "template-info.ftl",
                "${groupId}",
                "src/main/java",
                ""
        ));



        System.out.println(JSON.toJSONString(resolveProperty(templateConfig, globalData)));
    }

}
