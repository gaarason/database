package gaarason.database.tool;

import gaarason.database.connections.ProxyDataSource;
import gaarason.database.eloquent.Model;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ToolModel extends Model<ToolModel.Inner> {

    @Resource(name = "proxyDataSourceSingle")
    protected ProxyDataSource dataSource;

    public ProxyDataSource getProxyDataSource() {
        return dataSource;
    }


    public static class Inner {


    }
}
