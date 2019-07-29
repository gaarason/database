package gaarason.database.contracts.builder;

import gaarason.database.contracts.function.Chunk;
import gaarason.database.eloquent.Record;
import gaarason.database.eloquent.RecordList;
import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.exception.SQLRuntimeException;
import org.springframework.lang.Nullable;

import java.util.List;

public interface Execute<T> {

    @Nullable
    Record<T> first() throws SQLRuntimeException;

    Record<T> firstOrFail() throws EntityNotFoundException, SQLRuntimeException;

    RecordList<T> get() throws SQLRuntimeException;

    void dealChunk(Chunk<T> chunk) throws SQLRuntimeException;

    int insert() throws SQLRuntimeException;

    int insert(T entity) throws SQLRuntimeException;

    int insert(List<T> entityList) throws SQLRuntimeException;

    int update() throws SQLRuntimeException;

    int update(T entity) throws SQLRuntimeException;

    int delete() throws SQLRuntimeException;

    int restore() throws SQLRuntimeException;

    int forceDelete() throws SQLRuntimeException;
}
