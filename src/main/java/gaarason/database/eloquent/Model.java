package gaarason.database.eloquent;

import gaarason.database.connections.ProxyDataSource;
import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.exception.SQLRuntimeException;
import gaarason.database.query.Builder;
import gaarason.database.query.MySqlBuilder;
import gaarason.database.support.Collection;
import org.springframework.lang.Nullable;

import java.lang.reflect.ParameterizedType;

abstract public class Model<T> {

    /**
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

    public Entity<T> newEntity() {
        return new Entity<>(entityClass(), this);
    }

    /**
     * 事件会在从数据库中获取已存在模型时触发
     * @return 继续操作
     */
    public boolean retrieving() {
        return true;
    }

    /**
     * 事件会在从数据库中获取已存在模型时触发
     */
    public void retrieved() {

    }

    /**
     * 事件会当一个新模型被首次保存的时候触发
     * @return 继续操作
     */
    public boolean creating() {
        return true;
    }

    /**
     * 事件会当一个新模型被首次保存后触发
     */
    public void created() {

    }

    /**
     * 一个模型已经在数据库中存在并调用save
     * @return 继续操作
     */
    public boolean updating() {
        return true;
    }

    /**
     * 一个模型已经在数据库中存在并调用save
     */
    public void updated() {

    }

    /**
     * 无论是创建还是更新
     * @return 继续操作
     */
    public boolean saving() {
        return true;
    }

    /**
     * 无论是创建还是更新
     */
    public void saved() {

    }

    /**
     * 删除时
     * @return 继续操作
     */
    public boolean deleting() {
        return true;
    }

    /**
     * 删除后
     */
    public void deleted() {

    }

    /**
     * 恢复时
     * @return 继续操作
     */
    public boolean restoring() {
        return true;
    }

    /**
     * 恢复后
     */
    public void restored() {

    }

    /**
     * 新的查询构造器
     * @return 查询构造器
     */
    public Builder<T> newQuery() {
        // todo 按连接类型,等等信息选择 builder
        ProxyDataSource proxyDataSource = getProxyDataSource();
        return new MySqlBuilder<>(proxyDataSource, this, entityClass());
    }

    public Collection<T> all(String... column) throws SQLRuntimeException {
        return newEntity().all(column);
    }

    public Collection<T> findOrFail(String id) throws EntityNotFoundException, SQLRuntimeException {
        return newEntity().findOrFail(id);
    }

    public Collection<T> findOrFail(String column, String value) throws EntityNotFoundException, SQLRuntimeException {
        return newEntity().findOrFail(column, value);
    }

    @Nullable
    public Collection<T> find(String id) {
        return newEntity().find(id);
    }

    @Nullable
    public Collection<T> find(String column, String value) {
        return newEntity().find(column, value);
    }

}
