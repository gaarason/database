package gaarason.database.contracts.function;

import gaarason.database.eloquent.Record;

@FunctionalInterface
public interface Chunk<V> {

    /**
     * 分块处理
     * @param record 结果集
     */
    void deal(Record<V> record);
}
