package gaarason.database.query;

import gaarason.database.connections.ProxyConnection;
import gaarason.database.connections.ProxyDataSource;
import gaarason.database.contracts.function.GenerateSqlPart;
import gaarason.database.contracts.function.GetJDBCResult;
import gaarason.database.contracts.Grammar;
import gaarason.database.contracts.builder.*;
import gaarason.database.eloquent.Model;
import gaarason.database.eloquent.SqlType;
import gaarason.database.exception.ConfirmOperationException;
import gaarason.database.exception.SQLRuntimeException;
import gaarason.database.utils.FormatUtil;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
abstract public class Builder<T> implements Where<T>, Union<T>, Support<T>, From<T>, Execute<T>, Select<T>,
    OrderBy<T>, Limit<T>, Group<T>, Value<T>, Data<T>, Transaction<T> {

    /**
     * 数据实体
     */
    protected T entity;

    /**
     * 数据库连接
     */
    private ProxyDataSource dataSource;

    /**
     * 避免线程锁的connection缓存
     */
    private Map<String, Connection> localThreadConnectionList = new HashMap<>();

//    private static final ThreadLocal localConnection;

    /**
     * sql生成器
     */
    Grammar grammar;

    /**
     * 数据模型
     */
    Model<T> model;


    private void setLocalThreadConnection(Connection connection){
        String name = Thread.currentThread().getName();
        localThreadConnectionList.put(name , connection);
    }

    private Connection getLocalThreadConnection(){
        String name = Thread.currentThread().getName();
        return localThreadConnectionList.get(name);
    }


    public Builder(ProxyDataSource dataSource, Model<T> model, T entity) {
        this.dataSource = dataSource;
//        this.connection = connection;
        this.model = model;
        this.entity = entity;
        grammar = grammarFactory();
    }

    /**
     * 得到一个全新的查询构造器
     * @return 查询构造器
     */
    private Builder<T> getNewSelf() {
        return model.newQuery();
    }

    /**
     * 执行闭包生成sqlPart
     * @param closure 闭包
     * @return sqlPart eg:(`id`="3" and `age` between "12" and "19")
     */
    String generateSqlPart(GenerateSqlPart<T> closure) {
        return generateSql(closure, false);
    }

    /**
     * 执行闭包生成完整sql
     * @param closure 闭包
     * @return sqlPart eg:(select * from `student` where `id`="3" and `age` between "12" and "19")
     */
    String generateSql(GenerateSqlPart<T> closure) {
        return generateSql(closure, true);
    }

    /**
     * @return 数据库语句组装对象
     */
    abstract Grammar grammarFactory();

    /**
     * 执行sql, 处理jdbc结果集, 返回目标类型对象
     * @param callback jdbc结果集处理回调
     * @param <V>      返回的对象类型
     * @return 返回的对象
     */
    <V> V querySql(GetJDBCResult<V> callback) {
        try {
            Connection connection = theConnection(false);
            ResultSet  resultSet  = executeSql(connection, SqlType.SELECT).executeQuery();
            V          v          = callback.get(resultSet);
            if(!inTransaction()){
                connection.close();
            }
            return v;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public boolean begin() {
        try {
            dataSource.setInTransaction(true);
            Connection connection    = dataSource.getConnection();
            connection.setAutoCommit(false);
            setLocalThreadConnection(connection);
            if(!inTransaction()){
                connection.close();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean commit() {
        try {
            getLocalThreadConnection().commit();
            getLocalThreadConnection().close();
            dataSource.setInTransaction(false);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean rollBack() {
        try {
            getLocalThreadConnection().rollback();
            getLocalThreadConnection().close();
            dataSource.setInTransaction(false);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean inTransaction() {
        return dataSource.isInTransaction();
    }

    @Override
    public boolean transaction(Runnable runnable) {
        try {
            begin();
            runnable.run();
            return commit();
        }catch (Exception e){
            rollBack();
            return false;
        }
    }

    /**
     * 执行sql, 返回收影响的行数
     * @return 影响的行数
     */
    int updateSql(SqlType sqlType) {
        if (sqlType != SqlType.INSERT && !grammar.hasWhere())
            throw new ConfirmOperationException("You made a risky operation without where conditions, use where(1) " +
                "for sure");
        try {
            Connection connection    = theConnection(true);
            int        affectedLines = executeSql(connection, sqlType).executeUpdate();
            if(!inTransaction()){
                connection.close();
            }
            return affectedLines;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage(), e);
        }
    }

    private Connection theConnection(boolean isWrite) throws SQLException {
        if(inTransaction()){
            return getLocalThreadConnection();
        }else{
            dataSource.setWrite(isWrite);
            return dataSource.getConnection();
        }
    }

    /**
     * 执行sql
     * @param connection 数据库连接
     * @param sqlType    sql类型
     * @return 预执行对象
     */
    private PreparedStatement executeSql(Connection connection, SqlType sqlType) {
        try {
            String       sql           = grammar.generateSql(sqlType);
            List<String> parameterList = grammar.getParameterList();

            log.debug("sql : {}", sql);
            log.debug("parameterList : {}", parameterList);

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int               i                 = 1;
            for (String parameter : parameterList) {
                preparedStatement.setString(i++, parameter);
            }
            return preparedStatement;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 执行闭包生成sql
     * @param closure  闭包
     * @param wholeSql 是否生成完整sql
     * @return sql
     */
    private String generateSql(GenerateSqlPart<T> closure, boolean wholeSql) {
        Builder<T>   subBuilder    = closure.generate(getNewSelf());
        List<String> parameterList = subBuilder.grammar.getParameterList();
        for (String parameter : parameterList) {
            grammar.pushWhereParameter(parameter);
        }
        SqlType sqlType = wholeSql ? SqlType.SELECT : SqlType.SUBQUERY;
        return FormatUtil.bracket(subBuilder.grammar.generateSql(sqlType));
    }

}
