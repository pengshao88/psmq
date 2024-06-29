package cn.pengshao.mq.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * order model for test:
 *
 * @Author: yezp
 * @date 2024/6/29 8:12
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    private long id;
    private String item;
    private double price;
}