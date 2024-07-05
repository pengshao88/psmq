package cn.pengshao.mq.core;

import cn.pengshao.mq.model.Message;

/**
 * message listener
 * push model
 *
 * @Author: yezp
 * @date 2024/6/27 22:55
 */
public interface PsListener<T> {

    void onMessage(Message<T> message);

}
