package cn.pengshao.mq.core;

import cn.pengshao.mq.model.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @Author: yezp
 * @date 2024/6/29 8:04
 */
@Slf4j
public class PsConsumer {

    final PsBroker broker;
    String topic;
    PsMq mq;

    public PsConsumer(PsBroker broker) {
        this.broker = broker;
    }

    public <T> Message<T> poll(long timeout) {
        return mq.poll(timeout);
    }

    public void subscribe(String topic) {
        this.topic = topic;
        mq = broker.findMq(topic);
        if (mq == null) {
            throw new RuntimeException("topic not found");
        }
    }

    public <T> void addListener(PsListener<T> listener) {
        mq.addListener(listener);
    }

}
