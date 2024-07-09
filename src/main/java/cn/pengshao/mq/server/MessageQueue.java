package cn.pengshao.mq.server;

import cn.pengshao.mq.model.Message;
import cn.pengshao.mq.model.Stat;
import cn.pengshao.mq.model.Subscription;
import cn.pengshao.mq.store.Entry;
import cn.pengshao.mq.store.Indexer;
import cn.pengshao.mq.store.Store;
import lombok.Getter;
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
//    private final Message<?>[] queue = new Message[1024 * 10];
    private final Map<String, Subscription> subscriptions = new HashMap<>();
    @Getter
    private Store store = null;

    public MessageQueue(String topic) {
        this.topic = topic;
        store = new Store(topic);
        store.init();
    }

    public static List<Message<?>> batch(String topic, String consumerId, int size) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }

        if (messageQueue.subscriptions.containsKey(consumerId)) {
            int offset = messageQueue.subscriptions.get(consumerId).getOffset();
            int nextOffset = 0;
            if (offset > -1) {
                Entry entry = Indexer.getEntry(topic, offset);
                if (entry != null) {
                    nextOffset = offset + entry.getLength();
                }
            }

            List<Message<?>> result = new ArrayList<>();
            Message<?> recv = messageQueue.recv(nextOffset);
            while (recv != null) {
                result.add(recv);
                if (result.size() >= size) {
                    break;
                }

                // 待验证
                Entry entry = Indexer.getEntry(topic, nextOffset);
                if (entry != null) {
                    nextOffset = nextOffset + entry.getLength();
                    recv = messageQueue.recv(nextOffset);
                } else {
                    recv = null;
                }
            }
            log.info(" ===>> batch: topic/cid/size = " + topic + "/" + consumerId + "/" + result.size());
            log.info(" ===>> last message:{}", recv);
        }
        throw new RuntimeException("subscriptions not found for topic/consumerId = " + topic + "/" + consumerId);
    }

    public static Stat stat(String topic, String consumerId) {
        MessageQueue queue = QUEUE_MAP.get(topic);
        Subscription subscription = queue.subscriptions.get(consumerId);
        return new Stat(subscription, queue.getStore().total(), queue.getStore().pos());
    }

    public int send(Message<String> message) {
        int offset = store.pos();
        message.getHeaders().put("X-offset", String.valueOf(offset));
        store.write(message);
        return offset;
    }

    public Message<?> recv(int offset) {
        return store.read(offset);
    }

    public void subscribe(Subscription subscription) {
        String consumerId = subscription.getConsumerId();
        subscriptions.putIfAbsent(consumerId, subscription);
    }

    public void unsubscribe(Subscription subscription) {
        String consumerId = subscription.getConsumerId();
        subscriptions.remove(consumerId);
    }

    public static void sub(Subscription subscription) {
        MessageQueue messageQueue = QUEUE_MAP.get(subscription.getTopic());
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        messageQueue.subscribe(subscription);
    }

    public static void unsub(Subscription subscription) {
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

    public static Message<?> recv(String topic, String consumerId, int offset) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }

        if (messageQueue.subscriptions.containsKey(consumerId)) {
            return messageQueue.recv(offset);
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
            int offset = messageQueue.subscriptions.get(consumerId).getOffset();
            int nextOffset = 0;
            if (offset > -1) {
                Entry entry = Indexer.getEntry(topic, offset);
                if (entry != null) {
                    nextOffset = offset + entry.getLength();
                }
            }

            Message<?> recv = messageQueue.recv(nextOffset);
            log.info(" ===>> recv: topic/cid/offset = " + topic + "/" + consumerId + "/" + offset + " result=" + recv);
            return recv;
        }
        throw new RuntimeException("subscriptions not found for topic/consumerId = "
                + topic + "/" + consumerId);
    }

    public static int ack(String topic, String consumerId, int offset) {
        MessageQueue messageQueue = QUEUE_MAP.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        Subscription subscription = messageQueue.subscriptions.get(consumerId);
        if (subscription == null) {
            throw new RuntimeException("subscriptions not found for topic/consumerId = "
                    + topic + "/" + consumerId);
        }

        if (offset > subscription.getOffset() && offset < Store.LEN) {
            log.info(" ===>> ack: topic/cid/offset = " + topic + "/" + consumerId + "/" + offset);
            subscription.setOffset(offset);
            return offset;
        }
        return -1;

    }
}
