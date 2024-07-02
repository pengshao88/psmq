package cn.pengshao.mq.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Result for MQServer.
 *
 * @Author: yezp
 * @date 2024/7/1 22:19
 */
@Data
@AllArgsConstructor
public class Result<T> {

    private static final int SUCCESS = 1;
    private static final int FAIL = 0;

    private int code; // 1==success, 0==fail
    private T data;

    public static Result<String> ok() {
        return new Result<>(SUCCESS, "OK");
    }

    public static Result<String> ok(String msg) {
        return new Result<>(SUCCESS, msg);
    }

    public static Result<PsMessage<?>> msg(String msg) {
        return new Result<>(SUCCESS, PsMessage.create(msg, null));
    }

    public static Result<PsMessage<?>> msg(PsMessage<?> msg) {
        return new Result<>(SUCCESS, msg);
    }

    public static Result<String> fail() {
        return new Result<>(FAIL, "FAIL");
    }
}
