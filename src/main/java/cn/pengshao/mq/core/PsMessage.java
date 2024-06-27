package cn.pengshao.mq.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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

    private Long id;
    private T body;
    private Map<String, String> headers; // 系统属性， X-version = 1.0
    //private Map<String, String> properties; // 业务属性

}
