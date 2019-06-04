package gaarason.database.eloquent;


import gaarason.database.contracts.function.SelectResult;
import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.exception.SQLRuntimeException;
import gaarason.database.support.Collection;
import lombok.Data;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;

@Data
public class Entity<T> {

    /**
     * 数据实体类
     */
    private Class<T> entityClass;

    /**
     * 数据实体
     */
    private T entity;

    private Model<T> model;

    public Entity (Class<T> entityClass, Model<T> model){
        this.entityClass = entityClass;
        this.model = model;
        try {
            this.entity = entityClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public Collection<T> all(String... column) throws SQLRuntimeException {
        return select(() -> model.newQuery().select(column).get());
    }

    public Collection<T> findOrFail(String id) throws EntityNotFoundException, SQLRuntimeException {
        return select(() -> model.newQuery().where("id", id).firstOrFail());
    }

    public Collection<T> findOrFail(String column, String value) throws EntityNotFoundException, SQLRuntimeException {
        return select(() -> model.newQuery().where(column, value).firstOrFail());
    }

    @Nullable
    public Collection<T> find(String id) {
        return select(() -> model.newQuery().where("id", id).first());
    }

    @Nullable
    public Collection<T> find(String column, String value) {
        return select(() -> model.newQuery().where(column, value).first());
    }

    /**
     * 查询事件使用
     * @param callback 查询实现
     * @return 收集器
     */
    private Collection<T> select(SelectResult<T> callback){
        Collection<T> tCollection = callback.get();
        model.retrieved();
        return tCollection;
    }

    public boolean save(){
        return model.newQuery().insert(entity) > 0;
    }

}
