package gaarason.database.eloquent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.database.exception.*;
import gaarason.database.support.Column;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Record<T> {

    /**
     * 元数据
     */
    private Map<String, Column> metadataMap;

    /**
     * 数据模型
     */
    private Model<T> model;

    /**
     * 数据实体类
     */
    private Class<T> entityClass;

    /**
     * 原数据实体
     */
    private T originalEntity;

    /**
     * 数据实体
     */
    @Getter
    @Setter
    protected T entity;

    /**
     * 主键值
     */
    private Object originalPrimaryKeyValue;

    /**
     * 是否已经绑定具体的数据
     */
    private boolean hasBind;

    /**
     * @param entityClass     数据实体类
     * @param model           数据模型
     * @param stringColumnMap 元数据
     */
    public Record(Class<T> entityClass, Model<T> model, Map<String, Column> stringColumnMap) {
        this.entityClass = entityClass;
        this.model = model;
        this.metadataMap = stringColumnMap;
        entity = originalEntity = toObject();
        hasBind = true;
    }

    /**
     * @param entityClass 数据实体类
     * @param model       数据模型
     */
    public Record(Class<T> entityClass, Model<T> model) {
        this.entityClass = entityClass;
        this.model = model;
        hasBind = false;
    }

    /**
     * 元数据
     * @return map
     */
    public Map<String, Object> toMap() {
        return toMap(metadataMap);
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
     * 元数据转json字符串
     * @return eg:{"subject":null,"sex":"","name":"小明明明","age":"16"}
     * @throws JsonProcessingException 元数据不可转json
     */
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(toMap());
    }

    /**
     * 元数据转实体对象
     * @return 实体对象
     */
    public T toObject() {
        return toObject(entityClass, metadataMap);
    }

    /**
     * 元数据转指定实体对象
     * @return 指定实体对象
     */
    public <V> V toObject(Class<V> entityClassCustom) {
        return toObject(entityClassCustom, metadataMap);
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
    private <V> V toObject(Class<V> entityClass, Map<String, Column> stringColumnMap)
        throws EntityNewInstanceException {
        Field[] fields = entityClass.getDeclaredFields();
        try {
            V entity = entityClass.newInstance();
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
    private void fieldAssignment(Field[] fields, Map<String, Column> stringColumnMap, Object entity)
        throws TypeNotSupportedException {
        for (Field field : fields) {

            String columnName = columnName(field);
            Column column     = stringColumnMap.get(columnName);
            if (column == null) {
                continue;
            }
            field.setAccessible(true); // 设置属性是可访问
            try {
                Object value = columnFill(field, column.getValue());
                field.set(entity, columnFill(field, value));
                // 主键值记录
                if (field.isAnnotationPresent(Primary.class)) {
                    originalPrimaryKeyValue = value;
                }
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

    /**
     * 新增或者更新
     * 新增情况下: saving -> creating -> created -> saved
     * 更新情况下: saving -> updating -> created -> updated
     * @return 执行成功
     */
    public boolean save() {
        if (!model.saving(this)) {
            return false;
        }
        boolean success = hasBind ? update() : insert();
        model.saved(this);
        return success;
    }

    public boolean delete() {
        if (!model.deleting(this)) {
            return false;
        }
        boolean success = model.newQuery().where(model.PrimaryKeyName, originalPrimaryKeyValue.toString()).delete() > 0;
        model.deleted(this);
        return success;
    }

    /**
     * 新增
     * 事件使用 creating created
     * @return 执行成功
     */
    private boolean insert() {
        if (!model.creating(this)) {
            return false;
        }
        boolean success = model.newQuery().insert(entity) > 0;
        model.created(this);
        return success;
    }

    /**
     * 更新
     * 事件使用 updating updated
     * @return 执行成功
     */
    private boolean update() {
        if (!model.updating(this)) {
            return false;
        }
        boolean success = model.newQuery()
            .where(model.PrimaryKeyName, originalPrimaryKeyValue.toString())
            .update(entity) > 0;
        model.updated(this);
        return success;
    }

}
