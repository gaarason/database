package gaarason.database.eloquent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.database.exception.*;
import gaarason.database.support.Column;
import gaarason.database.utils.StringUtil;
import gaarason.database.utils.EntityUtil;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 记录对象
 * @param <T> 数据实体类
 */
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
    protected T entity;

    /**
     * 主键值
     */
    @Nullable
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
        init(stringColumnMap);
    }

    /**
     * @param entityClass 数据实体类
     * @param model       数据模型
     */
    public Record(Class<T> entityClass, Model<T> model) {
        this.entityClass = entityClass;
        this.model = model;
        init(new HashMap<>());
    }

    /**
     * 初始化数据
     * @param stringColumnMap 元数据
     */
    private void init(Map<String, Column> stringColumnMap) {
        this.metadataMap = stringColumnMap;
        entity = originalEntity = toObject();
        if (!stringColumnMap.isEmpty()) {
            hasBind = true;
            // aop通知
            model.retrieved(this);
        } else {
            hasBind = false;
        }

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
        return StringUtil.rtrim(sb.toString(), "&");
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

            String columnName = EntityUtil.columnName(field);
            Column column     = stringColumnMap.get(columnName);
            if (column == null) {
                continue;
            }
            field.setAccessible(true); // 设置属性是可访问
            try {
                Object value = columnFill(field, column.getValue());
                field.set(entity, value);
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
    @Nullable
    private static Object columnFill(Field field, @Nullable Object value) {
        if (value == null)
            return null;
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
     * 新增或者更新
     * 新增情况下: saving -> creating -> created -> saved
     * 更新情况下: saving -> updating -> updated -> saved
     * @return 执行成功
     */
    public boolean save() {
        // aop阻止
        if (!model.saving(this)) {
            return false;
        }
        // 执行
        boolean success = hasBind ? update() : insert();
        // aop通知
        model.saved(this);
        // 响应
        return success;
    }

    /**
     * 删除
     * deleting -> deleted
     * @return 执行成功
     */
    public boolean delete() {
        // 主键未知
        if (originalPrimaryKeyValue == null) {
            throw new PrimaryKeyNotFoundException();
        }
        // aop阻止
        if (!model.deleting(this)) {
            return false;
        }
        // 执行
        boolean success = model.newQuery().where(model.PrimaryKeyName, originalPrimaryKeyValue.toString()).delete() > 0;
        // 成功删除后后,刷新自身属性
        if (success) {
            this.metadataMap = new HashMap<>();
            entity = originalEntity = toObject();
            hasBind = false;
            // aop通知
            model.deleted(this);
        }
        // 响应
        return success;
    }

    /**
     * 恢复(成功恢复后将会刷新record)
     * restoring -> restored
     * @return 执行成功
     */
    public boolean restore() {
        return restore(true);
    }

    /**
     * 恢复
     * restoring -> restored
     * @return 执行成功
     */
    public boolean restore(boolean refresh) {
        // 主键未知
        if (originalPrimaryKeyValue == null) {
            throw new PrimaryKeyNotFoundException();
        }
        // aop阻止
        if (!model.restoring(this)) {
            return false;
        }
        // 执行
        boolean success = model.onlyTrashed()
            .where(model.PrimaryKeyName, originalPrimaryKeyValue.toString())
            .restore() > 0;
        // 成功恢复后,刷新自身属性
        if (success && refresh) {
            refresh();
        }
        // 响应
        return success;
    }

    /**
     * 刷新(重新从数据库获取)
     * retrieved
     * @return 执行成功
     */
    public Record<T> refresh() {
        // 主键未知
        if (originalPrimaryKeyValue == null) {
            throw new PrimaryKeyNotFoundException();
        }
        // 刷新自身属性
        init(model.withTrashed()
            .where(model.PrimaryKeyName, originalPrimaryKeyValue.toString())
            .firstOrFail().metadataMap);
        // 响应
        return this;
    }

    /**
     * 新增
     * 事件使用 creating created
     * @return 执行成功
     */
    private boolean insert() {
        // aop阻止
        if (!model.creating(this)) {
            return false;
        }
        // 执行
        boolean success = model.newQuery().insert(entity) > 0;
        // 成功插入后后,刷新自身属性
        if (success) {
            selfUpdate(entity, true);
            // aop通知
            model.created(this);
        }
        // 响应
        return success;
    }

    /**
     * 更新
     * 事件使用 updating updated
     * @return 执行成功
     */
    private boolean update() {
        // 主键未知
        if (originalPrimaryKeyValue == null) {
            throw new PrimaryKeyNotFoundException();
        }
        // aop阻止
        if (!model.updating(this)) {
            return false;
        }
        // 执行
        boolean success = model.newQuery()
            .where(model.PrimaryKeyName, originalPrimaryKeyValue.toString())
            .update(entity) > 0;
        // 成功更新后,刷新自身属性
        if (success) {
            selfUpdate(entity, false);
            // aop通知
            model.updated(this);
        }
        // 响应
        return success;
    }

    /**
     * 更新自身数据
     */
    private void selfUpdate(T entity, boolean insertType) {
        selfUpdateMetadataMap(entity, insertType);
        this.entity = originalEntity = toObject();
    }

    /**
     * 更新元数据
     * @param entity     数据实体
     * @param insertType 是否为更新操作
     */
    private void selfUpdateMetadataMap(T entity, boolean insertType) {
        for (Field field : entityClass.getDeclaredFields()) {
            Object value = EntityUtil.fieldGet(field, entity);
            if (EntityUtil.effectiveField(field, value, insertType)) {
                String columnName = EntityUtil.columnName(field);
                if (insertType) {
                    Column column = new Column();
                    column.setValue(value);
                    column.setName(columnName);
                    metadataMap.put(columnName, column);
                } else {
                    metadataMap.get(columnName).setValue(value);
                }
            }
        }
        hasBind = true;
    }

    /**
     * 转字符
     * @return 字符
     */
    public String toString(){
        return entity.toString();
    }
}
