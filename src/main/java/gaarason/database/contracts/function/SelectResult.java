package gaarason.database.contracts.function;

import gaarason.database.support.Collection;

public interface SelectResult<V> {

    Collection<V> get();
}
