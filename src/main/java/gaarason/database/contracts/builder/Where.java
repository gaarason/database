package gaarason.database.contracts.builder;

import gaarason.database.contracts.Closure;
import gaarason.database.contracts.function.GenerateSqlPart;
import gaarason.database.query.Builder;

import java.util.List;

public interface Where<T> {

    /**
     * 加入sql片段
     * @param sqlPart sql片段
     * @return
     */
    Builder<T> whereRaw(String sqlPart);

    /**
     * 比较列与值
     * @param column 列名
     * @param symbol 比较关系
     * @param value  值
     * @return
     */
    Builder<T> where(String column, String symbol, String value);

    /**
     * 比较列与值相等
     * @param column 列名
     * @param value  值
     * @return
     */
    Builder<T> where(String column, String value);

    /**
     * 列值在范围内
     * @param column    列名
     * @param valueList 值所在的list
     * @return
     */
    Builder<T> whereIn(String column, List<String> valueList);

    /**
     * 列值不在范围内
     * @param column    列名
     * @param valueList 值所在的list
     * @return
     */
    Builder<T> whereNotIn(String column, List<String> valueList);

    /**
     * 列值在2值之间
     * @param column 列名
     * @param min    值1
     * @param max    值2
     * @return
     */
    Builder<T> whereBetween(String column, String min, String max);

    /**
     * 列值不在2值之间
     * @param column 列名
     * @param min 值1
     * @param max 值2
     * @return
     */
    Builder<T> whereNotBetween(String column, String min, String max);

    /**
     * 列值为null
     * @param column 列名
     * @return
     */
    Builder<T> whereNull(String column);

    /**
     * 列值不为null
     * @param column 列名
     * @return
     */
    Builder<T> whereNotNull(String column);

    Builder<T> whereExists(String sqlWhole);

    Builder<T> whereNotExists(String sqlWhole);

    /**
     * 比较字段与字段
     * @param column1 列1
     * @param symbol  比较关系
     * @param column2 列2
     * @return
     */
    Builder<T> whereColumn(String column1, String symbol, String column2);

    /**
     * 字段与字段相等
     * @param column1 列1
     * @param column2 列2
     * @return
     */
    Builder<T> whereColumn(String column1, String column2);

    /**
     * 且
     * @param Closure
     * @return
     */
    Builder<T> andWhere(GenerateSqlPart<T> Closure);

    /**
     * 或
     * @param Closure
     * @return
     */
    Builder<T> orWhere(GenerateSqlPart<T> Closure);

}
