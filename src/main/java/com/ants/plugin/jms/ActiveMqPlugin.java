package com.ants.plugin.jms;

import com.ants.common.bean.Log;
import com.ants.core.ext.Plugin;
import com.ants.core.module.ServiceManager;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-05
 */
public class ActiveMqPlugin implements Plugin {

    /**
     * 主机地址, 库名称, 密码
     */
    private String brokenUrl, username, password;

    private Connection connection;

    public ActiveMqPlugin(String brokenUrl, String username, String password) {
        this.brokenUrl = brokenUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean start() throws Exception {
        try {
            //创建一个链接工厂
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, brokenUrl);
            //从工厂中创建一个链接
            connection = connectionFactory.createConnection();
            //开启链接
            connection.start();
            //创建一个事务（这里通过参数可以设置事务的级别）
            Session session = connection.createSession(Boolean.TRUE, Session.SESSION_TRANSACTED);
            Log.debug("ActiveMq Jms 连接成功... ");

            //初始化RedisTpl
            ActiveMqTpl activeMqTpl = new ActiveMqTpl(session);
            ServiceManager.setService("plugin_jms_ActiveMqTpl", activeMqTpl);
            //检查是否有消费者, 存在则实例化消费者
            return true;
        } catch (Exception e) {
            Log.error("ActiveMq Jms 连接失败, 请认真检查配置 ... ", e.getMessage());
            throw new Exception(e);
        }
    }

    
    @Override
    public boolean destroy() {
        try {
            connection.close();
            return true;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return false;
    }
}
