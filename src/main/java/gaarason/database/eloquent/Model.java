package gaarason.database.eloquent;

import gaarason.database.connections.ProxyConnection;
import gaarason.database.connections.ProxyDataSource;
import gaarason.database.exception.InvalidEntityException;
import gaarason.database.query.Builder;
import gaarason.database.query.MySqlBuilder;

import java.lang.reflect.ParameterizedType;

abstract public class Model<T> {

    /**
     *
     * @return dataSource代理
     */
    abstract public ProxyDataSource getProxyDataSource();

    public T newEntity() {
        try {
            Class<T> entityClass =
                    (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return entityClass.newInstance();
        } catch (InstantiationException e) {
            throw new InvalidEntityException();
        } catch (IllegalAccessException e) {
            throw new InvalidEntityException();
        }
    }

    public Builder<T> newQuery() {
        T entity = newEntity();
        // todo 按连接类型,等等信息选择 builder
        ProxyDataSource proxyDataSource = getProxyDataSource();
        return new MySqlBuilder<>(proxyDataSource, this, entity);
    }

    public T find() {
        return null;
    }

    public T findOrFail() {
        return null;
    }


}
