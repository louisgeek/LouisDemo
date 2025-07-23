package com.louis.mymedia3exo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapSorter {

    @SuppressWarnings("unchecked")
    public static Object sortRecursively(Object obj) {
        if (obj instanceof Map) {
            // 将 Map 转为 TreeMap 进行 key 排序
            Map<String, Object> sortedMap = new TreeMap<>();
            ((Map<?, ?>) obj).forEach((key, value) -> {
                // 递归处理 value
                sortedMap.put(key.toString(), sortRecursively(value));
            });
            return sortedMap;
        } else if (obj instanceof List) {
            // 处理 List 中的元素，特别是嵌套的 Map
            List<Object> sortedList = new ArrayList<>();
            for (Object item : (List<?>) obj) {
                // 递归处理 List 中的每个元素
                sortedList.add(sortRecursively(item));
            }
            return sortedList;
        } else {
            // 其他类型（String, Number, Boolean, null）直接返回
            return obj;
        }
    }

    // 辅助方法，方便调用
    @SuppressWarnings("unchecked")
    public static Map<String, Object> sortMap(Map<String, Object> map) {
        return (Map<String, Object>) sortRecursively(map);
    }
}
