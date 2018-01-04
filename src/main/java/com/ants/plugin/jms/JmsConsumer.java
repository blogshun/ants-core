package com.ants.plugin.jms;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-12-15
 */
public interface JmsConsumer {

    /**
     * 队列到达
     * @param message 消息
     */
    void received(String message);
}
