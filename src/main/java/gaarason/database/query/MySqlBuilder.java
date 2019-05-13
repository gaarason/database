package gaarason.database.query;

import gaarason.database.contracts.Closure;
import gaarason.database.contracts.Grammar;
import gaarason.database.eloquent.Model;
import gaarason.database.eloquent.OrderBy;
import gaarason.database.eloquent.SqlType;
import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.query.grammars.MySqlGrammar;
import gaarason.database.utils.EntityUtil;
import gaarason.database.utils.FormatUtil;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MySqlBuilder<T> extends Builder<T> {

    /**
     * 查询数据库的字段
     */
    private List<String> columnList = new ArrayList<>();

    public MySqlBuilder(DataSource dataSourceModel, Model model, T entity) {
        super(dataSourceModel, model, entity);
    }

    @Override
    Grammar grammarFactory() {
        return new MySqlGrammar(EntityUtil.tableName(entity));
    }


    @Override
    public Builder<T> whereRaw(String sqlPart) {
        grammar.pushWhere(sqlPart, "and");
        return this;
    }

    @Override
    public Builder<T> where(String column, String symbol, String value) {
        String sqlPart = FormatUtil.column(column) + symbol + formatValue(value);
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> where(String column, String value) {
        return where(column, "=", value);
    }

    @Override
    public Builder<T> whereIn(String column, List<String> valueList) {
        String sqlPart = FormatUtil.column(column) + "in" + FormatUtil.bracket(formatValue(valueList));
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> whereNotIn(String column, List<String> valueList) {
        String sqlPart = FormatUtil.column(column) + "not in" + FormatUtil.bracket(formatValue(valueList));
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> whereBetween(String column, String min, String max) {
        String sqlPart = FormatUtil.column(column) + "between" + formatValue(min) + "and" + formatValue(max);
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> whereNotBetween(String column, String min, String max) {
        String sqlPart =
                FormatUtil.column(column) + "not between" + formatValue(min) + "and" + formatValue(max);
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> whereNull(String column) {
        String sqlPart = FormatUtil.column(column) + "is null";
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> whereNotNull(String column) {
        String sqlPart = FormatUtil.column(column) + "is not null";
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> whereExists(String sqlWhole) {
        String sqlPart = "exists " + FormatUtil.bracket(sqlWhole);
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> whereNotExists(String sqlWhole) {
        String sqlPart = "not exists " + FormatUtil.bracket(sqlWhole);
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> whereColumn(String column1, String symbol, String column2) {
        String sqlPart = FormatUtil.column(column1) + symbol + FormatUtil.column(column2);
        return whereRaw(sqlPart);
    }

    @Override
    public Builder<T> whereColumn(String column1, String column2) {
        return whereColumn(column1, "=", column2);
    }

    @Override
    public Builder<T> andWhere(Closure Closure) {
        return null;
    }

    @Override
    public Builder<T> orWhere(Closure Closure) {
        return null;
    }

    @Override
    public Builder<T> from(String table) {
        grammar.pushFrom(FormatUtil.column(table));
        return this;
    }

    @Override
    public Builder<T> select(String column) {
        columnList.add(column);
        String sqlPart = FormatUtil.column(column);
        grammar.pushSelect(sqlPart);
        return this;
    }

    @Override
    public Builder<T> select(String... columnArray) {
        for (String column : columnArray) {
            select(column);
        }
        return this;
    }

    @Override
    public Builder<T> select(List<String> columnList) {
        for (String column : columnList) {
            select(column);
        }
        return this;
    }

    @Override
    public Builder<T> selectFunction(String function, Runnable callback, @Nullable String alias) {
        // todo
        return null;
    }

    @Override
    public Builder<T> orderBy(String column, OrderBy type) {
        String sqlPart = FormatUtil.column(column) + " " + (type == OrderBy.ASC ? "asc" : "desc");
        grammar.pushOrderBy(sqlPart);
        return this;
    }

    @Override
    public Builder<T> orderBy(String column) {
        return orderBy(column, OrderBy.ASC);
    }

    @Override
    public Builder<T> limit(int offset, int take) {
        String sqlPart = String.valueOf(offset) + ',' + take;
        grammar.pushLimit(sqlPart);
        return this;
    }

    @Override
    public Builder<T> limit(int take) {
        String sqlPart = String.valueOf(take);
        grammar.pushLimit(sqlPart);
        return this;
    }


    @Override
    public Builder<T> groupRaw(String sqlPart) {
        grammar.pushGroup(sqlPart);
        return this;
    }

    @Override
    public Builder<T> group(String column) {
        String sqlPart = FormatUtil.column(column);
        return groupRaw(sqlPart);
    }

    @Override
    public Builder<T> group(List<String> columnList) {
        for (String column : columnList) {
            group(column);
        }
        return this;
    }

    @Override
    public T firstOrFail() throws EntityNotFoundException {
        limit(1);
        return querySql((resultSet) -> EntityUtil.setValueToEntity(entity, resultSet, columnList));
    }

    @Override
    @Nullable
    public T first() {
        try {
            return firstOrFail();
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<T> get() {
        return querySql((resultSet) -> EntityUtil.setValueToEntityList(model, resultSet, columnList));
    }

    @Override
    public int insert() {
        return updateSql(SqlType.INSERT);
    }

    @Override
    public int insert(T entity) {
        // 获取entity所有有效字段
        List<String> columnNameList = EntityUtil.columnNameList(entity);
        // 获取entity所有有效字段的值
        List<String> valueList = EntityUtil.valueList(entity);
        // 字段加入grammar
        select(columnNameList);
        // 字段的值加入grammar
        value(valueList);
        // 执行
        return insert();
    }

    @Override
    public int insert(List<T> entityList) {
        // 获取entity所有有效字段
        List<String> columnNameList = EntityUtil.columnNameList(entityList.get(0));
        List<List<String>> valueListList = new ArrayList<>();
        for (T entity : entityList){
            // 获取entity所有有效字段的值
            List<String> valueList = EntityUtil.valueList(entity);
            valueListList.add(valueList);
        }
        // 字段加入grammar
        select(columnNameList);
        // 字段的值加入grammar
        valueList(valueListList);
        // 执行
        return insert();
    }

    @Override
    public int update() {
        return updateSql(SqlType.UPDATE);
    }

    @Override
    public int update(T entity) {
        // 获取entity所有有效字段对其值得映射
        Map<String, String> stringStringMap = EntityUtil.columnValueMap(entity);

        data(stringStringMap);
        // 执行
        return update();
    }

    @Override
    public int delete() {
        return updateSql(SqlType.DELETE);
    }

    /**
     * 格式化参数类型,到绑定参数
     * @param value 参数
     * @return 参数占位符?
     */
    private String formatValue(String value) {
        return FormatUtil.value(value, grammar);
    }

    /**
     * 格式化参数类型,到绑定参数
     * @param value 参数
     * @return 参数占位符?
     */
    private String formatData(String value) {
        return FormatUtil.data(value, grammar);
    }

    /**
     * 格式化参数类型,到绑定参数
     * @param valueList 参数
     * @return 参数占位符?
     */
    private String formatValue(List<String> valueList) {
        return FormatUtil.value(valueList, grammar);
    }

    @Override
    public Builder<T> value(List<String> valueList) {
        StringBuilder sqlPartBuilder = new StringBuilder("(");
        for (String value : valueList) {
            String stub = FormatUtil.value(value, grammar);
            sqlPartBuilder.append(stub).append(',');
        }
        String sqlPart = sqlPartBuilder.deleteCharAt(sqlPartBuilder.length() - 1).append(')').toString();
        grammar.pushValue(sqlPart);
        return this;
    }

    @Override
    public Builder<T> valueList(List<List<String>> valueList) {
        for(List<String> value : valueList){
            value(value);
        }
        return this;
    }

    @Override
    public Builder<T> data(String sqlPart) {
        grammar.pushData(sqlPart);
        return this;
    }

    @Override
    public Builder<T> data(String column, String value) {
        String sqlPart = FormatUtil.column(column) + '=' + formatData(value);
        return data(sqlPart);
    }

    @Override
    public Builder<T> data(Map<String, String> map) {
        for (String column : map.keySet()) {
            data(column, map.get(column));
        }
        return this;
    }

    @Override
    public Builder<T> dataIncrement(String column, int steps) {
        String sqlPart = FormatUtil.column(column) + '=' + FormatUtil.column(column) + '+' + steps;
        return data(sqlPart);
    }

    @Override
    public Builder<T> dataDecrement(String column, int steps) {
        String sqlPart = FormatUtil.column(column) + '=' + FormatUtil.column(column) + '-' + steps;
        return data(sqlPart);
    }
}
