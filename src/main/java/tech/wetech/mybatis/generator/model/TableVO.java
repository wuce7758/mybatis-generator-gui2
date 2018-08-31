package tech.wetech.mybatis.generator.model;

import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.IgnoredColumn;

import java.util.List;

/**
 * @author cjbi
 */
public class TableVO {

    private String tableName;

    private String domainObjectName;

    private String generateKeys;

    private String modelPackage;

    private String modelPackageTargetFolder;

    private String daoPackage;

    private String daoTargetFolder;

    private String mapperName;

    private String mappingXMLPackage;

    private String mappingXMLTargetFolder;

    private JdbcConnection jdbcConnection;

    private List<IgnoredColumn> ignoredColumns;

    private List<ColumnOverride> columnOverrides;

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getGenerateKeys() {
        return generateKeys;
    }

    public void setGenerateKeys(String generateKeys) {
        this.generateKeys = generateKeys;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDomainObjectName() {
        return domainObjectName;
    }

    public void setDomainObjectName(String domainObjectName) {
        this.domainObjectName = domainObjectName;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public String getModelPackageTargetFolder() {
        return modelPackageTargetFolder;
    }

    public void setModelPackageTargetFolder(String modelPackageTargetFolder) {
        this.modelPackageTargetFolder = modelPackageTargetFolder;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    public String getDaoTargetFolder() {
        return daoTargetFolder;
    }

    public void setDaoTargetFolder(String daoTargetFolder) {
        this.daoTargetFolder = daoTargetFolder;
    }

    public String getMappingXMLPackage() {
        return mappingXMLPackage;
    }

    public void setMappingXMLPackage(String mappingXMLPackage) {
        this.mappingXMLPackage = mappingXMLPackage;
    }

    public String getMappingXMLTargetFolder() {
        return mappingXMLTargetFolder;
    }

    public void setMappingXMLTargetFolder(String mappingXMLTargetFolder) {
        this.mappingXMLTargetFolder = mappingXMLTargetFolder;
    }

    public JdbcConnection getJdbcConnection() {
        return jdbcConnection;
    }

    public void setJdbcConnection(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    public List<IgnoredColumn> getIgnoredColumns() {
        return ignoredColumns;
    }

    public void setIgnoredColumns(List<IgnoredColumn> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }

    public List<ColumnOverride> getColumnOverrides() {
        return columnOverrides;
    }

    public void setColumnOverrides(List<ColumnOverride> columnOverrides) {
        this.columnOverrides = columnOverrides;
    }
}
