package cn.pengshao.mq.demo;

import cn.pengshao.mq.client.*;
import cn.pengshao.mq.model.Message;
import cn.pengshao.mq.model.Stat;
import cn.pengshao.mq.server.MessageQueue;
import com.alibaba.fastjson.JSON;

import java.io.IOException;

/**
 * mq demo
 *
 * @Author: yezp
 * @date 2024/6/29 8:13
 */
public class PsMqDemo {

    public static void main(String[] args) throws IOException {
        long ids = 1;
        String topic = MessageQueue.TEST_TOPIC;
        PsBroker broker = PsBroker.getDEFAULT();

        PsProducer producer = broker.createProducer();
        PsConsumer consumer = broker.createConsumer(topic);
        for (int i = 0; i < 10; i++) {
            Order order = new Order(ids, "item" + i, 100.0 * i);
            producer.send(topic, new Message<>(ids++, JSON.toJSONString(order), null));
        }

        for (int i = 0; i < 10; i++) {
            Message<String> message = consumer.recv(topic);
            System.out.println("consume message=" + message);
            consumer.ack(topic, message);
        }
//        consumer.listen(topic, message -> {
//            // 这里处理消息
//            System.out.println(" onMessage => " + message);
//        });

        while (true) {
            char c = (char) System.in.read();
            if (c == 'q' || c == 'e') {
                System.out.println(" [exit] : " + c );
                break;
            }
            if (c == 'p') {
                Order order = new Order(ids, "item" + ids, 100 * ids);
                producer.send(topic, new Message<>(ids++, JSON.toJSONString(order), null));
                System.out.println("send ok => " + order);
            }
            if (c == 'c') {
                Message<Order> message = consumer.recv(topic);
                if (message == null) {
                    System.out.println("recv ok => message is null, topic=" + topic);
                } else {
                    System.out.println("recv ok => " + message);
                    consumer.ack(topic, message);
                }
            }
            if (c == 's') {
                Stat stat = consumer.stat(topic);
                System.out.println(stat);
            }
            if (c == 'a') {
                for (int i = 0; i < 10; i++) {
                    Order order = new Order(ids, "item" + ids, 100 * ids);
                    producer.send(topic, new Message<>(ids++, JSON.toJSONString(order), null));
                }
                System.out.println("send 10 orders...");
            }
        }
    }

}
