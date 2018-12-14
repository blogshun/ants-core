package cn.jants.plugin.jms;

/**
 * @author MrShun
 * @version 1.0
 */
public interface JmsConsumer {

    /**
     * 队列到达
     *
     * @param message 消息
     */
    void received(String message);


    /**
     * 消息到达后出现异常
     *
     * @param e
     */
    void error(Exception e);
}
