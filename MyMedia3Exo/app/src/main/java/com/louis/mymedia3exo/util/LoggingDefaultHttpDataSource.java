//package com.louis.mymedia3exo.util;
//
//import android.net.Uri;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.media3.common.util.UnstableApi;
//import androidx.media3.datasource.DataSpec;
//import androidx.media3.datasource.DefaultHttpDataSource;
//
//import java.io.UnsupportedEncodingException;
//
//@UnstableApi
//public class LoggingDefaultHttpDataSource extends DefaultHttpDataSource {
//    private static final String TAG = "LoggingDefaultHttpDataS";
//
//    public LoggingDefaultHttpDataSource(@Nullable RequestProperties defaultRequestProperties) {
//        super(null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, false, defaultRequestProperties);
//    }
//
//    @Override
//    public long open(DataSpec dataSpec) throws HttpDataSourceException {
//        long result = super.open(dataSpec);
//        Uri uri = dataSpec.uri;
//        Log.d(TAG, "zzz open uri: " + uri + " result: " + dataSpec);
//        return result;
//    }
//
//    @Override
//    public int read(byte[] buffer, int offset, int length) throws HttpDataSourceException {
//        int bytesRead = super.read(buffer, offset, length);
//        if (bytesRead > 0) {
//            Log.d(TAG, "zzz read bytesRead: " + bytesRead + " length="+length);
//            String result = null;
//            try {
//                result = new String(buffer, offset, bytesRead, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            Log.d(TAG, "zzz read result: " + result);
//        }
//        return bytesRead;
//    }
//
//    @Override
//    public void close() throws HttpDataSourceException {
//        super.close();
//    }
//}
