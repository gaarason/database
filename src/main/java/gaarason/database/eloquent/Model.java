package gaarason.database.eloquent;

import gaarason.database.connections.ProxyDataSource;
import gaarason.database.query.Builder;
import gaarason.database.query.MySqlBuilder;

import java.lang.reflect.ParameterizedType;

abstract public class Model<T> {

    /**
     *
     * @return dataSource代理
     */
    abstract public ProxyDataSource getProxyDataSource();

    /**
     * 得到实体类型
     * @return 实体类型
     */
    private Class<T> entityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Builder<T> newQuery() {
        // todo 按连接类型,等等信息选择 builder
        ProxyDataSource proxyDataSource = getProxyDataSource();
        return new MySqlBuilder<>(proxyDataSource, this, entityClass());
    }

//    public T find() {
//        return null;
//    }
//
//    public T findOrFail() {
//        return null;
//    }


}
