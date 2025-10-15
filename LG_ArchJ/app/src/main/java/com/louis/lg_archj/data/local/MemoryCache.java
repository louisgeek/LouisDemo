package com.louis.lg_archj.data.local;

import androidx.collection.LruCache;


public class MemoryCache {

    public static final long TIME_UNIT_SECOND = 1000;
    public static final long TIME_UNIT_MINUTE = 60 * TIME_UNIT_SECOND;
    private final LruCache<String, CacheEntry<Object>> lruCache;

    private static volatile MemoryCache sInstance;

    private static final long DEFAULT_TIME_TO_LIVE_MILLIS = 3 * TIME_UNIT_MINUTE;

    public MemoryCache() {
        lruCache = new LruCache<>(500);//最多500条目
    }

    public static MemoryCache getInstance() {
        if (sInstance == null) {
            synchronized (MemoryCache.class) {
                if (sInstance == null) {
                    sInstance = new MemoryCache();
                }
            }
        }
        return sInstance;
    }

    //----------------------------------------------------------

    public void put(String key, Object data) {
        put(key, data, DEFAULT_TIME_TO_LIVE_MILLIS);
    }

    public void put(String key, Object data, long timeToLiveMillis) {
        lruCache.put(key, new CacheEntry<>(data, timeToLiveMillis));
    }

    public void remove(String key) {
        lruCache.remove(key);
    }

    public Object get(String key) {
        CacheEntry<Object> item = lruCache.get(key);
        if (item == null) {
            return null;
        }
        if (item.isExpired()) {
            lruCache.remove(key);
            return null;
        }
        return item.value;
    }


    private static class CacheEntry<V> {
        Object value;
        long createTimeMillis;
        long timeToLiveMillis;

        CacheEntry(V value, long timeToLiveMillis) {
            this.value = value;
            this.createTimeMillis = System.currentTimeMillis();
            this.timeToLiveMillis = timeToLiveMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - createTimeMillis > timeToLiveMillis;
        }
    }
}
