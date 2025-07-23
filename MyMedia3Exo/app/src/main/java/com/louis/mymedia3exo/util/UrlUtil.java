package com.louis.mymedia3exo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class UrlUtil {
    public static String toSortedQueryString(Map<String, Object> params) {
        // 使用 TreeMap 进行 key 排序


        Map<String, Object> sortedMap = MapSorter.sortMap(params);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(sortedMap);
    }

}
