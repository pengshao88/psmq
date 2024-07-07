package cn.pengshao.mq.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * mq for topic
 *
 * @Author: yezp
 * @date 2024/6/27 22:56
 */
@Slf4j
@AllArgsConstructor
public class PsMq {

//    private String topic;
//    private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();
//    private List<PsListener> listeners = new ArrayList<>();
//
//    public PsMq(String topic) {
//        this.topic = topic;
//    }
//
//    public boolean send(Message message) {
//        boolean offered = queue.offer(message);
//        log.info("send topic:{}, message:{}", topic, message);
//        listeners.forEach(listener -> listener.onMessage(message));
//        return offered;
//    }
//
//    /**
//     * 拉模式
//     *
//     * @param timeout 超时时间
//     * @return 消息
//     * @param <T> 泛型
//     */
//    @SneakyThrows
//    public <T> Message<T> poll(long timeout) {
//        return queue.poll(timeout, TimeUnit.MILLISECONDS);
//    }
//
//    public <T> void addListener(PsListener<T> listener) {
//        listeners.add(listener);
//    }

}
