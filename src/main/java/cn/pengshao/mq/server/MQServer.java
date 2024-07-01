package cn.pengshao.mq.server;

import cn.pengshao.mq.model.PsMessage;
import cn.pengshao.mq.model.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MQ server:
 *
 * @Author: yezp
 * @date 2024/7/1 22:18
 */
@RestController
@RequestMapping("/psmq")
public class MQServer {

    @RequestMapping("/send")
    public Result<String> send(@RequestParam("topic") String topic,
                               @RequestParam("cid") String consumerId,
                               @RequestBody PsMessage<String> message) {
        return Result.ok();
    }

    @RequestMapping("/recv")
    public Result<PsMessage<?>> recv(@RequestParam("topic") String topic,
                                     @RequestParam("cid") String consumerId) {
        return Result.msg("");
    }

    @RequestMapping("/ack")
    public Result<String> ack(@RequestParam("topic") String topic,
                              @RequestParam("cid") String consumerId,
                              @RequestParam("offset") Integer offset) {
        return Result.ok();
    }

    @RequestMapping("/sub")
    public Result<String> subscribe(@RequestParam("topic") String topic,
                              @RequestParam("cid") String consumerId) {
        return Result.ok();
    }

    @RequestMapping("/unsub")
    public Result<String> unsubscribe(@RequestParam("topic") String topic,
                                @RequestParam("cid") String consumerId) {
        return Result.ok();
    }

}
