package com.example.mynsd;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.ServerSocket;


public class ServerActivity extends AppCompatActivity {
    private static final String TAG = "ServerActivity";
    private static final String SERVICE_NAME = "MyAndroidServer"; // 服务名称
    private static final String SERVICE_TYPE = "_http._tcp.";     // 服务类型

    private NsdManagerHelper nsdHelper;
    private int serverPort; // 服务监听端口（动态分配）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        Button btnRegister = findViewById(R.id.btnRegister);
//        nsdHelper = new NsdManagerHelper(this);

        // 获取动态端口
//        try {
//            serverPort = new ServerSocket(0).getLocalPort();
//            new ServerSocket(serverPort).close(); // 释放端口，但保留端口号
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        btnRegister.setOnClickListener(v -> {



//            if (nsdHelper.isServiceRegistered()) {
//                nsdHelper.unregisterService();
//                btnRegister.setText("Register Service");
//            } else {
//                registerService();
//                btnRegister.setText("Unregister Service");
//            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        nsdHelper.unregisterService();
//        server.stopServer();
    }
}