package gaarason.database.contracts.builder;

import gaarason.database.query.Builder;
import org.springframework.lang.Nullable;

public interface OrderBy<T> {

    Builder<T> orderBy(String column, gaarason.database.eloquent.OrderBy orderByType);

    Builder<T> orderBy(String column);

}
