package cn.pengshao.mq.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Message Subscription
 *
 * @Author: yezp
 * @date 2024/7/1 22:31
 */
@Data
@AllArgsConstructor
public class Subscription {

    private String topic;
    private String consumerId;
    private int offset = -1;

}
