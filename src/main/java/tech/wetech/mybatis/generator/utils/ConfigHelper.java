package tech.wetech.mybatis.generator.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.mybatis.generator.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * XML based config file help class
 * <p>
 * Created by Owen on 6/16/16.
 */
public class ConfigHelper {

    private static final Logger _LOG = LoggerFactory.getLogger(ConfigHelper.class);
    private static final String BASE_DIR = "config";
    private static final String CONFIG_FILE = "/sqlite3.db";

    public static void createEmptyFiles() throws Exception {
        File file = new File(BASE_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        File uiConfigFile = new File(BASE_DIR + CONFIG_FILE);
        if (!uiConfigFile.exists()) {
            createEmptyXMLFile(uiConfigFile);
        }
    }

    static void createEmptyXMLFile(File uiConfigFile) throws IOException {
        InputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("sqlite3.db");
            fos = new FileOutputStream(uiConfigFile);
            byte[] buffer = new byte[1024];
            int byteread = 0;
            while ((byteread = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, byteread);
            }
        } finally {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
        }

    }

    public static <T> T loadConfig(String key, Class<T> clazz) throws Exception {
        return load(key, "configs", clazz);
    }

    private static <T> T load(String key, String tableName, Class<T> clazz) throws Exception {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery(String.format("SELECT * FROM %s WHERE key = '%s'", tableName, key));
            if (rs.next()) {
                String jsonStr = rs.getString("value");
                return JSON.parseObject(jsonStr, clazz);
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (stat != null) stat.close();
            if (conn != null) conn.close();
        }
    }

    public static void saveConfig(String key, Object object) throws Exception {
        save(key, "configs", object);
    }

    private static void save(String key, String tableName, Object object) throws Exception {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(String.format("SELECT * FROM %s WHERE key = '%s'", tableName, key));
            if (rs.next()) {
                stat = conn.createStatement();
                stat.executeUpdate(String.format("DELETE FROM %s WHERE key = '%s'", tableName, key));
            }
            stat = conn.createStatement();
            stat.executeUpdate(String.format("INSERT INTO %s (key, value) values('%s', '%s')", tableName, key, JSON.toJSONString(object)));
            conn.commit();
        } finally {
            conn.rollback();
            if (rs != null) rs.close();
            if (stat != null) stat.close();
            if (conn != null) conn.close();
        }
    }

    public static List<JdbcConnection> loadDatabaseConfig() throws Exception {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery("SELECT * FROM dbs");
            List<JdbcConnection> configs = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String value = rs.getString("value");
                JdbcConnection databaseConfig = JSON.parseObject(value, JdbcConnection.class);
                databaseConfig.setId(id);
                configs.add(databaseConfig);
            }

            return configs;
        } finally {
            if (rs != null) rs.close();
            if (stat != null) stat.close();
            if (conn != null) conn.close();
        }
    }

    public static void saveDatabaseConfig(boolean isUpdate, Integer primaryKey, JdbcConnection dbConfig) throws Exception {
        String configName = dbConfig.getName();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stat = conn.createStatement();
            if (!isUpdate) {
                ResultSet rs1 = stat.executeQuery("SELECT * from dbs where name = '" + configName + "'");
                if (rs1.next()) {
                    throw new RuntimeException("配置已经存在, 请使用其它名字");
                }
            }
            String jsonStr = JSON.toJSONString(dbConfig);
            String sql;
            if (isUpdate) {
                sql = String.format("UPDATE dbs SET name = '%s', value = '%s' where id = %d", configName, jsonStr, primaryKey);
            } else {
                sql = String.format("INSERT INTO dbs (name, value) values('%s', '%s')", configName, jsonStr);
            }
            stat.executeUpdate(sql);
        } finally {
            if (rs != null) rs.close();
            if (stat != null) stat.close();
            if (conn != null) conn.close();
        }
    }

    public static void deleteDatabaseConfig(JdbcConnection databaseConfig) throws Exception {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stat = conn.createStatement();
            String sql = String.format("delete from dbs where id=%d", databaseConfig.getId());
            stat.executeUpdate(sql);
        } finally {
            if (rs != null) rs.close();
            if (stat != null) stat.close();
            if (conn != null) conn.close();
        }
    }

    public static String findConnectorLibPath(String dbType) {
        JdbcType type = JdbcType.valueOf(dbType);
        URL resource = Thread.currentThread().getContextClassLoader().getResource("logback.xml");
        _LOG.info("jar resource: {}", resource);
        if (resource != null) {
            try {
                File file = new File(resource.toURI().getRawPath() + "/../libs/" + type.getConnectorJarFile());
                return URLDecoder.decode(file.getCanonicalPath(), Charset.forName("UTF-8").displayName());
            } catch (Exception e) {
                throw new RuntimeException("找不到驱动文件，请联系开发者");
            }
        } else {
            throw new RuntimeException("lib can't find");
        }
    }

    public static List<String> getAllJDBCDriverJarPaths() {
        List<String> jarFilePathList = new ArrayList<>();
        URL url = Thread.currentThread().getContextClassLoader().getResource("logback.xml");
        try {
            File file;
            if (url.getPath().contains(".jar")) {
                file = new File("libs/");
            } else {
                file = new File("src/main/resources/libs");
            }
            System.out.println(file.getCanonicalPath());
            File[] jarFiles = file.listFiles();
            System.out.println("jarFiles:" + jarFiles);
            if (jarFiles != null && jarFiles.length > 0) {
                for (File jarFile : jarFiles) {
                    if (jarFile.isFile() && jarFile.getAbsolutePath().endsWith(".jar")) {
                        jarFilePathList.add(jarFile.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("找不到驱动文件，请联系开发者");
        }
        return jarFilePathList;
    }


}
