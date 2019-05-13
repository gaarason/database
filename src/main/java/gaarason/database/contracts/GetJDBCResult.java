package gaarason.database.contracts;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface GetJDBCResult<V> {

    /**
     *
     * @param resultSet jdbc结果集
     * @return 处理完的结果
     */
    V get(ResultSet resultSet) throws SQLException;
}
