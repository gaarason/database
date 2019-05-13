package gaarason.database.eloquent;

import gaarason.database.exception.InvalidEntityException;
import gaarason.database.query.Builder;
import gaarason.database.query.MySqlBuilder;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;

public class Model<T> {

    @Resource
    DataSource dataSource;

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
        return new MySqlBuilder<>(dataSource, this, entity);
    }

    public T find() {
        return null;
    }

    public T findOrFail() {
        return null;
    }


}
