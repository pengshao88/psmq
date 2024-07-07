package cn.pengshao.mq;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 *
 * @Author: yezp
 * @date 2024/7/6 8:18
 */
@Getter
public enum ResultCode {

    SUCCESS(1, "成功"),
    FAIL(0, "失败");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
