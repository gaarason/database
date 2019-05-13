package gaarason.database.contracts.builder;

import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.exception.SQLRuntimeException;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface Execute<T> {

    @Nullable
    T first() throws SQLRuntimeException;

    T firstOrFail() throws EntityNotFoundException, SQLRuntimeException;

    List<T> get();

    int insert();

    int insert(T entity);

    int insert(List<T> entityList);

    int update();

    int update(T entity);

    int delete();

}
