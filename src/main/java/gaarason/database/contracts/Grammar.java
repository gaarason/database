package gaarason.database.contracts;

import gaarason.database.eloquent.SqlType;

import java.util.List;

public interface Grammar {

    void pushSelect(String something);

    void pushData(String something);

    void pushFrom(String something);

    void pushOrderBy(String something);

    void pushLimit(String something);

    void pushGroup(String something);

    void pushColumn(String something);

    void pushWhere(String something, String relationship);

    void pushValue(String something);

    String generateSql(SqlType sqlType);

    void pushWhereParameter(String value);

    void pushDataParameter(String value);

    List<String> getParameterList(SqlType sqlType);

//    List<String> getDataParameterList();

    boolean hasWhere();
}
