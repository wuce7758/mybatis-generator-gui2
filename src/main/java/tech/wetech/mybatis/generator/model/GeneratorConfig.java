package tech.wetech.mybatis.generator.model;

/**
 * @author cjbi
 */
public class GeneratorConfig {

	private boolean offsetLimit;

	private boolean comment;

	private boolean overrideXML;

	private boolean needToStringHashcodeEquals;

	private boolean needForUpdate;

	private boolean annotationDAO;

	private boolean annotation;

	private boolean useActualColumnNames;

	private boolean useExample;

	private String encoding;

	private boolean useTableNameAlias;

	private boolean useDAOExtendStyle;

    private boolean useSchemaPrefix;

    private boolean jsr310Support;

    public boolean isJsr310Support() {
        return jsr310Support;
    }

    public void setJsr310Support(boolean jsr310Support) {
        this.jsr310Support = jsr310Support;
    }

    public boolean isUseSchemaPrefix() {
        return useSchemaPrefix;
    }

    public void setUseSchemaPrefix(boolean useSchemaPrefix) {
        this.useSchemaPrefix = useSchemaPrefix;
    }

	public boolean isUseExample() {
		return useExample;
	}

	public void setUseExample(boolean useExample) {
		this.useExample = useExample;
	}

	public boolean isOffsetLimit() {
		return offsetLimit;
	}

	public void setOffsetLimit(boolean offsetLimit) {
		this.offsetLimit = offsetLimit;
	}

	public boolean isComment() {
		return comment;
	}

	public void setComment(boolean comment) {
		this.comment = comment;
	}

    public boolean isNeedToStringHashcodeEquals() {
        return needToStringHashcodeEquals;
    }

    public void setNeedToStringHashcodeEquals(boolean needToStringHashcodeEquals) {
        this.needToStringHashcodeEquals = needToStringHashcodeEquals;
    }

	public boolean isNeedForUpdate() {
		return needForUpdate;
	}

	public void setNeedForUpdate(boolean needForUpdate) {
		this.needForUpdate = needForUpdate;
	}

	public boolean isAnnotationDAO() {
		return annotationDAO;
	}

	public void setAnnotationDAO(boolean annotationDAO) {
		this.annotationDAO = annotationDAO;
	}

	public boolean isAnnotation() {
		return annotation;
	}

	public void setAnnotation(boolean annotation) {
		this.annotation = annotation;
	}

	public boolean isUseActualColumnNames() {
		return useActualColumnNames;
	}

	public void setUseActualColumnNames(boolean useActualColumnNames) {
		this.useActualColumnNames = useActualColumnNames;
	}

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

	public void setUseTableNameAlias(boolean useTableNameAlias) {
		this.useTableNameAlias = useTableNameAlias;
	}

	public boolean isUseTableNameAlias() {
		return useTableNameAlias;
	}

	public boolean isOverrideXML() {
		return overrideXML;
	}

	public void setOverrideXML(boolean overrideXML) {
		this.overrideXML = overrideXML;
	}

	public void setUseDAOExtendStyle(boolean useDAOExtendStyle) {
		this.useDAOExtendStyle = useDAOExtendStyle;
	}

	public boolean isUseDAOExtendStyle() {
		return useDAOExtendStyle;
	}
}
