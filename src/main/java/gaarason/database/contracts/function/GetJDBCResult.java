package gaarason.database.contracts.function;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface GetJDBCResult<V> {

    /**
     * 处理jdbc结果集
     * @param resultSet jdbc结果集
     * @return 处理完的结果
     */
    V get(ResultSet resultSet) throws SQLException;
}
