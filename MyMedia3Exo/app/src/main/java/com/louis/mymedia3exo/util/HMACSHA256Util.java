package com.louis.mymedia3exo.util;


import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author xuan
 * @date 2025年01月02日 10:43
 */
public class HMACSHA256Util {

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    private static String secretKey = "353453434";
    private static String accesskey = "234234";

    // HMAC-SHA256生成Hex编码的签名
    public static String generateHMACSHA256Hex(long timestamp, String param) {
        try {
            // Step 1: 构造消息 accesskey + Timestamp + param
            String message = accesskey + timestamp + param;

            // Step 2: 生成HMAC-SHA256
            byte[] hmac = hmacSHA256(message, secretKey);

            // Step 3: 将HMAC-SHA256结果转换为Hex编码
            return bytesToHex(hmac);
        } catch (Exception e) {
            return "";
        }
    }

    // 使用给定的key生成HMAC-SHA256签名
    private static byte[] hmacSHA256(String message, String secretKey) throws Exception {
        // 创建 SecretKeySpec
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        // 获取 HMAC 实例
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);

        // 计算 HMAC
        return mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
    }

    // 将字节数组转换为Hex编码的字符串
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            // 高4位
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            // 低4位
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    // 测试方法
    public static void main(String[] args) {
        try {
            long timestamp = 1633045964l; // 这里填入你的Timestamp
            String signParam = "111111"; // 这里填入你的入参字符串

            String hmacHex = generateHMACSHA256Hex(timestamp, signParam);
            System.out.println("HMAC-SHA256 Hex: " + hmacHex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}