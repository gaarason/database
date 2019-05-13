package gaarason.database.query;

import gaarason.database.contracts.GetJDBCResult;
import gaarason.database.contracts.Grammar;
import gaarason.database.contracts.builder.*;
import gaarason.database.eloquent.Model;
import gaarason.database.eloquent.SqlType;
import gaarason.database.exception.ConfirmOperationException;
import gaarason.database.exception.SQLRuntimeException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

abstract public class Builder<T> implements Where<T>, Union<T>, Support<T>, From<T>, Execute<T>, Select<T>,
        OrderBy<T>, Limit<T>, Group<T>, Value<T>, Data<T> {

    protected DataSource dataSource;

    protected Grammar grammar;

    protected T entity;

    protected Model model;

    public Builder(DataSource dataSourceModel, Model parentModel, T EntityModel) {
        dataSource = dataSourceModel;
        model = parentModel;
        entity = EntityModel;
        grammar = grammarFactory();
    }

    abstract Grammar grammarFactory();

    /**
     * 执行sql, 处理jdbc结果集, 返回目标类型对象
     * @param callback jdbc结果集处理回调
     * @param <V>      返回的对象类型
     * @return 返回的对象
     */
    <V> V querySql(GetJDBCResult<V> callback) {
        try {
            Connection connection = dataSource.getConnection();
            ResultSet  resultSet  = executeSql(connection, SqlType.SELECT).executeQuery();
            V          v          = callback.get(resultSet);
            connection.close();
            return v;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 执行sql, 返回收影响的行数
     * @return 返回的对象
     */
    int updateSql(SqlType sqlType) {
        if (sqlType != SqlType.INSERT && !grammar.hasWhere())
            throw new ConfirmOperationException("You made a risky operation without where conditions, use where(1) " +
                    "for sure");
        try {
            Connection connection    = dataSource.getConnection();
            int        affectedLines = executeSql(connection, sqlType).executeUpdate();
            connection.close();
            return affectedLines;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage(), e);
        }
    }

    private PreparedStatement executeSql(Connection connection, SqlType sqlType) {
        try {
            String       sql                = grammar.generateSql(sqlType);
            List<String> whereParameterList = grammar.getParameterList();
            System.out.println("sql : " + sql);
            System.out.println("whereParameterList : " + whereParameterList);

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int               i                 = 1;
            for (String parameter : whereParameterList) {
                preparedStatement.setString(i++, parameter);
            }
            return preparedStatement;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage(), e);
        }
    }

}
