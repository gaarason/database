package gaarason.database.contracts.function;

import gaarason.database.eloquent.Record;

public interface FilterRecordAttribute<V> {

    Object filter(Record<V> record);
}
