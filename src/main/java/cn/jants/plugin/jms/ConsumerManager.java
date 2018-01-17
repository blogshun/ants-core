package cn.jants.plugin.jms;

import cn.jants.common.annotation.service.Service;
import cn.jants.core.module.ServiceManager;
import cn.jants.core.utils.GenerateUtil;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
public class ConsumerManager {

    /**
     * 主机地址, 库名称, 密码
     */
    private String brokenUrl, username, password;

    private List<Class<?>> consumers;


    public ConsumerManager(String brokenUrl, String username, String password, List<Class<?>> consumers) {
        this.brokenUrl = brokenUrl;
        this.username = username;
        this.password = password;
        this.consumers = consumers;
    }


    public void start() {
        Connection connection;
        try {
            //创建一个链接工厂
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, brokenUrl);
            //从工厂中创建一个链接
            connection = connectionFactory.createConnection();
            //开启链接
            connection.start();
            //创建一个事务（这里通过参数可以设置事务的级别）
            Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

            for (Class<?> consumerCls : consumers) {
                JmsListener jmsListener = consumerCls.getAnnotation(JmsListener.class);
                Object target = consumerCls.newInstance();
                if (target instanceof JmsConsumer) {
                    //检测是否有Service
                    if(consumerCls.getAnnotation(Service.class) != null){
                        String serName = consumerCls.getName();
                        String key = GenerateUtil.createServiceKey(serName);
                        target = ServiceManager.getService(key);
                    }
                    String dest = jmsListener.destination();
                    Destination destination = session.createQueue(dest);
                    MessageConsumer consumer = session.createConsumer(destination);
                    // 注册消息监听
                    consumer.setMessageListener(new ConsumerListener(dest, target));
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
