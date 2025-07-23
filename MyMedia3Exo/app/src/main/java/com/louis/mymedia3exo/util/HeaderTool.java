package com.louis.mymedia3exo.util;

import android.os.Build;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class HeaderTool {
    public static Map<String, String> getHeaderMap(long timeStamp, String signature) {
        Locale locale = Locale.getDefault();
        // 示例：Redmi/Redmi K30(V2041)/Android(MagicUI)/Android10(API Level31)
        String osInfo = String.format("%s(%s)/%s/Android(%s)/Android %s(API Level%s)", Build.BRAND, Build.MANUFACTURER, Build.MODEL, "RomInfo", Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-PW-Algorithm", "HMAC-SHA256");
        headerMap.put("X-PW-AppId", "8");
        headerMap.put("X-PW-TerminalId", "6aecde7b-ce6b-3baa-ad03-5866848d9d9b");
        headerMap.put("X-PW-TimeZoneId", TimeZone.getDefault().getID());
        headerMap.put("X-PW-Os", osInfo);
        headerMap.put("X-PW-Language", locale.getLanguage() + "-" + locale.getCountry());
        headerMap.put("X-PW-AccessKey", "234234");
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Accept", "application/json");
        headerMap.put("Accept-Charset", "UTF-8");
        headerMap.put("X-PW-TerminalType", "Phone");
        headerMap.put("X-PW-AppVersion", "1.0.0.14");
        headerMap.put("X-PW-Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDExNzUxMDExNTU5ODI0MDQzIiwiY2xhaW1fdGVybWluYWxfaWQiOiI2YWVjZGU3Yi1jZTZiLTNiYWEtYWQwMy01ODY2ODQ4ZDlkOWIiLCJjbGFpbV90ZXJtaW5hbF90eXBlIjoiUGhvbmUiLCJleHAiOjE3NTI3NDkyODh9.UsCNc-3baNGI6scPH2betdt1NW-mkEp0ziRjoP2zwyg");
        headerMap.put("X-PW-Timestamp", timeStamp + "");
        headerMap.put("X-PW-Signature", signature);
        return headerMap;
    }


}
