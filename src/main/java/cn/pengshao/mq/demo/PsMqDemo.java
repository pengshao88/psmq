package cn.pengshao.mq.demo;

import cn.pengshao.mq.core.*;
import cn.pengshao.mq.model.PsMessage;

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
        String topic = "order";
        PsBroker broker = new PsBroker();
        broker.createMq(topic);

        PsProducer producer = broker.createProducer();
        for (int i = 0; i < 10; i++) {
            Order order = new Order(ids, "item" + i, 100.0 * i);
            producer.send("order", new PsMessage<>(ids++, order, null));
        }

        PsConsumer consumer = broker.createConsumer(topic);
        for (int i = 0; i < 10; i++) {
            System.out.println("consume message=" + consumer.poll(1000));
        }
        consumer.addListener((PsListener<Order>)
                message -> System.out.println("receive message=" + message));

        while (true) {
            char c = (char) System.in.read();
            if (c == 'q' || c == 'e') {
                break;
            }
            if (c == 'p') {
                Order order = new Order(ids, "item" + ids, 100 * ids);
                producer.send(topic, new PsMessage<>(ids ++, order, null));
                System.out.println("send ok => " + order);
            }
            if (c == 'c') {
                PsMessage<Order> message = consumer.poll(1000);
                System.out.println("poll ok => " + message);
            }
            if (c == 'a') {
                for (int i = 0; i < 10; i++) {
                    Order order = new Order(ids, "item" + ids, 100 * ids);
                    producer.send(topic, new PsMessage<>(ids ++, order, null));
                }
                System.out.println("send 10 orders...");
            }
        }
    }

}
