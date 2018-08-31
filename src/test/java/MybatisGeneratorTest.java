import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cjbi
 */
public class MybatisGeneratorTest {

    @Test
    public void testGenerator() throws Exception {
        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        File configFile = new File(MybatisGeneratorTest.class.getClassLoader().getResource("generatorConfig.xml").getFile());
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        ProgressCallback progressCallback = new VerboseProgressCallback();
        myBatisGenerator.generate(progressCallback);
    }

    @Test
    public void testFreeMarkerTemplateGenerator() throws Exception {
        Configuration configuration = new Configuration();
    }


}
