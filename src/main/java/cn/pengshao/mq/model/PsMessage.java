package cn.pengshao.mq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * message model
 *
 * @Author: yezp
 * @date 2024/6/27 22:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsMessage<T> {

    static AtomicLong idGenerate = new AtomicLong(0);
    private long id;
    private T body;
    private Map<String, String> headers; // 系统属性， X-version = 1.0
    //private Map<String, String> properties; // 业务属性

    public static long getId() {
        return idGenerate.getAndIncrement();
    }

    public static PsMessage<?> create(String body, Map<String, String> headers) {
        return new PsMessage<>(getId(), body, headers);
    }

}
