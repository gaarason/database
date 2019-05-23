package gaarason.database.models.base;

import gaarason.database.connections.ProxyDataSource;
import gaarason.database.eloquent.Model;

import javax.annotation.Resource;

public class SingleModel<T> extends Model<T> {

    @Resource(name = "proxyDataSourceSingle")
    protected ProxyDataSource dataSource;

    public ProxyDataSource getProxyDataSource(){
        return dataSource;
    }
}
