package gaarason.database.connections;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Slf4j
public class ProxyDataSource implements DataSource {

    /**
     * 写连接
     */
    @Getter
    private List<DataSource> masterDataSourceList;

    /**
     * 读连接
     */
    @Getter
    private List<DataSource> slaveDataSourceList;

    /**
     * 是否主从(读写分离)
     */
    private boolean hasSlave;

    /**
     * 是否处于数据库事物中
     */
    private ThreadLocal<Boolean> inTransaction = ThreadLocal.withInitial(() -> false);

    public boolean isInTransaction(){
        return inTransaction.get();
    }
    public void setInTransaction(){
        inTransaction.set(true);
    }
    public void setOutTransaction(){
//        inTransaction.set(false);
        inTransaction.remove();
    }

    /**
     * 是否写连接
     */
    @Setter
    @Getter
    private boolean isWrite = false;

    private ThreadLocal<DataSource> masterDataSource = ThreadLocal.withInitial(() -> {
        log.debug("---------------- 首次使用,将DataSource与该线程绑定,加入本地线程 w ---------------");
        // TODO 权重选择
        return masterDataSourceList.get((new Random()).nextInt(masterDataSourceList.size()));
    });

    private ThreadLocal<DataSource> slaveDataSource = ThreadLocal.withInitial(() -> {
        log.debug("---------------- 首次使用,将DataSource与该线程绑定,加入本地线程 r ---------------");
        // TODO 权重选择
        return slaveDataSourceList.get((new Random()).nextInt(slaveDataSourceList.size()));
    });

    public ProxyDataSource(List<DataSource> masterDataSourceList, List<DataSource> slaveDataSourceList) {
        this.masterDataSourceList = masterDataSourceList;
        this.slaveDataSourceList = slaveDataSourceList;
        hasSlave = true;
    }

    public ProxyDataSource(List<DataSource> masterDataSourceList) {
        this.masterDataSourceList = masterDataSourceList;
        hasSlave = false;
    }

    /**
     * 得到 DataSource
     * @return DataSource
     */
    private DataSource getRealDataSource() {
        return (!hasSlave || isWrite || isInTransaction()) ? masterDataSource.get() : slaveDataSource.get();
    }

    @Override
    public Connection getConnection() throws SQLException {
        DataSource realDataSource = getRealDataSource();
        return realDataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getRealDataSource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getRealDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getRealDataSource().isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getRealDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getRealDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getRealDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getRealDataSource().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getRealDataSource().getParentLogger();
    }

}
