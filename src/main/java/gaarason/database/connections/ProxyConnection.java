package gaarason.database.connections;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executor;

@Slf4j
public class ProxyConnection implements Connection {


    /**
     * 写连接
     */
    private DataSource writeDataSource;

    /**
     * 读连接
     */
    private List<DataSource> readDataSourceList;

    /**
     * 是否主从(读写分离)
     */
    private boolean hasSlave;
    /**
     * 是否处于数据库事物中
     */
    @Setter
    private boolean inTransaction = false;

    /**
     * 是否写连接
     */
    @Setter
    private boolean isWrite = false;

    public ProxyConnection(DataSource writeDataSource){
        this.writeDataSource = writeDataSource;
        hasSlave = false;
    }

    public ProxyConnection(DataSource writeDataSource, List<DataSource> readDataSourceList){
        this.writeDataSource = writeDataSource;
        this.readDataSourceList = readDataSourceList;
        hasSlave = true;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return getRealConnection().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getRealConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return getRealConnection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return getRealConnection().nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        getRealConnection().setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return getRealConnection().getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        getRealConnection().commit();
    }

    @Override
    public void rollback() throws SQLException {
        getRealConnection().rollback();
    }

    @Override
    public void close() throws SQLException {
getRealConnection().close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return getRealConnection().isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getRealConnection().getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        getRealConnection().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return getRealConnection().isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        getRealConnection().setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return getRealConnection().getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        getRealConnection().setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return getRealConnection().getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return getRealConnection().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
getRealConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return getRealConnection().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
        throws SQLException {
        return getRealConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getRealConnection().prepareCall(sql, resultSetType , resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getRealConnection().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getRealConnection().setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        getRealConnection().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return getRealConnection().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return getRealConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return getRealConnection().setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        getRealConnection().rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getRealConnection().releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException {
        return getRealConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        return getRealConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        return getRealConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getRealConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return getRealConnection().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return getRealConnection().prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return getRealConnection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return getRealConnection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return getRealConnection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return getRealConnection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return getRealConnection().isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        try {
            getRealConnection().setClientInfo( name, value);
        } catch (SQLClientInfoException var5) {
            throw var5;
        } catch (SQLException var6) {
            SQLClientInfoException clientInfoEx = new SQLClientInfoException();
            clientInfoEx.initCause(var6);
            throw clientInfoEx;
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            getRealConnection().setClientInfo(properties);
        } catch (SQLClientInfoException var5) {
            throw var5;
        } catch (SQLException var6) {
            SQLClientInfoException clientInfoEx = new SQLClientInfoException();
            clientInfoEx.initCause(var6);
            throw clientInfoEx;
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return getRealConnection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return getRealConnection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return getRealConnection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return getRealConnection().createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        getRealConnection().setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return getRealConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        getRealConnection().abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getRealConnection().setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return getRealConnection().getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getRealConnection().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getRealConnection().isWrapperFor(iface);
    }


    private Connection getRealConnection() throws SQLException {
        return getRealDataSource().getConnection();
    }

    private DataSource getRealDataSource(){
        if(!hasSlave || inTransaction || isWrite){
            log.info("---------------- write dataSource ---------------");
            return writeDataSource;
        }else{
            log.info("---------------- read dataSource ---------------");
            return weightSelection();
        }
    }

    private DataSource weightSelection(){

        // todo 按权重选
        Random random = new Random();
        int n = random.nextInt(readDataSourceList.size());
        log.info("---------------- read dataSourceList the {} : {} ---------------", n , readDataSourceList);
        return readDataSourceList.get(n);
    }
}
