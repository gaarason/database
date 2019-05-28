package gaarason.database.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.database.exception.EntityNewInstanceException;
import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.exception.TypeNotSupportedException;
import lombok.ToString;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@ToString
public class Collection<T> {

    /**
     * 元数据
     */
    private List<Map<String, Column>> metadataMapList;

    /**
     * 实体
     */
    private Class<T> entityClass;

    public Collection(Class<T> entityClass, List<Map<String, Column>> metadataMapList) {
        this.entityClass = entityClass;
        this.metadataMapList = metadataMapList;
    }

    public Collection(Class<T> entityClass, ResultSet resultSet, boolean throwEmpty)
        throws SQLException, EntityNotFoundException {
        this.entityClass = entityClass;
        this.metadataMapList = JDBCResultToMapList(resultSet, throwEmpty);
    }

    /**
     * 元数据
     * @return
     */
    public Map<String, Object> toMap() {
        return toMap(metadataMapList.get(0));
    }

    /**
     * @return
     */
    public List<Map<String, Object>> toMapMultidimensional() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map<String, Column> stringColumnMap : metadataMapList) {
            mapList.add(toMap(stringColumnMap));
        }
        return mapList;
    }

    /**
     * 元数据转String
     * @return eg:age=16&name=alice&sex=
     */
    public String toSearch() {
        Map<String, Object> stringObjectMap = toMap();
        Set<String>         keySet          = stringObjectMap.keySet();
        String[]            keyArray        = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String key : keyArray) {
            Object s = stringObjectMap.get(key);
            if (s != null) {
                sb.append(key).append("=").append(s.toString()).append("&");
            }
        }
        return rtrim(sb.toString(), "&");
    }

    /**
     * 元数据转String
     * @return eg:age=16&name=alice&sex=
     */
    public String toSearchMultidimensional() {
        List<Map<String, Object>> mapList = toMapMultidimensional();
        StringBuilder             sb      = new StringBuilder();
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> stringObjectMap = mapList.get(i);
            Set<String>         keySet          = stringObjectMap.keySet();
            String[]            keyArray        = keySet.toArray(new String[keySet.size()]);
            Arrays.sort(keyArray);
            for (String key : keyArray) {
                Object s = stringObjectMap.get(key);
                if (s != null) {
                    sb.append(i).append('[').append(key).append(']').append("=").append(s.toString()).append("&");
                }
            }
        }
        return rtrim(sb.toString(), "&");
    }

    /**
     * 元数据转json字符串
     * @return eg:{"subject":null,"sex":"","name":"小明明明","age":"16"}
     * @throws JsonProcessingException 元数据不可转json
     */
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(toMap());
    }

    /**
     * 元数据转json字符串
     * @return eg:[{"subject":null,"sex":"","name":"小明明明","age":"16"}]
     * @throws JsonProcessingException 元数据不可转json
     */
    public String toJsonMultidimensional() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(toMapMultidimensional());
    }

    /**
     * 元数据转实体对象
     * @return 实体对象
     */
    public T toObject() {
        return toObject(entityClass, metadataMapList.get(0));
    }

    /**
     * 元数据转指定实体对象
     * @return 指定实体对象
     */
    public <V> V toObject(Class<V> entityClass) {
        return toObject(entityClass, metadataMapList.get(0));
    }

    /**
     * 元数据转实体对象列表
     * @return 实体对象列表
     */
    public List<T> toObjectList() {
        List<T> objectList = new ArrayList<>();
        for (Map<String, Column> stringColumnMap : metadataMapList) {
            objectList.add(toObject(entityClass, stringColumnMap));
        }
        return objectList;
    }

    /**
     * 元数据转指定实体对象列表
     * @return 指定实体对象列表
     */
    public <V> List<V> toObjectList(Class<V> entityClass) {
        List<V> objectList = new ArrayList<>();
        for (Map<String, Column> stringColumnMap : metadataMapList) {
            objectList.add(toObject(entityClass, stringColumnMap));
        }
        return objectList;
    }

    /**
     * 将JDBC结果集转化为元数据
     * @param resultSet JDBC结果集
     * @return 元数据
     */
    private static List<Map<String, Column>> JDBCResultToMapList(ResultSet resultSet, boolean throwEmpty)
        throws SQLException, EntityNotFoundException {
        final ResultSetMetaData   resultSetMetaData  = resultSet.getMetaData();
        final int                 columnCountMoreOne = resultSetMetaData.getColumnCount() + 1;
        List<Map<String, Column>> mapList            = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Column> map = new HashMap<>();
            for (int i = 1; i < columnCountMoreOne; i++) {
                Column column = new Column();
                column.setName(resultSetMetaData.getColumnName(i));
                column.setValue(resultSet.getObject(column.getName()));
                column.setType(resultSetMetaData.getColumnType(i));
                column.setTypeName(resultSetMetaData.getColumnTypeName(i));
                column.setCount(resultSetMetaData.getColumnCount());
                column.setCatalogName(resultSetMetaData.getCatalogName(i));
                column.setClassName(resultSetMetaData.getColumnClassName(i));
                column.setDisplaySize(resultSetMetaData.getColumnDisplaySize(i));
                column.setLabel(resultSetMetaData.getColumnLabel(i));
                column.setSchemaName(resultSetMetaData.getSchemaName(i));
                column.setPrecision(resultSetMetaData.getPrecision(i));
                column.setScale(resultSetMetaData.getScale(i));
                column.setTableName(resultSetMetaData.getTableName(i));
                column.setAutoIncrement(resultSetMetaData.isAutoIncrement(i));
                column.setCaseSensitive(resultSetMetaData.isCaseSensitive(i));
                column.setSearchable(resultSetMetaData.isSearchable(i));
                column.setCurrency(resultSetMetaData.isCurrency(i));
                column.setNullable(resultSetMetaData.isNullable(i) == ResultSetMetaData.columnNullable);
                column.setSigned(resultSetMetaData.isSigned(i));
                column.setReadOnly(resultSetMetaData.isReadOnly(i));
                column.setWritable(resultSetMetaData.isWritable(i));
                column.setDefinitelyWritable(resultSetMetaData.isDefinitelyWritable(i));
                map.put(column.getName(), column);
            }
            mapList.add(map);
        }
        if (mapList.size() == 0 && throwEmpty) {
            throw new EntityNotFoundException();
        }
        return mapList;
    }

    /**
     * 将元数据map转化为普通map
     * @param stringColumnMap 元数据
     * @return 普通map
     */
    private static Map<String, Object> toMap(Map<String, Column> stringColumnMap) {
        Map<String, Object> map = new HashMap<>();
        for (Column value : stringColumnMap.values()) {
            map.put(value.getName(), value.getValue());
        }
        return map;
    }

    /**
     * 将元数据map赋值给实体对象
     * @param entityClass     实体类
     * @param stringColumnMap 元数据map
     * @return 实体对象
     */
    private static <T> T toObject(Class<T> entityClass, Map<String, Column> stringColumnMap)
        throws EntityNewInstanceException {
        Field[] fields = entityClass.getDeclaredFields();
        try {
            T entity = entityClass.newInstance();
            fieldAssignment(fields, stringColumnMap, entity);
            return entity;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EntityNewInstanceException(e.getMessage());
        }
    }

    /**
     * 将数据库查询结果赋值给entity的field
     * @param fields          属性
     * @param stringColumnMap 元数据map
     * @param entity          数据表实体对象
     */
    private static void fieldAssignment(Field[] fields, Map<String, Column> stringColumnMap, Object entity)
        throws TypeNotSupportedException {
        for (Field field : fields) {
            String columnName = columnName(field);
            Column column     = stringColumnMap.get(columnName);
            if (column == null) {
                continue;
            }
            field.setAccessible(true); // 设置属性是可访问
            try {
                field.set(entity, columnFill(field, column.getValue()));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new TypeNotSupportedException(e.getMessage());
            }
        }
    }

    /**
     * 用数据库字段填充类属性
     * @param field 属性
     * @param value 值
     * @return 数据库字段值, 且对应实体entity的数据类型
     */
    private static Object columnFill(Field field, Object value) {
        switch (field.getType().toString()) {
            case "class java.lang.Byte":
                return Byte.valueOf(value.toString());
            case "class java.lang.String":
                return value.toString();
            case "class java.lang.Integer":
                return new Integer(value.toString());
            default:
                return value;
        }
    }

    /**
     * 获取类属性对应的数据库字段名
     * @param field 属性
     * @return 数据库字段名
     */
    private static String columnName(Field field) {
        if (field.isAnnotationPresent(gaarason.database.eloquent.Column.class)) {
            gaarason.database.eloquent.Column column = field.getAnnotation(gaarason.database.eloquent.Column.class);
            if (!"".equals(column.name())) {
                return column.name();
            }
        }
        return field.getName();
    }

    /**
     * 移除字符串右侧的所有character
     * @param str       原字符串
     * @param character 将要移除的字符
     * @return 处理后的字符
     */
    private static String rtrim(String str, String character) {
        if (str.equals(""))
            return str;
        return str.substring(str.length() - 1).equals(character) ? rtrim(str.substring(0, str.length() - 1),
            character) : str;
    }

}
