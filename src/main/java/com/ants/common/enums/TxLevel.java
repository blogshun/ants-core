package com.ants.common.enums;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-05-19
 */
public enum TxLevel {

    /**
     * 允许脏读，也就是可能读取到其他会话中未提交事务修改的数据
     */
    READ_UNCOMMITTED(0),

    /**
     * 只能读取到已经提交的数据。Oracle等多数数据库默认都是该级别 (不重复读)
     */
    READ_COMMITTED(1),
    /**
     * 可重复读。在同一个事务内的查询都是事务开始时刻一致的，InnoDB默认级别。在SQL标准中，该隔离级别消除了不可重复读，但是还存在幻象读
     */
    REPEATED_READ(2),

    /**
     * 完全串行化的读，每次读都需要获得表级共享锁，读写相互都会阻塞
     */
    SERIALIZABLE(3);

    private int level;

    TxLevel(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }
}
