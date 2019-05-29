package gaarason.database.connections;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Setter
    @Getter
    private boolean inTransaction = false;

    /**
     * 是否写连接
     */
    @Setter
    @Getter
    private boolean isWrite = false;

    /**
     * 线程安全
     */
    private Map<String, Integer> localThreadMasterConnectionIndexList = new HashMap<>();

    /**
     * 线程安全
     */
    private Map<String, Integer> localThreadSlaveConnectionIndexList = new HashMap<>();

    public ProxyDataSource(List<DataSource> masterDataSourceList, List<DataSource> slaveDataSourceList){
        this.masterDataSourceList = masterDataSourceList;
        this.slaveDataSourceList = slaveDataSourceList;
        hasSlave = true;
    }

    public ProxyDataSource(List<DataSource> masterDataSourceList){
        this.masterDataSourceList = masterDataSourceList;
        hasSlave = false;
    }

    /**
     * 得到 DataSource
     * @return DataSource
     */
    private DataSource getRealDataSource(){
        if(!hasSlave || inTransaction || isWrite){
            log.debug("---------------- write dataSource ---------------");
            return weightSelection(localThreadMasterConnectionIndexList, masterDataSourceList);
        }else{
            log.debug("---------------- read dataSource ---------------");
            return weightSelection(localThreadSlaveConnectionIndexList, slaveDataSourceList);
        }
    }

    /**
     * 一个线程绑定一个DataSource
     * @return 当前线程的DataSource
     */
    private DataSource weightSelection(Map<String, Integer> localThreadConnectionIndexList,
                                       List<DataSource> dataSourceList) {
        int i = theLocalThreadConnectionIndex(localThreadConnectionIndexList, dataSourceList.size());
        log.debug("---------------- use dataSourceList the {}  ---------------", i);
        return dataSourceList.get(i);
    }

    /**
     * 获索引,可以保证线程安全
     * @param localThreadConnectionIndexList 线程安全的DataSource索引的List
     * @param size 尺寸
     * @return 索引
     */
    private int theLocalThreadConnectionIndex(Map<String, Integer> localThreadConnectionIndexList, int size) {
        String threadFlag = Thread.currentThread().getName();
        if (!localThreadConnectionIndexList.containsKey(threadFlag)) {
            log.debug("---------------- 加入线程安全 {} ---------------", size);
            // todo 按权重选
            int    index  = size == 0 ? 0 : (new Random()).nextInt(size);
            localThreadConnectionIndexList.put(threadFlag, index);
        }
        return localThreadConnectionIndexList.get(threadFlag);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getRealDataSource().getConnection();
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
