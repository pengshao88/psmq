package cn.pengshao.mq.core;

import cn.pengshao.mq.model.PsMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @Author: yezp
 * @date 2024/6/29 8:00
 */
@Slf4j
@AllArgsConstructor
public class PsProducer {

    private PsBroker broker;

    public boolean send(String topic, PsMessage message) {
        PsMq psMq = broker.findMq(topic);
        if (psMq == null) {
            throw new RuntimeException("topic not found");
        }
        return psMq.send(message);
    }
}
