package gaarason.database.eloquent;

import gaarason.database.connections.ProxyDataSource;
import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.exception.SQLRuntimeException;
import gaarason.database.query.Builder;
import gaarason.database.query.MySqlBuilder;
import org.springframework.lang.Nullable;

import java.lang.reflect.ParameterizedType;

abstract public class Model<T> extends InitializeModel<T> {

    /**
     * @return dataSource代理
     */
    abstract public ProxyDataSource getProxyDataSource();

    /**
     * 得到实体类型
     * @return 实体类型
     */
//    private Class<T> entityClass() {
//        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//    }

//    public Record<T> newRecord() {
//        return new Record<>(entityClass, this);
//        return newQuery().
//    }

    /**
     * 事件会在从数据库中获取已存在模型时触发
     */
    public void retrieved(Record<T> record) {

    }

    /**
     * 事件会当一个新模型被首次保存的时候触发
     * @return 继续操作
     */
    public boolean creating(Record<T> record) {
        return true;
    }

    /**
     * 事件会当一个新模型被首次保存后触发
     */
    public void created(Record<T> record) {

    }

    /**
     * 一个模型已经在数据库中存在并调用save
     * @return 继续操作
     */
    public boolean updating(Record<T> record) {
        return true;
    }

    /**
     * 一个模型已经在数据库中存在并调用save
     */
    public void updated(Record<T> record) {

    }

    /**
     * 无论是创建还是更新
     * @return 继续操作
     */
    public boolean saving(Record<T> record) {
        return true;
    }

    /**
     * 无论是创建还是更新
     */
    public void saved(Record<T> record) {

    }

    /**
     * 删除时
     * @return 继续操作
     */
    public boolean deleting(Record<T> record) {
        return true;
    }

    /**
     * 删除后
     */
    public void deleted(Record<T> record) {

    }

    /**
     * 恢复时
     * @return 继续操作
     */
    public boolean restoring(Record<T> record) {
        return true;
    }

    /**
     * 恢复后
     */
    public void restored(Record<T> record) {

    }

    /**
     * 新的查询构造器
     * @return 查询构造器
     */
    public Builder<T> newQuery() {
        // todo 按连接类型,等等信息选择 builder
        ProxyDataSource proxyDataSource = getProxyDataSource();
        return new MySqlBuilder<>(proxyDataSource, this, entityClass);
    }

//    public Collection<T> all(String... column) throws SQLRuntimeException {
//        return newRecord().all(column);
//    }
//
//    public Collection<T> findOrFail(String id) throws EntityNotFoundException, SQLRuntimeException {
//        return newRecord().findOrFail(id);
//    }
//
//    public Collection<T> findOrFail(String column, String value) throws EntityNotFoundException, SQLRuntimeException {
//        return newRecord().findOrFail(column, value);
//    }
//
//    @Nullable
//    public Collection<T> find(String id) {
//        return newRecord().find(id);
//    }
//
//    @Nullable
//    public Collection<T> find(String column, String value) {
//        return newRecord().find(column, value);
//    }

}
