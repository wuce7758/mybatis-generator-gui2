package tech.wetech.mybatis.generator.bridge;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.mybatis.generator.formatter.FreemarkerTemplateFormatter;
import tech.wetech.mybatis.generator.formatter.TemplateFormatter;
import tech.wetech.mybatis.generator.model.GeneratorContext;
import tech.wetech.mybatis.generator.model.JdbcType;
import tech.wetech.mybatis.generator.model.TableVO;
import tech.wetech.mybatis.generator.model.TemplateFile;
import tech.wetech.mybatis.generator.plugins.*;
import tech.wetech.mybatis.generator.utils.ConfigHelper;
import tech.wetech.mybatis.generator.utils.DbUtil;

import java.io.File;
import java.util.*;

/**
 * @author cjbi
 */
public class MybatisGeneratorBridge {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisGeneratorBridge.class);

    private ProgressCallback progressCallback;

    private static final TemplateFormatter formatter = new FreemarkerTemplateFormatter();

    public MybatisGeneratorBridge() {
    }

    public void generate(GeneratorContext generatorContext) throws Exception {
        Configuration configuration = new Configuration();

        for (TableVO table : generatorContext.getTables()) {
            formatter.resolveProperty(table, generatorContext.buildResolveParams());
            Context context = new Context(ModelType.CONDITIONAL);
            configuration.addContext(context);

            context.addProperty("autoDelimitKeywords", "true");
            context.addProperty("beginningDelimiter", "`");
            context.addProperty("endingDelimiter", "`");

            context.addProperty("javaFileEncoding", "UTF-8");


            String connectorLibPath = ConfigHelper.findConnectorLibPath(table.getJdbcConnection().getDbType());
            configuration.addClasspathEntry(connectorLibPath);
            LOGGER.info("connectorLibPath: {}", connectorLibPath);

            // TableVO configuration
            TableConfiguration tableConfig = new TableConfiguration(context);
            tableConfig.setTableName(table.getTableName());
            tableConfig.setDomainObjectName(table.getDomainObjectName());

            if (generatorContext.getConfiguration().isUseExample()) {
                tableConfig.setUpdateByExampleStatementEnabled(false);
                tableConfig.setCountByExampleStatementEnabled(false);
                tableConfig.setDeleteByExampleStatementEnabled(false);
                tableConfig.setSelectByExampleStatementEnabled(false);
            }

            if (JdbcType.MySQL.name().equals(table.getJdbcConnection().getDbType())) {
                tableConfig.setSchema(table.getJdbcConnection().getSchema());
            } else {
                tableConfig.setCatalog(table.getJdbcConnection().getSchema());
            }

            if (generatorContext.getConfiguration().isUseSchemaPrefix()) {
                if (JdbcType.MySQL.name().equals(table.getJdbcConnection().getDbType())) {
                    tableConfig.setSchema(table.getJdbcConnection().getSchema());
                } else if (JdbcType.Oracle.name().equals(table.getJdbcConnection().getDbType())) {
                    //Oracle的schema为用户名，如果连接用户拥有dba等高级权限，若不设schema，会导致把其他用户下同名的表也生成一遍导致mapper中代码重复
                    tableConfig.setSchema(table.getJdbcConnection().getUsername());
                } else {
                    tableConfig.setCatalog(table.getJdbcConnection().getSchema());
                }
            }

            // 针对 postgresql 单独配置
            if (JdbcType.valueOf(table.getJdbcConnection().getDbType()).getDriverClass() == "org.postgresql.Driver") {
                tableConfig.setDelimitIdentifiers(true);
            }

            //添加GeneratedKey主键生成
            if (StringUtils.isNoneEmpty(table.getGenerateKeys())) {
                String dbType = table.getJdbcConnection().getDbType();
                if (JdbcType.MySQL.name().equals(dbType)) {
                    dbType = "JDBC";
                    //dbType为JDBC，且配置中开启useGeneratedKeys时，Mybatis会使用Jdbc3KeyGenerator,
                    //使用该KeyGenerator的好处就是直接在一次INSERT 语句内，通过resultSet获取得到 生成的主键值，
                    //并很好的支持设置了读写分离代理的数据库
                    //例如阿里云RDS + 读写分离代理
                    //无需指定主库
                    //当使用SelectKey时，Mybatis会使用SelectKeyGenerator，INSERT之后，多发送一次查询语句，获得主键值
                    //在上述读写分离被代理的情况下，会得不到正确的主键
                }
                tableConfig.setGeneratedKey(new GeneratedKey(table.getGenerateKeys(), dbType, true, null));
            }

            if (table.getMapperName() != null) {
                tableConfig.setMapperName(table.getMapperName());
            }

            //add ignore columns
            if (table.getIgnoredColumns() != null) {
                table.getIgnoredColumns().stream().forEach(ignoredColumn -> {
                    tableConfig.addIgnoredColumn(ignoredColumn);
                });
            }
            if (table.getColumnOverrides() != null) {
                table.getColumnOverrides().stream().forEach(columnOverride -> {
                    tableConfig.addColumnOverride(columnOverride);
                });
            }
            if (generatorContext.getConfiguration().isUseActualColumnNames()) {
                tableConfig.addProperty("useActualColumnNames", "true");
            }

            if (generatorContext.getConfiguration().isUseTableNameAlias()) {
                tableConfig.setAlias(table.getTableName());
            }

            JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
            // http://www.mybatis.org/generator/usage/mysql.html
            if (JdbcType.MySQL.name().equals(table.getJdbcConnection().getDbType())) {
                jdbcConfig.addProperty("nullCatalogMeansCurrent", "true");
            }


            jdbcConfig.setDriverClass(JdbcType.valueOf(table.getJdbcConnection().getDbType()).getDriverClass());
            jdbcConfig.setConnectionURL(DbUtil.getConnectionUrlWithSchema(table.getJdbcConnection()));
            jdbcConfig.setUserId(table.getJdbcConnection().getUsername());
            jdbcConfig.setPassword(table.getJdbcConnection().getPassword());
            // java model
            JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
            modelConfig.setTargetPackage(table.getModelPackage());
            modelConfig.setTargetProject(generatorContext.getProjectFolder() + "/" + table.getModelPackageTargetFolder());
            // Mapper configuration
            SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();
            mapperConfig.setTargetPackage(table.getMappingXMLPackage());
            mapperConfig.setTargetProject(generatorContext.getProjectFolder() + "/" + table.getMappingXMLTargetFolder());
            // DAO
            JavaClientGeneratorConfiguration daoConfig = new JavaClientGeneratorConfiguration();
            daoConfig.setConfigurationType("XMLMAPPER");
            daoConfig.setTargetPackage(table.getDaoPackage());
            daoConfig.setTargetProject(generatorContext.getProjectFolder() + "/" + table.getDaoTargetFolder());


            context.setId(table.getJdbcConnection().getSchema());
            context.addTableConfiguration(tableConfig);
            context.setJdbcConnectionConfiguration(jdbcConfig);
            context.setJdbcConnectionConfiguration(jdbcConfig);
            context.setJavaModelGeneratorConfiguration(modelConfig);
            context.setSqlMapGeneratorConfiguration(mapperConfig);
            context.setJavaClientGeneratorConfiguration(daoConfig);
            context.setTargetRuntime("MyBatis3");
            // Comment
            CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
            commentConfig.setConfigurationType(DbRemarksCommentGenerator.class.getName());
            if (generatorContext.getConfiguration().isComment()) {
                commentConfig.addProperty("columnRemarks", "true");
            }
            if (generatorContext.getConfiguration().isAnnotation()) {
                commentConfig.addProperty("annotations", "true");
            }
            context.setCommentGeneratorConfiguration(commentConfig);
            // set java file encoding
            context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, generatorContext.getConfiguration().getEncoding());

            //实体添加序列化
            PluginConfiguration serializablePluginConfiguration = new PluginConfiguration();
            serializablePluginConfiguration.addProperty("type", "org.mybatis.generator.plugins.SerializablePlugin");
            serializablePluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
            context.addPluginConfiguration(serializablePluginConfiguration);
            // toString, hashCode, equals插件
            if (generatorContext.getConfiguration().isNeedToStringHashcodeEquals()) {
                PluginConfiguration pluginConfiguration1 = new PluginConfiguration();
                pluginConfiguration1.addProperty("type", "org.mybatis.generator.plugins.EqualsHashCodePlugin");
                pluginConfiguration1.setConfigurationType("org.mybatis.generator.plugins.EqualsHashCodePlugin");
                context.addPluginConfiguration(pluginConfiguration1);
                PluginConfiguration pluginConfiguration2 = new PluginConfiguration();
                pluginConfiguration2.addProperty("type", "org.mybatis.generator.plugins.ToStringPlugin");
                pluginConfiguration2.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
                context.addPluginConfiguration(pluginConfiguration2);
            }
            // limit/offset插件
            if (generatorContext.getConfiguration().isOffsetLimit()) {
                if (JdbcType.MySQL.name().equals(table.getJdbcConnection().getDbType())
                        || JdbcType.PostgreSQL.name().equals(table.getJdbcConnection().getDbType())) {
                    PluginConfiguration pluginConfiguration = new PluginConfiguration();
                    pluginConfiguration.addProperty("type", MySQLLimitPlugin.class.getName());
                    pluginConfiguration.setConfigurationType(MySQLLimitPlugin.class.getName());
                    context.addPluginConfiguration(pluginConfiguration);
                }
            }
            //for JSR310
            if (generatorContext.getConfiguration().isJsr310Support()) {
                JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
                javaTypeResolverConfiguration.setConfigurationType(JavaTypeResolverJsr310Impl.class.getName());
                context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
            }
            //forUpdate 插件
            if (generatorContext.getConfiguration().isNeedForUpdate()) {
                if (JdbcType.MySQL.name().equals(table.getJdbcConnection().getDbType())
                        || JdbcType.PostgreSQL.name().equals(table.getJdbcConnection().getDbType())) {
                    PluginConfiguration pluginConfiguration = new PluginConfiguration();
                    pluginConfiguration.addProperty("type", MySQLForUpdatePlugin.class.getName());
                    pluginConfiguration.setConfigurationType(MySQLForUpdatePlugin.class.getName());
                    context.addPluginConfiguration(pluginConfiguration);
                }
            }
            //repository 插件
            if (generatorContext.getConfiguration().isAnnotationDAO()) {
                if (JdbcType.MySQL.name().equals(table.getJdbcConnection().getDbType())
                        || JdbcType.PostgreSQL.name().equals(table.getJdbcConnection().getDbType())) {
                    PluginConfiguration pluginConfiguration = new PluginConfiguration();
                    pluginConfiguration.addProperty("type", RepositoryPlugin.class.getName());
                    pluginConfiguration.setConfigurationType(RepositoryPlugin.class.getName());
                    context.addPluginConfiguration(pluginConfiguration);
                }
            }

            if (generatorContext.getConfiguration().isUseDAOExtendStyle()) {
                if (JdbcType.MySQL.name().equals(table.getJdbcConnection().getDbType())
                        || JdbcType.PostgreSQL.name().equals(table.getJdbcConnection().getDbType())) {
                    PluginConfiguration pluginConfiguration = new PluginConfiguration();
                    pluginConfiguration.addProperty("type", CommonDAOInterfacePlugin.class.getName());
                    pluginConfiguration.setConfigurationType(CommonDAOInterfacePlugin.class.getName());
                    context.addPluginConfiguration(pluginConfiguration);
                }
            }

            // if overrideXML selected, delete oldXML ang generate new one
            if (generatorContext.getConfiguration().isOverrideXML()) {
                String mappingXMLFilePath = getMappingXMLFilePath(generatorContext, table);
                File mappingXMLFile = new File(mappingXMLFilePath);
                if (mappingXMLFile.exists()) {
                    mappingXMLFile.delete();
                }
            }


            //TemplateFile插件
            for (TemplateFile templateFile : generatorContext.getTemplateConfig().getTemplateFileSet()) {
                PluginConfiguration pluginConfiguration = new PluginConfiguration() {{

                    getProperties().putAll(generatorContext.buildProperties());
                    getProperties().put("templateFile", templateFile);
                    getProperties().put("formatter", formatter);
                    addProperty("targetProject", generatorContext.getProjectFolder() + "/" + templateFile.getTargetFolder());
                    setConfigurationType(TemplateFilePlugin.class.getName());
                }};
                context.addPluginConfiguration(pluginConfiguration);
            }

        }

        List<String> warnings = new ArrayList<>();
        Set<String> fullyQualifiedTableNames = new HashSet<>();
        Set<String> contexts = new HashSet<>();
        ShellCallback shellCallback = new DefaultShellCallback(true); // override=true
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, shellCallback, warnings);

        myBatisGenerator.generate(progressCallback, contexts, fullyQualifiedTableNames);

    }

    private String getMappingXMLFilePath(GeneratorContext generatorData, TableVO table) {
        StringBuilder sb = new StringBuilder();
        sb.append(generatorData.getProjectFolder()).append("/");
        sb.append(table.getMappingXMLTargetFolder()).append("/");
        String mappingXMLPackage = table.getMappingXMLPackage();
        if (StringUtils.isNotEmpty(mappingXMLPackage)) {
            sb.append(mappingXMLPackage.replace(".", "/")).append("/");
        }
        if (StringUtils.isNotEmpty(table.getMapperName())) {
            sb.append(table.getMapperName()).append(".xml");
        } else {
            sb.append(table.getDomainObjectName()).append("Mapper.xml");
        }

        return sb.toString();
    }

    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

}