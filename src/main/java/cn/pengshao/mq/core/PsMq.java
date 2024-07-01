package cn.pengshao.mq.core;

import cn.pengshao.mq.model.PsMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * mq for topic
 *
 * @Author: yezp
 * @date 2024/6/27 22:56
 */
@Slf4j
@AllArgsConstructor
public class PsMq {

    private String topic;
    private LinkedBlockingQueue<PsMessage> queue = new LinkedBlockingQueue<>();
    private List<PsListener> listeners = new ArrayList<>();

    public PsMq(String topic) {
        this.topic = topic;
    }

    public boolean send(PsMessage message) {
        boolean offered = queue.offer(message);
        log.info("send topic:{}, message:{}", topic, message);
        listeners.forEach(listener -> listener.onMessage(message));
        return offered;
    }

    /**
     * 拉模式
     *
     * @param timeout 超时时间
     * @return 消息
     * @param <T> 泛型
     */
    @SneakyThrows
    public <T> PsMessage<T> poll(long timeout) {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    public <T> void addListener(PsListener<T> listener) {
        listeners.add(listener);
    }

}
