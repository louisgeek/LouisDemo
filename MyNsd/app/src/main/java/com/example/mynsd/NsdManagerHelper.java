package com.example.mynsd;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.InetAddress;

public class NsdManagerHelper {
    private static final String SERVICE_TYPE = "_http._tcp.";
    private final NsdManager mNsdManager;
    private boolean isRegistered = false;
    private boolean isDiscovering = false;

    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener resolveListener;

    public NsdManagerHelper(Context context) {
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    private static final String TAG = "NsdManagerHelper";

    // 发现服务
    public void discoverServices(Context context) {
        NsdManager nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        //
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                callback.onDiscoveryEvent(ACTION_FOUND, serviceInfo);

                String serviceType = serviceInfo.getServiceType();
                String serviceName = serviceInfo.getServiceName();
                if (!SERVICE_TYPE.equals(serviceType)) {
                    Log.e(TAG, "onServiceFound: serviceType：" + serviceType);
                    return;
                }
                if ("MyNsdAndroidServer".equals(serviceName)) {
                    Log.d(TAG, "Same machine(Same IP): " + serviceName);
                    return;
                }
                if (serviceName.contains("MyNsdAndroidServer")) {
                    //
                    resolveService(serviceInfo);
                }

            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {

            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                callback.onDiscoveryEvent(ACTION_ERROR, null);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                callback.onDiscoveryEvent(ACTION_ERROR, null);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                isDiscovering = true;
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                isDiscovering = false;
            }
        };

        mNsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    // 解析服务
    private void resolveService(Context context, NsdServiceInfo serviceInfo) {
        resolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                String host = serviceInfo.getHost().getHostAddress();
                int port = serviceInfo.getPort();
                Log.d(TAG, "服务解析成功，IP：" + host + "，端口：" + port);
            }
        };
        NsdManager nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        nsdManager.resolveService(serviceInfo, resolveListener);
        nsdManager.stopServiceResolution();
    }

    // 停止发现
    public void stopDiscovery() {
        if (isDiscovering) {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            isDiscovering = false;
        }
    }

    // 取消注册
    public void unregisterService() {
        if (isRegistered) {
            mNsdManager.unregisterService(mRegistrationListener);
            isRegistered = false;
        }
    }

    // 是否正在注册服务
    public boolean isServiceRegistered() {
        return isRegistered;
    }

    // 是否正在发现服务
    public boolean isDiscoveryActive() {
        return isDiscovering;
    }

    // 错误信息转换
//    private String getErrorText(int errorCode) {
//        int resId = 0;
//        switch (errorCode) {
//            case NsdManager.NAME_CONFLICT:
//                return "NAME_CONFLICT";
//            case NsdManager.DELEGATION_FAILURE:
//                return "DELEGATION_FAILURE";
//            case NsdManager.OPERATION_CANCELLED:
//                return "OPERATION_CANCELLED";
//            case NsdManager.NETWORK_UNREACHABLE:
//                return "NETWORK_UNREACHABLE";
//            case NsdManager.UNKNOWN:
//                return "UNKNOWN";
//            default:
//                return "UNKNOWN ERROR";
//        }
//    }

    // 回调接口
    public interface Callback {
        void onResult(boolean success, String message);
    }

    public interface DiscoveryCallback {
        void onDiscoveryEvent(String action, NsdServiceInfo serviceInfo);
    }

    public interface ResolveCallback {
        void onResolveResult(NsdServiceInfo serviceInfo, String error);
    }

    private static final String ACTION_FOUND = "FOUND";
    private static final String ACTION_ERROR = "ERROR";
}