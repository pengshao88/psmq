package cn.pengshao.mq.server;

import cn.pengshao.mq.model.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * queues
 *
 * @Author: yezp
 * @date 2024/7/1 22:40
 */
@Slf4j
public class MessageQueue {

    public static final Map<String, MessageQueue> QUEUE_MAP = new HashMap<>();
    public static final String TEST_TOPIC = "cn.pengshao.test";
    static {
        QUEUE_MAP.put(TEST_TOPIC, new MessageQueue(TEST_TOPIC));
    }

    private final String topic;
    private int index = 0;
    private final Message<?>[] queue = new Message[1024 * 10];
    private final Map<String, MessageSubscription> subscriptions = new HashMap<>();

    public MessageQueue(String topic) {
        this.topic = topic;
    }

    public static List<Message<?>> batch(String topic, String consumerId, int size) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }

        if (messageQueue.subscriptions.containsKey(consumerId)) {
            int ind = messageQueue.subscriptions.get(consumerId).getOffset();
            int offset = ind + 1;
            List<Message<?>> result = new ArrayList<>();
            Message<?> recv = messageQueue.recv(offset);
            while (recv != null) {
                result.add(recv);
                if (result.size() >= size) {
                    break;
                }

                offset++;
                recv = messageQueue.recv(offset);
            }

            log.info(" ===>> batch: topic/cid/size = " + topic + "/" + consumerId + "/" + result.size());
            log.info(" ===>> last message:{}", recv);
        }
        throw new RuntimeException("subscriptions not found for topic/consumerId = " + topic + "/" + consumerId);
    }

    public int send(Message<?> message) {
        if (index >= queue.length) {
            return -1;
        }

        message.getHeaders().put("X-offset", String.valueOf(index));
        queue[index++] = message;
        return index;
    }

    public Message<?> recv(int ind) {
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

    public static int send(String topic, Message<String> message) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        return messageQueue.send(message);
    }

    public static Message<?> recv(String topic, String consumerId, int ind) {
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

    public static Message<?> recv(String topic, String consumerId) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }

        if (messageQueue.subscriptions.containsKey(consumerId)) {
            int ind = messageQueue.subscriptions.get(consumerId).getOffset();
            return messageQueue.recv(ind + 1);
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
