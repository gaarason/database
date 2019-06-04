package gaarason.database.spring.boot.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

public class DatabaseDruidDataSourceBuilder {

    public static DatabaseDruidDataSourceBuilder create() {
        return new DatabaseDruidDataSourceBuilder();
    }

    //    @ConfigurationProperties("gaarason.database.druid")
    public DruidDataSource build(Map<String, Object> configMap) {
        return new DatabaseDruidDataSourceWrapper(configMap);
    }

    public DruidDataSource build() {
        return new DatabaseDruidDataSourceWrapper();
    }


}