package cn.jants.plugin.jms;

import cn.jants.common.bean.JsonMap;

import javax.jms.*;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 */
public class ActiveMqTpl {

    private Session session;


    private ThreadLocal<Map<String, MessageProducer>> threadLocal = new ThreadLocal<>();

    public ActiveMqTpl(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    /**
     * 生产者发送消息, 或者序列化消息
     *
     * @param queueName 队列名称
     * @param message   消息
     */
    public void send(String queueName, String message) {
        //创建一个消息队列
        Queue queue;
        //消息生产者
        MessageProducer messageProducer;
        try {
            queue = session.createQueue(queueName);
            Map<String, MessageProducer> current = threadLocal.get();
            if (current != null) {
                messageProducer = current.get(queueName);
                if(messageProducer == null){
                    messageProducer = session.createProducer(queue);
                    threadLocal.set(JsonMap.newJsonMap().set(queueName, messageProducer));
                }
            } else {
                messageProducer = session.createProducer(queue);
                threadLocal.set(JsonMap.newJsonMap().set(queueName, messageProducer));
            }
            //创建一条消息
            TextMessage textMessage = session.createTextMessage(message);
            //发送消息
            messageProducer.send(textMessage);
            //提交事务
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
