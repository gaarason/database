package gaarason.database.eloquent;

import gaarason.database.utils.EntityUtil;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

abstract class InitializeModel<T> {

    /**
     * 主键列名(并非一定是实体的属性名)
     */
    @Getter
    protected String PrimaryKeyName;

    /**
     * 主键自增
     */
    @Getter
    protected boolean PrimaryKeyIncrement;

    /**
     * 实体类型
     */
    Class<T> entityClass;

    InitializeModel() {
        entityClass = entityClass();
        analysisEntityClass();
    }

    /**
     * 得到实体类型
     * @return 实体类型
     */
    private Class<T> entityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 实体类型分析
     */
    private void analysisEntityClass() {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Primary.class)) {
                Primary primary = field.getAnnotation(Primary.class);
                PrimaryKeyIncrement = primary.increment();
                PrimaryKeyName = EntityUtil.columnName(field);
                break;
            }
        }
    }
}
