package cn.pengshao.mq.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @Author: yezp
 * @date 2024/6/29 7:55
 */
@Slf4j
public class PsBroker {

    private final Map<String, PsMq> mqMapping = new ConcurrentHashMap<>(64);

    public PsMq findMq(String topic) {
        return mqMapping.get(topic);
    }

    public PsMq createMq(String topic) {
        log.info("create topic:{}", topic);
        return mqMapping.putIfAbsent(topic, new PsMq(topic));
    }

    public PsProducer createProducer() {
        return new PsProducer(this);
    }

    public PsConsumer createConsumer(String topic) {
        PsConsumer psConsumer = new PsConsumer(this);
        psConsumer.subscribe(topic);
        return psConsumer;
    }
}
