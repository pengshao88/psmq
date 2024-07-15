package cn.pengshao.mq.store;

import cn.pengshao.mq.model.Message;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Message store:
 *
 * @Author: yezp
 * @date 2024/7/9 22:27
 */
public class Store {

    private String topic;

    public static final int LEN = 1024 * 10;

    public Store(String topic) {
        this.topic = topic;
    }

    @Getter
    MappedByteBuffer mappedByteBuffer = null;

    @SneakyThrows
    public void init() {
        File file = new File(topic + ".dat");
        if (!file.exists()) {
            file.createNewFile();
        }

        Path path = Paths.get(file.getAbsolutePath());
        FileChannel channel = (FileChannel) Files.newByteChannel(path,
                StandardOpenOption.READ, StandardOpenOption.WRITE);
        mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, LEN);

        // todo 1、消费端 -> 读取索引
        // 判断是否有数据
        // 读前10位，转成int=len，看是不是大于0，往后翻len的长度，就是下一条记录，
        // 重复上一步，一直到0为止，找到数据结尾
        // mappedByteBuffer.position(init_pos);
        ByteBuffer buffer = mappedByteBuffer.asReadOnlyBuffer();
        byte[] header = new byte[10];
        buffer.get(header);
        int pos = 0;
        while (header[9] > 0) {
            String trim = new String(header, StandardCharsets.UTF_8).trim();
            System.out.println(trim);
            int len = Integer.parseInt(trim) + 10;
            Indexer.addEntry(topic, pos, len);

            pos += len;
            System.out.println(" next=" + pos);
            buffer.position(pos);
            buffer.get(header);
        }

        buffer = null;
        System.out.println("init pos = " + pos);
        mappedByteBuffer.position(pos);
        // todo 2、如果总数据 > 10M，使用多个数据文件的list来管理持久化数据
        // 需要创建第二个数据文件，怎么来管理多个数据文件。
    }

    public int write(Message<String> message) {
        int position = mappedByteBuffer.position();
        System.out.println(" write pos -> " + position);
        String msg = JSON.toJSONString(message);

        int len = msg.getBytes(StandardCharsets.UTF_8).length;
        String format = String.format("%010d", len);
        msg = format + msg;
        len = len + 10;
        Indexer.addEntry(topic, position, len);
        mappedByteBuffer.put(StandardCharsets.UTF_8.encode(msg));
        return position;
    }

    public int pos() {
        return mappedByteBuffer.position();
    }

    public Message<String> read(int offset) {
        ByteBuffer readOnlyBuffer = mappedByteBuffer.asReadOnlyBuffer();
        Entry entry = Indexer.getEntry(topic, offset);
        if (entry == null) {
            System.out.println(" read store no data");
            return null;
        }

        readOnlyBuffer.position(entry.getOffset() + 10);
        System.out.println(" read store -> " + readOnlyBuffer.position() + "/"
                + entry.getOffset() + "/" + entry.getLength());
        int len = entry.length - 10;
        byte[] bytes = new byte[len];
        readOnlyBuffer.get(bytes, 0, len);
        String json = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(" read store data -> " + json);
        return JSON.parseObject(json, new TypeReference<>() {
        });
    }

    public int total() {
        return Indexer.getEntries(topic).size();
    }

}
