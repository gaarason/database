package gaarason.database.contracts.function;

import gaarason.database.support.Collection;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface GetJDBCResultToCollection<V> {

    /**
     * 处理jdbc结果集
     * @param resultSet jdbc结果集
     * @return 处理完的结果
     */
    Collection<V> get(ResultSet resultSet) throws SQLException;
}
