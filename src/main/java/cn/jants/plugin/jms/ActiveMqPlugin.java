package cn.jants.plugin.jms;

import cn.jants.common.bean.Log;
import cn.jants.core.ext.Plugin;
import cn.jants.core.module.ServiceManager;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @author MrShun
 * @version 1.0
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
            //初始化RedisTpl
            ActiveMqTpl activeMqTpl = new ActiveMqTpl(connectionFactory);
            connection = activeMqTpl.getConnection();
            Log.debug("ActiveMq Jms 连接成功... ");
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
