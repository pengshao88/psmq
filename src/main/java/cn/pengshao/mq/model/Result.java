package cn.pengshao.mq.model;

import cn.pengshao.mq.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Result for MQServer.
 *
 * @Author: yezp
 * @date 2024/7/1 22:19
 */
@Data
@AllArgsConstructor
public class Result<T> {

    private int code; // 1==success, 0==fail
    private T data;

    public static Result<String> ok() {
        return new Result<>(ResultCode.SUCCESS.getCode(), "OK");
    }

    public static Result<String> ok(String msg) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg);
    }

    public static Result<Message<?>> msg(String msg) {
        return new Result<>(ResultCode.SUCCESS.getCode(), Message.create(msg, null));
    }

    public static Result<Message<?>> msg(Message<?> msg) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg);
    }

    public static Result<List<Message<?>>> msg(List<Message<?>> msg) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg);
    }

    public static Result<String> fail() {
        return new Result<>(ResultCode.FAIL.getCode(), "FAIL");
    }
}
