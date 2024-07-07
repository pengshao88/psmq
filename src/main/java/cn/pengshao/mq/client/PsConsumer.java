package cn.pengshao.mq.client;

import cn.pengshao.mq.model.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @Author: yezp
 * @date 2024/6/29 8:04
 */
@Slf4j
public class PsConsumer<T> {

    private final String id;
    final PsBroker broker;
    static AtomicInteger idGen = new AtomicInteger(0);

    public PsConsumer(PsBroker broker) {
        this.broker = broker;
        this.id = "CID" + idGen.getAndIncrement();
    }

    public <T> Message<T> recv(String topic) {
        return broker.recv(topic, id);
    }

    public void subscribe(String topic) {
        broker.sub(topic, id);
    }

    public void unsubscribe(String topic) {
        broker.unsub(topic, id);
    }

    public boolean ack(String topic, Integer offset) {
        return broker.ack(topic, id, offset);
    }

    public boolean ack(String topic, Message<?> message) {
        int offset = Integer.parseInt(message.getHeaders().get("X-offset"));
        return ack(topic, offset);
    }

    public void listen(String topic, PsListener<T> listener) {
        this.listener = listener;
        broker.addConsumer(topic, this);
    }

    @Getter
    private PsListener listener;

}
