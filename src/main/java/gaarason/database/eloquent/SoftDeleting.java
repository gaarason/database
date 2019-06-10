package gaarason.database.eloquent;

import gaarason.database.query.Builder;
import gaarason.database.utils.EntityUtil;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

abstract class SoftDeleting<T> extends Initializing<T> {

    /**
     * 是否启用软删除
     */
    protected boolean softDeleting() {
        return false;
    }

    /**
     * 删除(软/硬删除)
     * @param builder 查询构造器
     * @return 删除的行数
     */
    public int delete(Builder<T> builder) {
        return softDeleting() ? softDelete(builder) : builder.forceDelete();
    }

    /**
     * 软删除查询作用域(反)
     * @param builder 查询构造器
     */
    protected void scopeSoftDeleteOnlyTrashed(Builder<T> builder) {
        builder.where("is_deleted", "1");
    }

    /**
     * 软删除查询作用域(反)
     * @param builder 查询构造器
     */
    protected void scopeSoftDeleteWithTrashed(Builder<T> builder) {

    }

    /**
     * 软删除查询作用域
     * @param builder 查询构造器
     */
    protected void scopeSoftDelete(Builder<T> builder) {
        builder.where("is_deleted", "0");
    }


    /**
     * 软删除实现
     * @param builder 查询构造器
     * @return 删除的行数
     */
    protected int softDelete(Builder<T> builder) {
        return builder.data("is_deleted", "1").update();
    }
}
