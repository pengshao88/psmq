package cn.pengshao.mq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * stats for mq.
 *
 * @Author: yezp
 * @date 2024/7/9 23:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stat {

    private Subscription subscription;
    private int total;
    private int position;

}
