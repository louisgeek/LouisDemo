//package com.louis.mymedia3exo.util;
//
//import androidx.media3.common.util.UnstableApi;
//import androidx.media3.datasource.HttpDataSource;
//
//import java.util.Map;
//
//@UnstableApi
//public class LoggingHttpDataSourceFactory implements HttpDataSource.Factory {
//    private final HttpDataSource.RequestProperties defaultRequestProperties;
//
//    public LoggingHttpDataSourceFactory() {
//        defaultRequestProperties = new HttpDataSource.RequestProperties();
//    }
//
//    @Override
//    public LoggingDefaultHttpDataSource createDataSource() {
//        return new LoggingDefaultHttpDataSource(defaultRequestProperties);
//    }
//
//    @Override
//    public LoggingHttpDataSourceFactory setDefaultRequestProperties(Map<String, String> defaultRequestProperties) {
//        this.defaultRequestProperties.clearAndSet(defaultRequestProperties);
//        return this;
//    }
//}
