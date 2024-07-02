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

    private final String topic;
    private int index = 0;
    private final PsMessage<?>[] queue = new PsMessage[1024 * 10];
    private final Map<String, MessageSubscription> subscriptions = new HashMap<>();

    public MessageQueue(String topic) {
        this.topic = topic;
    }

    public int send(PsMessage<?> message) {
        if (index >= queue.length) {
            return -1;
        }

        queue[index++] = message;
        return index;
    }

    public PsMessage<?> recv(int ind) {
        if (ind <= index) {
            return queue[ind];
        }
        return null;
    }

    public void subscribe(MessageSubscription subscription) {
        String consumerId = subscription.getConsumerId();
        subscriptions.putIfAbsent(consumerId, subscription);
    }

    public void unsubscribe(MessageSubscription subscription) {
        String consumerId = subscription.getConsumerId();
        subscriptions.remove(consumerId);
    }

    public static void sub(MessageSubscription subscription) {
        MessageQueue messageQueue = QUEUE_MAP.get(subscription.getTopic());
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        messageQueue.subscribe(subscription);
    }

    public static void unsub(MessageSubscription subscription) {
        MessageQueue messageQueue = QUEUE_MAP.get(subscription.getTopic());
        if (messageQueue == null) {
            return;
        }
        messageQueue.unsubscribe(subscription);
    }

    public static int send(String topic, String consumerId, PsMessage<String> message) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        return messageQueue.send(message);
    }

    public static PsMessage<?> recv(String topic, String consumerId, int ind) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }

        if (messageQueue.subscriptions.containsKey(consumerId)) {
            return messageQueue.recv(ind);
        }
        throw new RuntimeException("subscriptions not found for topic/consumerId = "
                + topic + "/" + consumerId);
    }

    public static PsMessage<?> recv(String topic, String consumerId) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }

        if (messageQueue.subscriptions.containsKey(consumerId)) {
            int ind = messageQueue.subscriptions.get(consumerId).getOffset();
            return messageQueue.recv(ind);
        }
        throw new RuntimeException("subscriptions not found for topic/consumerId = "
                + topic + "/" + consumerId);
    }

    public static int ack(String topic, String consumerId, int offset) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        MessageSubscription messageSubscription = messageQueue.subscriptions.get(consumerId);
        if (messageSubscription == null) {
            throw new RuntimeException("subscriptions not found for topic/consumerId = "
                    + topic + "/" + consumerId);
        }

        if (offset > messageSubscription.getOffset() && offset <= messageQueue.index) {
            messageSubscription.setOffset(offset);
            return offset;
        }
        return -1;

    }
}
