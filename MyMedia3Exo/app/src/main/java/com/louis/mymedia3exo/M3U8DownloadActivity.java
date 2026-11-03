package com.louis.mymedia3exo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class M3U8DownloadActivity extends AppCompatActivity {
    private M3U8DownloadService downloadService;
    private boolean serviceBound = false;
    private Handler handler = new Handler();
    private DownloadAdapter adapter;
    private RecyclerView recyclerView;
    private EditText urlEditText;
    private EditText filenameEditText;
    private Button addButton;
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            M3U8DownloadService.LocalBinder binder = (M3U8DownloadService.LocalBinder) service;
            downloadService = binder.getService();
            serviceBound = true;
            
            // 设置适配器并开始更新UI
            adapter = new DownloadAdapter(downloadService.getDownloadTasks());
            recyclerView.setAdapter(adapter);
            startUpdatingUI();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3u8_download);
        
        setupUI();
        
        // 启动并绑定服务
        Intent intent = new Intent(this, M3U8DownloadService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void setupUI() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        urlEditText = findViewById(R.id.urlEditText);
        filenameEditText = findViewById(R.id.filenameEditText);
        addButton = findViewById(R.id.addButton);
        
        addButton.setOnClickListener(v -> {
            String url = urlEditText.getText().toString().trim();
            String filename = filenameEditText.getText().toString().trim();
            
            if (!url.isEmpty() && !filename.isEmpty()) {
                if (!filename.endsWith(".mp4")) {
                    filename += ".mp4";
                }
                
                if (serviceBound) {
                    downloadService.addDownloadTask(url, filename);
                    Toast.makeText(M3U8DownloadActivity.this, "添加下载任务: " + filename, Toast.LENGTH_SHORT).show();
                    
                    urlEditText.setText("");
                    filenameEditText.setText("");
                }
            } else {
                Toast.makeText(M3U8DownloadActivity.this, "请输入URL和文件名", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void startUpdatingUI() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (serviceBound && adapter != null) {
                    adapter.updateData(downloadService.getDownloadTasks());
                }
                handler.postDelayed(this, 1000); // 每秒更新一次
            }
        }, 1000);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
        }
        handler.removeCallbacksAndMessages(null);
    }
    
    static class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
        private List<M3U8DownloadService.DownloadTask> tasks;
        
        public DownloadAdapter(List<M3U8DownloadService.DownloadTask> tasks) {
            this.tasks = tasks;
        }
        
        public void updateData(List<M3U8DownloadService.DownloadTask> newTasks) {
            this.tasks = newTasks;
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            M3U8DownloadService.DownloadTask task = tasks.get(position);
            String statusText = String.format("%s - %s %s", 
                task.getOutputFileName(),
                task.getStatus().name(),
                task.getStatus() == M3U8DownloadService.DownloadStatus.FAILED ? "(" + task.getErrorMessage() + ")" : ""
            );
            holder.textView.setText(statusText);
        }
        
        @Override
        public int getItemCount() {
            return tasks != null ? tasks.size() : 0;
        }
        
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}