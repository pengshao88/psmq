package cn.pengshao.mq.store;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Description:
 *
 * @Author: yezp
 * @date 2024/7/9 22:42
 */
@Data
@AllArgsConstructor
public class Entry {

    int offset;
    int length;

}
