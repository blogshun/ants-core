package com.ants.plugin.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author MrShun
 * @version 1.0
 */
public class ConsumerListener implements MessageListener {

    private ExecutorService threadPool = Executors.newFixedThreadPool(8);



    private String dest;

    private JmsConsumer target;

    public ConsumerListener(String dest, Object target) {
        this.dest = dest;
        this.target = (JmsConsumer)target;
    }

    @Override
    public void onMessage(final Message message) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        Thread.sleep(new Random().nextInt(2)*500);
                        TextMessage textMessage = (TextMessage)message;
                        if(null != textMessage){
                            target.received(textMessage.getText());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
