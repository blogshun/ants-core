package cn.jants.plugin.jms;

import cn.jants.common.bean.JsonMap;
import cn.jants.common.bean.Log;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 */
public class ActiveMqTpl {

    private ActiveMQConnectionFactory connectionFactory;

    private Connection connection;

    private Session session;

    private ThreadLocal<Map<String, MessageProducer>> threadLocal = new ThreadLocal<>();

    public ActiveMqTpl(ActiveMQConnectionFactory connectionFactory) throws JMSException {
        this.connectionFactory = connectionFactory;
        //从工厂中创建一个链接
        this.connection = connectionFactory.createConnection();
        //开启链接
        this.connection.start();
        //创建一个事务（这里通过参数可以设置事务的级别）
        Session session = connection.createSession(Boolean.TRUE, Session.SESSION_TRANSACTED);
        this.session = session;
    }

    public ActiveMQConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Connection getConnection() {
        return connection;
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
