package gaarason.database.contracts.builder;

import gaarason.database.query.Builder;
import org.springframework.lang.Nullable;

import java.util.List;

public interface Select<T> {

    Builder<T> select(String column);

    Builder<T> select(String ...column);

    Builder<T> select(List<String> columnList);

    Builder<T> selectFunction(String function, String parameter, @Nullable String alias);

}
