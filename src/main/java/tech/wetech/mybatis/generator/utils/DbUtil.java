package tech.wetech.mybatis.generator.utils;

import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.mybatis.generator.exception.DbDriverLoadingException;
import tech.wetech.mybatis.generator.model.JdbcConnection;
import tech.wetech.mybatis.generator.model.JdbcType;
import tech.wetech.mybatis.generator.model.UITableColumnVO;

import java.sql.*;
import java.util.*;

/**
 * Created by Owen on 6/12/16.
 */
public class DbUtil {

    private static final Logger _LOG = LoggerFactory.getLogger(DbUtil.class);
    private static final int DB_CONNECTION_TIMEOUTS_SECONDS = 1;

    private static Map<JdbcType, Driver> drivers = new HashMap<>();

    public static Connection getConnection(JdbcConnection config) throws ClassNotFoundException, SQLException {
		JdbcType jdbcType = JdbcType.valueOf(config.getDbType());
		if (drivers.get(jdbcType) == null){
			loadDbDriver(jdbcType);
		}

		String url = getConnectionUrlWithSchema(config);
	    Properties props = new Properties();

	    props.setProperty("user", config.getUsername()); //$NON-NLS-1$
	    props.setProperty("password", config.getPassword()); //$NON-NLS-1$

		DriverManager.setLoginTimeout(DB_CONNECTION_TIMEOUTS_SECONDS);
	    Connection connection = drivers.get(jdbcType).connect(url, props);
        _LOG.info("getConnection, connection url: {}", connection);
        return connection;
    }

    public static List<String> getTableNames(JdbcConnection config) throws Exception {
        String url = getConnectionUrlWithSchema(config);
        _LOG.info("getTableNames, connection url: {}", url);
	    Connection connection = getConnection(config);
	    try {
		    List<String> tables = new ArrayList<>();
		    DatabaseMetaData md = connection.getMetaData();
		    ResultSet rs;
		    if (JdbcType.valueOf(config.getDbType()) == JdbcType.SQL_Server) {
			    String sql = "select name from sysobjects  where xtype='u' or xtype='v' ";
			    rs = connection.createStatement().executeQuery(sql);
			    while (rs.next()) {
				    tables.add(rs.getString("name"));
			    }
		    } else if (JdbcType.valueOf(config.getDbType()) == JdbcType.Oracle){
			    rs = md.getTables(null, config.getUsername().toUpperCase(), null, new String[] {"TABLE", "VIEW"});
		    } else if (JdbcType.valueOf(config.getDbType())==JdbcType.Sqlite){
		    	String sql = "Select name from sqlite_master;";
			    rs = connection.createStatement().executeQuery(sql);
			    while (rs.next()) {
				    tables.add(rs.getString("name"));
			    }
		    } 
		    else {
			    // rs = md.getTables(null, config.getUsername().toUpperCase(), null, null);


				rs = md.getTables(config.getSchema(), null, "%", new String[] {"TABLE", "VIEW"});			//针对 postgresql 的左侧数据表显示
		    }
		    while (rs.next()) {
			    tables.add(rs.getString(3));
		    }
		    return tables;
	    } finally {
	    	connection.close();
	    }
	}

    public static List<UITableColumnVO> getTableColumns(JdbcConnection dbConfig, String tableName) throws Exception {
        String url = getConnectionUrlWithSchema(dbConfig);
        _LOG.info("getTableColumns, connection url: {}", url);
		Connection conn = getConnection(dbConfig);
		try {
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getColumns(null, null, tableName, null);
			List<UITableColumnVO> columns = new ArrayList<>();
			while (rs.next()) {
				UITableColumnVO columnVO = new UITableColumnVO();
				String columnName = rs.getString("COLUMN_NAME");
				columnVO.setColumnName(columnName);
				columnVO.setJdbcType(rs.getString("TYPE_NAME"));
				columns.add(columnVO);
			}
			return columns;
		} finally {
			conn.close();
		}
	}

    public static String getConnectionUrlWithSchema(JdbcConnection dbConfig) throws ClassNotFoundException {
		JdbcType jdbcType = tech.wetech.mybatis.generator.model.JdbcType.valueOf(dbConfig.getDbType());
		String connectionUrl = String.format(jdbcType.getConnectionUrlPattern(), dbConfig.getHost(), dbConfig.getPort(), dbConfig.getSchema(), dbConfig.getEncoding());
        _LOG.info("getConnectionUrlWithSchema, connection url: {}", connectionUrl);
        return connectionUrl;
    }

	/**
	 * 加载数据库驱动
	 * @param jdbcType 数据库类型
	 */
	private static void loadDbDriver(JdbcType jdbcType){
		List<String> driverJars = ConfigHelper.getAllJDBCDriverJarPaths();
		ClassLoader classloader = ClassloaderUtility.getCustomClassloader(driverJars);
		try {
			Class clazz = Class.forName(jdbcType.getDriverClass(), true, classloader);
			Driver driver = (Driver) clazz.newInstance();
			_LOG.info("load driver class: {}", driver);
			drivers.put(jdbcType, driver);
		} catch (Exception e) {
			_LOG.error("load driver error", e);
			throw new DbDriverLoadingException("找不到" + jdbcType.getConnectorJarFile() + "驱动");
		}
	}
}
