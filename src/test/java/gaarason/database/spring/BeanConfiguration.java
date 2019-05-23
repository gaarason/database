package gaarason.database.spring;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import gaarason.database.connections.ProxyDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@PropertySource("database.properties")
public class BeanConfiguration {

    @Bean(name = "dataSourceMaster0")
    @Primary
    @ConfigurationProperties(prefix = "database.master0")
    public DataSource dataSourceMaster0() {
        log.info("-------------------- database.master0 init ---------------------");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceMaster1")
    @ConfigurationProperties(prefix = "database.master1")
    public DataSource dataSourceMaster1() {
        log.info("-------------------- database.master1 init ---------------------");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceMaster2")
    @ConfigurationProperties(prefix = "database.master2")
    public DataSource dataSourceMaster2() {
        log.info("-------------------- database.master1 init ---------------------");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceSlave0")
    @ConfigurationProperties(prefix = "database.slave0")
    public DataSource dataSourceSlave0() {
        log.info("-------------------- database.slave0 init ---------------------");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceSlave1")
    @ConfigurationProperties(prefix = "database.slave1")
    public DataSource dataSourceSlave1() {
        log.info("-------------------- database.slave1 init ---------------------");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceSlave2")
    @ConfigurationProperties(prefix = "database.slave2")
    public DataSource dataSourceSlave2() {
        log.info("-------------------- database.slave2 init ---------------------");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceSlave3")
    @ConfigurationProperties(prefix = "database.slave3")
    public DataSource dataSourceSlave3() {
        log.info("-------------------- database.slave3 init ---------------------");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("dataSourceSlaveList")
    public List<DataSource> dataSourceSlaveList() {
        List<DataSource> dataSources = new ArrayList<>();
        dataSources.add(dataSourceSlave0());
        dataSources.add(dataSourceSlave1());
        dataSources.add(dataSourceSlave2());
        dataSources.add(dataSourceSlave3());
        return dataSources;
    }

    @Bean("dataSourceMasterList")
    public List<DataSource> dataSourceMasterList() {
        List<DataSource> dataSources = new ArrayList<>();
        dataSources.add(dataSourceMaster0());
        dataSources.add(dataSourceMaster1());
        dataSources.add(dataSourceMaster2());
        return dataSources;
    }


    //    @Bean
//    @ConfigurationProperties("spring.datasource.druid")
//    public DataSource dataSource(){
//        return DruidDataSourceBuilder.create().build();
//    }
//
//    @Bean
//    public DataSource proxyDataSource(@Qualifier("writeDataSource") DataSource writeDataSource) {
//        return (DataSource) (new ProxyFactory(writeDataSource).getProxyInstance());
//    }
//    @Bean
//    public DataSource proxyDataSource(@Qualifier("writeDataSource") DataSource writeDataSource, @Qualifier(
//        "readDataSourceList") List<DataSource> readDataSourceList) {
//        return readDataSourceList.isEmpty() ? new ProxyDataSource(writeDataSource) :
//            new ProxyDataSource(writeDataSource, readDataSourceList);
//    }

    @Bean
    public ProxyDataSource proxyDataSource(@Qualifier("dataSourceMasterList") List<DataSource> dataSourceMasterList, @Qualifier(
        "dataSourceSlaveList") List<DataSource> readDataSourceList) {
        return readDataSourceList.isEmpty() ? new ProxyDataSource(dataSourceMasterList) :
            new ProxyDataSource(dataSourceMasterList, readDataSourceList);
    }



    @Bean
    public List<DataSource> dataSourceMasterListSingle() {
        List<DataSource> dataSources = new ArrayList<>();
        dataSources.add(dataSourceSlave0());
        return dataSources;
    }

    @Bean
    public List<DataSource> dataSourceSlaveListSingle() {
        List<DataSource> dataSources = new ArrayList<>();
        return dataSources;
    }

    @Bean
    public ProxyDataSource proxyDataSourceSingle(@Qualifier("dataSourceMasterListSingle") List<DataSource> dataSourceMasterList, @Qualifier(
        "dataSourceSlaveListSingle") List<DataSource> readDataSourceList) {
        return readDataSourceList.isEmpty() ? new ProxyDataSource(dataSourceMasterList) :
            new ProxyDataSource(dataSourceMasterList, readDataSourceList);
    }

//    @Bean
//    public ProxyConnection proxyConnection(@Qualifier("dataSourceMasterList") List<DataSource> dataSourceMasterList, @Qualifier(
//        "dataSourceSlaveList") List<DataSource> readDataSourceList) {
//        return readDataSourceList.isEmpty() ? new ProxyConnection(dataSourceMasterList) :
//            new ProxyConnection(dataSourceMasterList, readDataSourceList);
//    }
}
