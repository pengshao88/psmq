package cn.pengshao.mq.server;

import cn.pengshao.mq.model.PsMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * queues
 *
 * @Author: yezp
 * @date 2024/7/1 22:40
 */
public class MessageQueue {

    public static final Map<String, MessageQueue> QUEUE_MAP = new HashMap<>();
    private static final String TEST_TOPIC = "cn.pengshao.test";
    static {
        QUEUE_MAP.put(TEST_TOPIC, new MessageQueue(TEST_TOPIC));
    }

    private String topic;
    private int index = 0;
    private PsMessage<?>[] queue = new PsMessage[1024 * 10];
    private final Map<String, MessageSubscription> subscriptions = new HashMap<>();

    public MessageQueue(String topic) {
        this.topic = topic;
    }

}
