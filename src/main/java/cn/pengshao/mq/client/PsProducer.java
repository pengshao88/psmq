package cn.pengshao.mq.client;

import cn.pengshao.mq.model.Message;
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

    public boolean send(String topic, Message message) {
        return broker.send(topic, message);
    }
}
