package cn.jants.plugin.tool;

import cn.jants.common.bean.Log;
import cn.jants.common.utils.GenUtil;
import cn.jants.common.bean.Prop;
import cn.jants.common.utils.StrUtil;
import cn.jants.plugin.jms.ActiveMqTpl;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @author MrShun
 * @version 1.0
 */
public class ActiveMqTool extends ConcurrentToolMap {

    /**
     * 获取ActiveMq实例
     *
     * @param brokenUrl 链接地址
     * @param username  账号
     * @param password  密码
     * @return
     */
    public static ActiveMqTpl getActiveMq(String brokenUrl, String username, String password) {
        username = Prop.getKeyStrValue(username);
        password = Prop.getKeyStrValue(password);
        brokenUrl = Prop.getKeyStrValue(brokenUrl);
        String key = "activemq_".concat(GenUtil.makeMd5Str(brokenUrl, username, password));
        if (PLUGINS.containsKey(key)) {
            return (ActiveMqTpl) PLUGINS.get(key);
        }
        try {
            //创建一个链接工厂
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, brokenUrl);
            //从工厂中创建一个链接
            Connection connection = connectionFactory.createConnection();
            //开启链接
            connection.start();
            //创建一个事务（这里通过参数可以设置事务的级别）
            Session session = connection.createSession(Boolean.TRUE, Session.SESSION_TRANSACTED);
            Log.debug("ActiveMq Jms 连接成功... ");
            ActiveMqTpl activeMqTpl = new ActiveMqTpl(session);
            PLUGINS.put(key, activeMqTpl);
            return activeMqTpl;
        } catch (JMSException e) {
            throw new RuntimeException(String.format("error , ActiveMq连接失败... -> %s", e.getMessage()));
        }
    }

    public static ActiveMqTpl getActiveMq() {
        String brokenUrl = Prop.getStr("ants.activemq.broken-url");
        String username = Prop.getStr("ants.activemq.username");
        String password = Prop.getStr("ants.activemq.password");
        if (StrUtil.notBlank(brokenUrl, username, password)) {
            return getActiveMq(brokenUrl, username, password);
        }
        throw new RuntimeException("没有在配置文件中找到ActiveMq默认配置");
    }

}
