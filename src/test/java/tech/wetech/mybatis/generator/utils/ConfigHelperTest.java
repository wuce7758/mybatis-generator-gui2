package tech.wetech.mybatis.generator.utils;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.mybatis.generator.model.TemplateConfig;
import tech.wetech.mybatis.generator.model.TemplateFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class ConfigHelperTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigHelperTest.class);

    @Test
    public void loadConfig() {
        LOGGER.info(getTestOneTemplateContent());
    }

    /**
     * 读取文件
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuffer stringBuffer = new StringBuffer();
        String line = reader.readLine();
        while (line != null) {
            stringBuffer.append(line).append("\n");
            line = reader.readLine();
        }
        return stringBuffer.toString();
    }

    private String getTestOneTemplateContent() {
        InputStream inputStream = null;
        try {
            inputStream = ConfigHelperTest.class.getResourceAsStream("/ftl/template-test.ftl");
            return read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void saveConfig() throws Exception {
        TemplateConfig templateConfig = new TemplateConfig();
        Set<TemplateFile> templateFileSet = new HashSet<>();
        templateConfig.setTemplateFileSet(templateFileSet);

        TemplateFile templateFile = new TemplateFile(
                "模板测试",
                "template-info.ftl",
                "${groupId}",
                "src/main/java",
                getTestOneTemplateContent()
        );

        templateFileSet.add(templateFile);

        ConfigHelper.saveConfig(ConfigConstant.TEMPLATE_CONFIG, templateConfig);
        System.out.println(JSON.toJSONString(ConfigHelper.loadConfig(ConfigConstant.TEMPLATE_CONFIG, TemplateConfig.class)));
    }
}