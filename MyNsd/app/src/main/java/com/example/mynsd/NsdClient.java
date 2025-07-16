package com.example.mynsd;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class NsdClient {
    private NsdManager mNsdManager;

    private static final String SERVICE_TYPE = "_http._tcp.";
    private NsdManager.DiscoveryListener discoveryListener;
    private void discoverServices(Context context) {
        NsdManager nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        //
        discoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {

            }

            @Override
            public void onDiscoveryStopped(String serviceType) {

            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {

            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {

            }
        };
        //服务发现
        nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD discoveryListener);
    }
    //停止发现
    public void stopServiceDiscovery(Context context) {
        NsdManager nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        //
        if (discoveryListener != null) {
            //服务发现停止
            nsdManager.stopServiceDiscovery(discoveryListener);
        }
    }


    private void resolveService(NsdServiceInfo serviceInfo) {
        NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                String host = serviceInfo.getHost().getHostAddress();
                int port = serviceInfo.getPort();
                Log.d("NSD", "解析成功，IP：" + host + "，端口：" + port);

                // 建立Socket连接（示例）
                new Thread(() -> {
                    try {
                        Socket socket = new Socket(host, port);
                        // 发送/接收数据...
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e("NSD", "解析失败，错误码：" + errorCode);
            }
        };

        mNsdManager.resolveService(serviceInfo, resolveListener);
    }
}