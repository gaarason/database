package gaarason.database.contracts.builder;

import gaarason.database.query.Builder;

public interface Lock<T> {

    /**
     * lock in share mode 不会阻塞其它事务读取被锁定行记录的值
     * 相对适用于,两张以及多张表存在业务关系时的一致性要求,性能稍好,但易发生死锁
     * @return 查询构造器
     */
    Builder<T> sharedLock();

    /**
     * for update 会阻塞其他锁定性读对锁定行的读取
     * 相对适用于,适用于操作同一张表时的一致性要求,性能稍弱,但不会发生死锁
     * @return 查询构造器
     */
    Builder<T> lockForUpdate();
}
