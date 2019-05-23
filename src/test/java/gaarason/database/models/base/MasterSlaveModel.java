package gaarason.database.models.base;

import gaarason.database.connections.ProxyDataSource;
import gaarason.database.eloquent.Model;

import javax.annotation.Resource;

public class MasterSlaveModel<T> extends Model<T> {

    @Resource(name = "proxyDataSource")
    protected ProxyDataSource dataSource;

    public ProxyDataSource getProxyDataSource(){
        return dataSource;
    }

}
