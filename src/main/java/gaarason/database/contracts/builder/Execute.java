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

    Collection<T> get() throws SQLRuntimeException;

    int insert() throws SQLRuntimeException;

    int insert(T entity) throws SQLRuntimeException;

    int insert(List<T> entityList) throws SQLRuntimeException;

    int update() throws SQLRuntimeException;

    int update(T entity) throws SQLRuntimeException;

    int delete() throws SQLRuntimeException;

}
