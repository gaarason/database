package gaarason.database.contracts.builder;

import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.exception.SQLRuntimeException;
import gaarason.database.support.Collection;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Execute<T> {

    @Nullable
    Collection<T> first() throws SQLRuntimeException;

    Collection<T> firstOrFail() throws EntityNotFoundException, SQLRuntimeException;

    Collection<T> get();

    int insert();

    int insert(T entity);

    int insert(List<T> entityList);

    int update();

    int update(T entity);

    int delete();

}
