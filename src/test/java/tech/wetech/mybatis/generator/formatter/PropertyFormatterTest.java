package tech.wetech.mybatis.generator.formatter;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.mybatis.generator.model.TemplateConfig;
import tech.wetech.mybatis.generator.model.TemplateFile;
import tech.wetech.mybatis.generator.utils.ConfigHelper;
import tech.wetech.mybatis.generator.utils.ConfigConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PropertyFormatterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyFormatterTest.class);

    @Test
    public void resolveString() throws Exception {
        Map<String, Object> globalData = new HashMap<String, Object>() {{
            put("groupId", "com.example.test");
            put("tableName", "t_user");
        }};
        Formatter formatter = new FreemarkerTemplateFormatter();
        String result = formatter.resolveProperty("${groupId},${tableName}", globalData);
        LOGGER.info(JSON.toJSONString(result));
    }

    @Test
    public void resolveProperty() throws Exception {
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
        Formatter formatter = new FreemarkerTemplateFormatter();
        long time = System.currentTimeMillis();
        templateConfig = formatter.resolveProperty(templateConfig, globalData);
        LOGGER.info("FreemarkerTemplateFormatter耗时：{}ms", System.currentTimeMillis() - time);
        LOGGER.info(JSON.toJSONString(templateConfig));

    }

    @Test
    public void resolvePropertyByJSON() throws Exception {
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
        Formatter formatter = new FastJsonPropertyFormatter();
        long time = System.currentTimeMillis();
        templateConfig = formatter.resolveProperty(templateConfig, globalData);
        LOGGER.info("FastJsonPropertyFormatter耗时：{}ms", System.currentTimeMillis() - time);
        LOGGER.info(JSON.toJSONString(templateConfig));
    }

}