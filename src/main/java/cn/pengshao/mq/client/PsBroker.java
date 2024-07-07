package cn.pengshao.mq.client;

import cn.pengshao.common.http.HttpInvoker;
import cn.pengshao.common.thread.Scheduler;
import cn.pengshao.common.thread.ThreadUtils;
import cn.pengshao.mq.ResultCode;
import cn.pengshao.mq.model.Message;
import cn.pengshao.mq.model.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Description:
 *
 * @Author: yezp
 * @date 2024/6/29 7:55
 */
@Slf4j
public class PsBroker {

    @Getter
    public static PsBroker DEFAULT = new PsBroker();
    public static final String BROKER_URL = "http://localhost:8765/psmq";
//    todo createTopic


    static {
        init();
    }

    private static void init() {
        Scheduler scheduler = ThreadUtils.getDefault();
        scheduler.init(1);
        scheduler.schedule(() -> {
            MultiValueMap<String, PsConsumer<?>> consumers = getDEFAULT().getConsumers();
            consumers.forEach((topic, consumerList) -> {
                for (PsConsumer<?> consumer : consumerList) {
                    Message<?> recv = consumer.recv(topic);
                    if (recv == null) {
                        continue;
                    }

                    try {
                        consumer.getListener().onMessage(recv);
                        consumer.ack(topic, recv);
                    } catch (Exception ex) {
                        // TODO
                    }
                }
            });
        },  10, 1000);
    }

    public PsProducer createProducer() {
        return new PsProducer(this);
    }

    public PsConsumer createConsumer(String topic) {
        PsConsumer psConsumer = new PsConsumer(this);
        psConsumer.subscribe(topic);
        return psConsumer;
    }

    public boolean send(String topic, Message message) {
        log.info("====>>> send message: {}", message);
        Result<String> result = HttpInvoker.httpPost(JSON.toJSONString(message),
                BROKER_URL + "/send" + "?topic=" + topic, new TypeReference<>() {});
        log.info("====>>> send result: {}", result);
        return result.getCode() == ResultCode.SUCCESS.getCode();
    }

    public boolean sub(String topic, String consumerId) {
        log.info("====>>> sub topic: {}", topic);
        Result<String> result = HttpInvoker.httpGet(BROKER_URL + "/sub" + "?topic=" + topic + "&cid=" + consumerId,
                 new TypeReference<>() {});
        log.info("====>>> sub result: {}", result);
        return result.getCode() == ResultCode.SUCCESS.getCode();
    }

    public boolean unsub(String topic, String consumerId) {
        log.info("====>>> unsub topic: {}", topic);
        Result<String> result = HttpInvoker.httpGet(BROKER_URL + "/unsub" + "?topic=" + topic + "&cid=" + consumerId,
                new TypeReference<>() {});
        log.info("====>>> unsub result: {}", result);
        return result.getCode() == ResultCode.SUCCESS.getCode();
    }

    public <T> Message<T> recv(String topic, String consumerId) {
        log.info("====>>> recv topic/cid, {}/{}", topic, consumerId);
        Result<Message<String>> result = HttpInvoker.httpGet(BROKER_URL + "/recv" + "?topic=" + topic + "&cid=" + consumerId,
                new TypeReference<Result<Message<String>>>() {
                });
        log.info("====>>> recv result: {}", result);
        return (Message<T>) result.getData();
    }

    public boolean ack(String topic, String consumerId, Integer offset) {
        log.info("====>>> ack topic/cid/offset, {}/{}/{}", topic, consumerId, offset);
        Result<String> result = HttpInvoker.httpGet(
                BROKER_URL + "/ack" + "?topic=" + topic + "&cid=" + consumerId + "&offset=" + offset,
                new TypeReference<>() {});
        log.info("====>>> ack result: {}", result);
        return result.getCode() == ResultCode.SUCCESS.getCode();
    }

    @Getter
    private final MultiValueMap<String, PsConsumer<?>> consumers = new LinkedMultiValueMap<>();
    public void addConsumer(String topic, PsConsumer<?> consumer) {
        consumers.add(topic, consumer);
    }
}
