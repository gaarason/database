package gaarason.database.utils;

import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Model;
import gaarason.database.eloquent.Table;
import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.exception.TypeNotSupportedException;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EntityUtil {

    /**
     * 通过entity解析对应的字段和值组成的map, 忽略值为null的项目
     * @param entity 数据表实体对象
     * @param <T>    数据表实体类
     * @return 字段对值的映射
     */
    public static <T> Map<String, String> columnValueMap(T entity) {
        Map<String, String> columnValueMap = new HashMap<>();
        Field[]             fields         = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object value = fieldGet(field, entity);
            if (value != null) {
                String columnName = columnName(field);
                if (value instanceof Date) {
                    SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String           dateString = formatter.format(value);
                    columnValueMap.put(columnName, dateString);
                } else
                    columnValueMap.put(columnName, value.toString());
            }

        }
        return columnValueMap;

    }

    /**
     * 通过entity解析对应的字段的值组成的list, 忽略值为null的项目
     * @param entity 数据表实体对象
     * @param <T>    数据表实体类
     * @return 字段的值组成的list
     */
    public static <T> List<String> valueList(T entity) {
        List<String> valueList = new ArrayList<>();
        Field[]      fields    = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object value = fieldGet(field, entity);
            if (value != null) {
                if (value instanceof Date) {
                    SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String           dateString = formatter.format(value);
                    valueList.add(dateString);
                } else
                    valueList.add(value.toString());
            }

        }
        return valueList;
    }

    /**
     * 通过entity解析对应的表名
     * @param entity 数据表实体对象
     * @param <T>    数据表实体类
     * @return 数据表名
     */
    public static <T> String tableName(T entity) {
        Class userCla = entity.getClass();
        if (userCla.isAnnotationPresent(Table.class)) {
            Table table = (Table) userCla.getAnnotation(Table.class);
            return table.name();
        }
        return "";
    }

    /**
     * 通过entity解析对应的字段组成的list,忽略值为null的项目
     * @param entity 数据表实体对象
     * @param <T>    数据表实体类
     * @return 字段组成的list
     */
    public static <T> List<String> columnNameList(T entity) {
        List<String> columnList = new ArrayList<>();
        Field[]      fields     = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (fieldGet(field, entity) != null) {
                columnList.add(columnName(field));
            }
        }
        return columnList;
    }

    /**
     * 将jdbc结果集赋值到entity
     * @param entity     数据表实体对象
     * @param resultSet  jdbc结果集
     * @param columnList 查询字段集
     * @param <T>        数据表实体类
     * @return entity
     * @throws SQLException
     * @throws EntityNotFoundException
     */
    public static <T> T setValueToEntity(T entity, ResultSet resultSet, List<String> columnList) throws SQLException,
        EntityNotFoundException {
        Field[] fields = entity.getClass().getDeclaredFields();
        if (!resultSet.next())
            throw new EntityNotFoundException();
        fieldAssignment(fields, columnList, resultSet, entity);
        return entity;
    }

    /**
     * 将map结果集赋值到entity
     * @param entity     数据表实体对象
     * @param resultMap  map结果集
     * @param <T>        数据表实体类
     * @return entity
     * @throws EntityNotFoundException
     */
    public static <T> T setValueToEntity(T entity, Map<String, Object> resultMap) throws EntityNotFoundException {
        Field[] fields = entity.getClass().getDeclaredFields();
        fieldAssignment(fields, resultMap, entity);
        return entity;
    }

    /**
     * 将jdbc结果集赋值到List
     * @param model      数据表模型
     * @param resultSet  jdbc结果集
     * @param columnList 查询字段集
     * @param <T>        数据表实体类
     * @return entityList
     * @throws SQLException
     */
    public static <T> List<T> setValueToEntityList(Model<T> model, ResultSet resultSet, List<String> columnList)
            throws SQLException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T       entity = model.newEntity();
            Field[] fields = entity.getClass().getDeclaredFields();
            fieldAssignment(fields, columnList, resultSet, entity);
            list.add(entity);
        }
        return list;
    }

    /**
     * 将数据库查询结果赋值给entity的field
     * @param fields     属性
     * @param columnList 查询字段集
     * @param resultSet  jdbc结果集
     * @param entity     数据表实体对象
     * @throws SQLException
     */
    private static void fieldAssignment(Field[] fields, List<String> columnList, ResultSet resultSet, Object entity)
        throws SQLException {
        for (Field field : fields) {
            // 非*, 且没在select中的字段则忽略,
            if (!columnList.contains(columnName(field)) && columnList.size() != 0) {
                continue;
            }
            field.setAccessible(true); // 设置些属性是可以访问的
            try {
                field.set(entity, columnFill(field, resultSet));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException();
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * 将数据库查询结果赋值给entity的field
     * @param fields     属性
     * @param resultMap  map结果集
     * @param entity     数据表实体对象
     */
    private static void fieldAssignment(Field[] fields, Map<String, Object> resultMap,
                                        Object entity) {
        final Set<String> columnSet = resultMap.keySet();
        for (Field field : fields) {
            // 非*, 且没在select中的字段则忽略,
            if (!columnSet.contains(columnName(field)) && columnSet.size() != 0) {
                continue;
            }
            field.setAccessible(true); // 设置些属性是可以访问的
            try {
                field.set(entity, columnFill(field, resultMap));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException();
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * 获取属性的值
     * @param field 属性
     * @param obj   对象
     * @return 值
     */
    @Nullable
    private static Object fieldGet(Field field, Object obj) {
        try {
            field.setAccessible(true); // 设置些属性是可以访问的
            return field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 获取类属性对应的数据库字段名
     * @param field 属性
     * @return 数据库字段名
     */
    private static String columnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            if (!"".equals(column.name())) {
                return column.name();
            }
        }
        return field.getName();
    }

    /**
     * 用数据库字段填充类属性
     * @param field 属性
     * @param resultSet jdbc结果集
     * @return 数据库字段值, 且对应实体entity的数据类型
     */
    private static Object columnFill(Field field, ResultSet resultSet) throws SQLException {
        String columnName = columnName(field);
        switch (field.getType().toString()) {
            case "class java.util.Date":
            case "class java.sql.Timestamp":
                return resultSet.getTimestamp(columnName);
            case "class java.sql.Date":
                return resultSet.getDate(columnName);
            case "class java.sql.Time":
                return resultSet.getTime(columnName);
            case "class java.lang.String":
                return resultSet.getString(columnName);
            case "class java.lang.Integer":
                return resultSet.getInt(columnName);
            case "class java.math.BigInteger":
                return resultSet.getInt(columnName);
            case "class java.math.BigDecimal":
                return resultSet.getBigDecimal(columnName);
            case "class java.lang.Long":
                return resultSet.getLong(columnName);
            case "class java.lang.Float":
                return resultSet.getFloat(columnName);
            case "class java.lang.Double":
                return resultSet.getDouble(columnName);
            case "class java.lang.Boolean":
                return resultSet.getBoolean(columnName);
            case "class java.lang.Byte":
                return resultSet.getByte(columnName);
            case "class java.lang.Byte[]":
                return resultSet.getBytes(columnName);
            case "class java.lang.Short":
                return resultSet.getShort(columnName);
            default:
                throw new TypeNotSupportedException(field.getType().toString());
        }
    }

    /**
     * 用数据库字段填充类属性
     * @param field 属性
     * @param resultMap map结果集
     * @return 数据库字段值, 且对应实体entity的数据类型
     */
    private static Object columnFill(Field field, Map<String, Object> resultMap) {
        String columnName = columnName(field);
        return resultMap.get(columnName);
    }
}
