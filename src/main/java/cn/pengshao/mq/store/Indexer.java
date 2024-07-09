package cn.pengshao.mq.store;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * entry indexer.
 * 索引器
 *
 * @Author: yezp
 * @date 2024/7/9 22:43
 */
public class Indexer {

    static MultiValueMap<String, Entry> indexes = new LinkedMultiValueMap<>();
    static Map<String, Map<Integer, Entry>> mappings = new HashMap<>();

    public static void addEntry(String topic, int offset, int len) {
        System.out.println(" ===❀❀❀❀❀❀❀>>>> add entry(t/p/l):" + topic + "/" + offset + "/" + len);
        Entry entry = new Entry(len, offset);
        indexes.add(topic, entry);
        putMapping(topic, offset, entry);
    }

    private static void putMapping(String topic, int offset, Entry entry) {
        mappings.computeIfAbsent(topic, k -> new HashMap<>()).put(offset, entry);
    }

    public static List<Entry> getEntries(String topic) {
        return indexes.get(topic);
    }

    public static Entry getEntry(String topic, int offset) {
        Map<Integer, Entry> map = mappings.get(topic);
        return map == null ? null : map.get(offset);
    }
}
