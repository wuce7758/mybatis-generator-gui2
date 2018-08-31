package tech.wetech.mybatis.generator.model;

import static java.util.Locale.ENGLISH;

/**
 * The parameters passed to the template.
 * @author cjbi
 */
public class TemplateInfo {

    private String variableName;

    private String lowerCaseName;

    private String shortClassName;

    private String fullClassName;

    private String packageName = "${groupId}${tableName}";

    public TemplateInfo() {

    }

    public TemplateInfo(Builder builder) {
        this.variableName = builder.variableName;
        this.packageName = builder.packageName;
        this.lowerCaseName = builder.lowerCaseName;
        this.shortClassName = builder.shortClassName;
        this.fullClassName = builder.fullClassName;
    }

    public static class Builder {

        // Required params
        private String variableName;
        private String packageName;

        //Option params
        private String lowerCaseName;
        private String shortClassName;
        private String fullClassName;

        /**
         * Returns a String which capitalizes the first letter of the string.
         */
        private String capitalize(String name) {
            if (name == null || name.length() == 0) {
                return name;
            }
            return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
        }


        public Builder(String variableName, String packageName) {
            this.variableName = variableName;
            this.packageName = packageName;

            this.lowerCaseName = variableName.toLowerCase();
            this.shortClassName = capitalize(variableName);
            this.fullClassName = packageName + "." + shortClassName;
        }

        public TemplateInfo build() {
            return new TemplateInfo(this);
        }

    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }

    public String getShortClassName() {
        return shortClassName;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public String getPackageName() {
        return packageName;
    }
}
