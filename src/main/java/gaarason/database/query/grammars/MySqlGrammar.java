package gaarason.database.query.grammars;

import gaarason.database.contracts.Grammar;
import gaarason.database.eloquent.SqlType;
import gaarason.database.exception.InvalidSQLTypeException;
import gaarason.database.utils.FormatUtil;

import java.util.ArrayList;
import java.util.List;

public class MySqlGrammar implements Grammar {

    private String table;

    private String data;

    private String from;

    private String select;

    private String column;

    private String where;

    private String orderBy;

    private String group;

    private String limit;

    private List<String> valueList = new ArrayList<>();

    private List<String> whereParameterList = new ArrayList<>();

    private List<String> dataParameterList = new ArrayList<>();


    public MySqlGrammar(String tableName) {
        table = tableName;
    }

    @Override
    public void pushWhere(String something, String relationship) {
        if (where == null) {
            where = something;
        } else {
            where += FormatUtil.spaces(relationship) + something;
        }
    }

    @Override
    public void pushValue(String something) {
        valueList.add(something);
    }

    @Override
    public void pushFrom(String something) {
        from = something;
    }

    @Override
    public void pushSelect(String something) {
        if (select == null) {
            select = something;
        } else {
            select += ',' + something;
        }
    }

    @Override
    public void pushData(String something) {
        data = (data == null) ? something : data + ',' + something;
    }

    @Override
    public void pushOrderBy(String something) {
        if (orderBy == null) {
            orderBy = something;
        } else {
            orderBy += ',' + something;
        }
    }

    @Override
    public void pushLimit(String something) {
        limit = something;
    }

    @Override
    public void pushGroup(String something) {
        if (group == null) {
            group = something;
        } else {
            group += ',' + something;
        }
    }

    @Override
    public void pushColumn(String something) {
        column = (column == null) ? something : column + ',' + something;
    }

    private String dealSelect() {
        return null == select ? "*" : select;
    }

    private String dealColumn() {
        return null == select ? "" : FormatUtil.bracket(select);
    }

    private String dealFromSelect() {
        return null == from ? " from " + dealTable() : " from " + from;
    }

    private String dealFrom() {
        return null == from ? dealTable() : from;
    }

    private String dealTable(){
        return FormatUtil.backQuote(table);
    }

    private String dealWhere(SqlType sqlType) {
        String whereKeyword = sqlType == SqlType.SUBQUERY ? "" : " where ";
        return null == where ? "" : whereKeyword + where;
    }

    private String dealData() {
        return null == data ? "" : data;
    }

    private String dealJoin() {
        return "";
    }

    private String dealGroup() {
        return null == group ? "" : " group by " + group;
    }

    private String dealHaving() {
        return "";
    }

    private String dealOrderBy() {
        return orderBy == null ? "" : " order by " + orderBy;
    }

    private String dealLock() {
        return "";
    }

    private String dealValue() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : valueList) {
            stringBuilder.append(value).append(',');
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }

    private String dealLimit() {
        return limit == null ? "" : (" limit " + limit);
    }

    @Override
    public String generateSql(SqlType sqlType) {
        String sql;
        switch (sqlType) {
            case SELECT:
                sql = "select " + dealSelect() + dealFromSelect();
                break;
            case UPDATE:
                sql = "update " + dealFrom() + " set" + dealData();
                break;
            case INSERT:
                sql = "insert into " + dealFrom() + dealColumn() + " values" + dealValue();
                break;
            case DELETE:
                sql = "delete from " + dealFrom();
                break;
            case REPLACE:
                sql = "replace into " + dealFrom();
                break;
            case SUBQUERY:
                sql = "";
                break;
            default:
                throw new InvalidSQLTypeException();
        }

        sql += dealJoin() + dealWhere(sqlType) + dealGroup() + dealHaving() + dealOrderBy() + dealLimit() + dealLock();

        return sql;
    }

    @Override
    public List<String> getParameterList() {
        dataParameterList.addAll(whereParameterList);
        return dataParameterList;
    }

//    @Override
//    public List<String> getDataParameterList() {
//        return dataParameterList;
//    }

    @Override
    public boolean hasWhere() {
        return null != where;
    }

    @Override
    public void pushWhereParameter(String value) {
        whereParameterList.add(value);
    }

    @Override
    public void pushDataParameter(String value) {
        dataParameterList.add(value);
    }
}
