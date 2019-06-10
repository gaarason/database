package gaarason.database.eloquent;

abstract class Eventing<T> extends SoftDeleting<T> {

    /**
     * 事件会在从数据库中获取已存在模型时触发
     */
    public void retrieved(Record<T> record) {

    }

    /**
     * 事件会当一个新模型被首次保存的时候触发
     * @return 继续操作
     */
    public boolean creating(Record<T> record) {
        return true;
    }

    /**
     * 事件会当一个新模型被首次保存后触发
     */
    public void created(Record<T> record) {

    }

    /**
     * 一个模型已经在数据库中存在并调用save
     * @return 继续操作
     */
    public boolean updating(Record<T> record) {
        return true;
    }

    /**
     * 一个模型已经在数据库中存在并调用save
     */
    public void updated(Record<T> record) {

    }

    /**
     * 无论是创建还是更新
     * @return 继续操作
     */
    public boolean saving(Record<T> record) {
        return true;
    }

    /**
     * 无论是创建还是更新
     */
    public void saved(Record<T> record) {

    }

    /**
     * 删除时
     * @return 继续操作
     */
    public boolean deleting(Record<T> record) {
        return true;
    }

    /**
     * 删除后
     */
    public void deleted(Record<T> record) {

    }

    /**
     * 恢复时
     * @return 继续操作
     */
    public boolean restoring(Record<T> record) {
        return true;
    }

    /**
     * 恢复后
     */
    public void restored(Record<T> record) {

    }
}
