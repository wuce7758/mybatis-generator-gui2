package ${templateInfo.packageName};

import ${tableClass.fullClassName};

public interface ${templateInfo.shortClassName} {

    void create${tableClass.shortClassName}(${tableClass.shortClassName} ${tableClass.variableName});

    void delete${tableClass.shortClassName}(${tableClass.pkFields[0].shortTypeName} id);

    void update${tableClass.shortClassName}(${tableClass.shortClassName} ${tableClass.variableName});

}