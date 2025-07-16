package com.example.mynsd;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final String SERVICE_TYPE = "_http._tcp.";

    private NsdManagerHelper nsdHelper;
    private TextView txtResult;
    NsdClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        txtResult = findViewById(R.id.txtResult);
        Button btnDiscover = findViewById(R.id.btnDiscover);
        nsdHelper = new NsdManagerHelper(this);
         client = new NsdClient();
        btnDiscover.setOnClickListener(v -> {


            client.startDiscovery(getApplicationContext());

//            if (nsdHelper.isDiscoveryActive()) {
//                nsdHelper.stopDiscovery();
//                btnDiscover.setText("Discover Services");
//            } else {
//                discoverServices();
//                btnDiscover.setText("Stop Discovery");
//            }
        });
    }

//    private void discoverServices() {
//        nsdHelper.discoverServices(SERVICE_TYPE, (action, serviceInfo) -> {
//            runOnUiThread(() -> {
//                if (action.equals(NsdManagerHelper.ACTION_FOUND)) {
//                    // 解析服务以获取IP和端口
//                    nsdHelper.resolveService(serviceInfo, (resolvedService, error) -> {
//                        if (resolvedService != null) {
//                            String result = "Service Found:\n" +
//                                    "Name: " + resolvedService.getServiceName() + "\n" +
//                                    "IP: " + resolvedService.getHost().getHostAddress() + "\n" +
//                                    "Port: " + resolvedService.getPort();
//                            txtResult.setText(result);
//                        } else {
//                            Toast.makeText(ClientActivity.this, "Resolve Failed: " + error, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//        });
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        nsdHelper.stopDiscovery();
        client.stopDiscovery();
    }
}