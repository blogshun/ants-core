package cn.jants.plugin.jms;

/**
 * @author MrShun
 * @version 1.0
 */
public interface JmsConsumer {

    /**
     * 队列到达
     * @param message 消息
     */
    void received(String message);
}
